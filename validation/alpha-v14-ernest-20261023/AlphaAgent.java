import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Separate diagnostic agent, prepared by Ernest Chua with OpenAI Codex assistance. */
public class AlphaAgent {
    static final List<String> report = new ArrayList<>();
    static Path output;
    static Stage stage;

    public static void premain(String arg, Instrumentation unused) {
        output = Path.of(arg);
        Thread worker = new Thread(AlphaAgent::audit, "completion-native-check");
        worker.setDaemon(true);
        worker.start();
    }
    static void audit() {
        boolean passed=false;
        try {
            Files.createDirectories(output);
            log("Released v1.4 alpha: runtime="+System.getProperty("java.runtime.version")+"; OS="+System.getProperty("os.name"));
            for(int i=0;i<100 && stage==null;i++) {
                try {stage=fx(() -> Window.getWindows().stream().filter(w -> w instanceof Stage && w.isShowing()
                    && w.getScene()!=null && w.getScene().lookup("#commandTextField")!=null).map(Stage.class::cast).findFirst().orElse(null));}
                catch(IllegalStateException starting) {}
                if(stage==null)Thread.sleep(200);
            }
            require(stage!=null,"No production window");
            fx(() -> {
                for(Screen s:Screen.getScreens())log("Screen="+s.getBounds()+"; scale="+s.getOutputScaleX());return null;});
            Thread.sleep(300);
            if(Boolean.getBoolean("alpha.reload")) {
                byte[] before=Files.readAllBytes(Path.of("data/ponhub.json"));
                command("list r/student",true);observePeopleScrollbar();
                command("showlesson lid/L1 d/2026-10-05",true);command("history 2 lid/L1",true);
                require(fx(() -> visibleLabels()).contains("absent"),"Reload lost retained absence");
                require(Arrays.equals(before,Files.readAllBytes(Path.of("data/ponhub.json"))),"Read-only restart checks wrote data");
                log("Restart: IDs, lesson, roster and retained attendance reload=PASS");
            } else {
                require(!Files.exists(Path.of("data/ponhub.json")),"Not a fresh JAR folder");
                command("add r/tutor n/Mei Lim p/92345678",true);
                command("add r/student n/Alex Tan l/P1 pp/91234567",true);
                command("add r/student n/Jamie Tan l/S2 pp/91234567",true);
                command("addlesson d/Mon st/1600 et/1700 s/Math tu/Mei Lim rm/R1",true);
                command("list r/student",true);observePeopleScrollbar();
                command("enrol 1 lid/L1",true);command("enrol 2 lid/L1",true);
                command("mark 1 lid/L1 d/2026-10-05 s/present",true);command("mark 2 lid/L1 d/2026-10-05 s/absent",true);
                require(records()==2,"Attendance keys were conflated");
                command("mark 1 lid/L1 d/2026-10-05 s/absent",true);require(records()==2,"Correction allocated another key");
                byte[] before=Files.readAllBytes(Path.of("data/ponhub.json"));
                var stamp=Files.getLastModifiedTime(Path.of("data/ponhub.json"));
                command("mark 1 lid/L1 d/2026-10-05 s/absent",true);
                require(Arrays.equals(before,Files.readAllBytes(Path.of("data/ponhub.json"))) && stamp.equals(Files.getLastModifiedTime(Path.of("data/ponhub.json"))),"Identical mark wrote data");
                for(String bad:List.of("mark 1 lid/L1 d/2026-10-06 s/present","mark 1 lid/L1 d/2026-02-30 s/present",
                    "mark 0 lid/L1 d/2026-10-05 s/present","mark 1 lid/L999 d/2026-10-05 s/present")) {
                    command(bad,false);require(Arrays.equals(before,Files.readAllBytes(Path.of("data/ponhub.json"))),"Invalid command changed bytes");
                }
                command("unenrol 1 lid/L1",true);require(records()==2,"Unenrolment deleted retained history");
                command("history 1 lid/L1",true);require(fx(() -> visibleLabels()).contains("former"),"Former-member context missing");
                command("mark 1 lid/L1 d/2026-10-12 s/present",false);require(records()==2,"New record allowed for former member");
                command("mark 1 lid/L1 d/2026-10-05 s/present",true);require(records()==2,"Former-member correction failed");
                command("showlesson lid/L1 d/2026-10-05",true);
                command("unmark 1 lid/L1 d/2026-10-05",true);require(records()==1,"Unmark removed another Student's record");
                before=Files.readAllBytes(Path.of("data/ponhub.json"));command("unmark 1 lid/L1 d/2026-10-05",true);
                require(Arrays.equals(before,Files.readAllBytes(Path.of("data/ponhub.json"))),"Repeated unmark changed data");
                command("deletelesson lid/L1",false);require(Arrays.equals(before,Files.readAllBytes(Path.of("data/ponhub.json"))),"Referenced lesson deletion changed data");
                // Induce a reversible save failure only in this disposable JAR folder.
                Path file=Path.of("data/ponhub.json"), backup=Path.of("data/fixture-backup.json");
                Files.move(file,backup);Files.createDirectory(file);
                command("add r/parent n/Pat Tan p/91234567",false);
                require(new com.fasterxml.jackson.databind.ObjectMapper().readTree(Files.readString(backup)).get("people").size()==3,"Failure changed saved source");
                require(fx(() -> ((javafx.scene.control.ListView<?>)stage.getScene().lookup("#personListView")).getItems().size())==2,"Failed save changed filtered view");
                Files.delete(file);Files.move(backup,file);command("add r/parent n/Pat Tan p/91234567",true);
                require(new com.fasterxml.jackson.databind.ObjectMapper().readTree(Files.readString(file)).get("personCounters").get("PARENT").asInt()==1,"Retry lost the first Parent ID");
                log("Attendance independent keys/corrections/no-op/invalid-date/selector/former-member/unmark and failed-save/retry=PASS");
            }
            log("Commands exercise the real packaged CommandBox/ActionEvent/session/storage; text-only evidence.");
            log("This does not certify all features/platforms or count as independent testing of Ernest's history UI.");
            passed=true;
        }catch(Throwable e){log("FAIL="+e);for(StackTraceElement x:e.getStackTrace())log("  "+x);}
        finally {
            log("Released-JAR alpha checks="+(passed?"PASS":"FAIL"));
            try{Files.write(output.resolve("result.txt"),report);}catch(Exception ignored){}
            try{fx(() -> {stage.hide();Platform.exit();return null;});}catch(Exception ignored){}
        }
    }
    static void command(String text,boolean success)throws Exception {
        fx(() -> {input().setText(text);javafx.event.Event.fireEvent(input(),new javafx.event.ActionEvent());return null;});
        Thread.sleep(100);boolean actual=fx(() -> input().getText().isEmpty());
        log("Command="+text+"; success="+actual+"; feedback="+fx(() -> result().getText()).replace("\n"," | "));
        require(actual==success,"Unexpected outcome for "+text);
    }
    static int records()throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(Files.readString(Path.of("data/ponhub.json"))).get("attendance").size();
    }
    static String visibleLabels(){return java.util.stream.Stream.concat(stage.getScene().getRoot().lookupAll(".label").stream(),
        stage.getScene().getRoot().lookupAll(".list-cell").stream()).filter(n -> n.isVisible() && n instanceof javafx.scene.control.Labeled)
        .map(n -> ((javafx.scene.control.Labeled)n).getText()).filter(java.util.Objects::nonNull).reduce("",(a,b)->a+" | "+b).toLowerCase();}
    static void observePeopleScrollbar() throws Exception {
        log("People horizontal scrollbars=" + fx(() -> stage.getScene().lookup("#personListView")
                .lookupAll(".scroll-bar").stream().map(javafx.scene.control.ScrollBar.class::cast)
                .filter(bar -> bar.getOrientation() == javafx.geometry.Orientation.HORIZONTAL)
                .map(bar -> "visible=" + bar.isVisible() + "; max=" + bar.getMax()).toList()));
    }
    static TextField input() { return (TextField) stage.getScene().lookup("#commandTextField"); }
    static TextArea result() { return (TextArea) stage.getScene().lookup("#resultDisplay"); }
    static <T> T fx(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action); Platform.runLater(task); return task.get(10,TimeUnit.SECONDS);
    }
    static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    static void log(String text) { report.add(text); System.out.println(text); }
}
