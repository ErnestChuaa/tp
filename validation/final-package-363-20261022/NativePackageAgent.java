import java.awt.image.BufferedImage;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Native exact-JAR checks explicitly requested by the user; Ernest Chua with Codex assistance. */
public class NativePackageAgent extends FinalPackageAgent {
    static Robot robot;
    public static void premain(String arg, Instrumentation instrumentation) {
        output = Path.of(arg);
        Thread worker = new Thread(NativePackageAgent::nativeAudit, "native-final-package-audit");
        worker.setDaemon(true);
        worker.start();
    }
    static void nativeAudit() {
        boolean passed = false;
        try {
            Files.createDirectories(output);
            log("Time=" + Instant.now() + "; Java=" + System.getProperty("java.runtime.version")
                    + "; vendor=" + System.getProperty("java.vendor") + "; OS="
                    + System.getProperty("os.name") + "; arch=" + System.getProperty("os.arch"));
            for (int i = 0; i < 100 && stage == null; i++) {
                try { stage = fx(() -> Window.getWindows().stream().filter(w -> w instanceof Stage
                        && w.isShowing() && w.getScene() != null
                        && w.getScene().lookup("#commandTextField") != null).map(w -> (Stage) w)
                        .findFirst().orElse(null)); } catch (IllegalStateException starting) { }
                if (stage == null) Thread.sleep(200);
            }
            require(stage != null, "Production window did not show");
            fx(() -> { robot = new Robot(); stage.toFront(); stage.requestFocus();
                for (Screen s : Screen.getScreens()) log("Screen=" + s.getBounds() + "; outputScale=" + s.getOutputScaleX());
                log("JavaFX=" + System.getProperty("javafx.runtime.version")); return null; });
            Thread.sleep(400);
            require(!Files.exists(Path.of("data/ponhub.json")), "Fresh native fixture already has operational data");
            compact("native-fresh-startup"); screen("01-native-startup");
            command("add r/tutor n/Mei Lim p/92345678");
            command("add r/student n/Alex Tan l/S2 pp/91234567");
            command("add r/student n/Jamie Tan l/S2 pp/91234567");
            command("addlesson d/Mon st/1600 et/1730 s/Math tu/Mei Lim rm/R1");
            command("list r/student"); command("enrol 1 lid/L1");
            command("mark 1 lid/L1 d/2026-10-05 s/present");
            fx(() -> { stage.setMaximized(true); return null; }); Thread.sleep(300);
            compact("native-maximized"); screen("02-native-maximized");
            double initial = height(); drag(.55);
            require(height() > initial + 80, "Native drag did not expand feedback");
            double expanded = height(); command("list r/student"); command("help search");
            require(Math.abs(height() - expanded) <= 1, "Commands changed native-chosen divider");
            screen("03-native-expanded-help"); drag(.01); compact("native-drag-shrunk");
            command("help search"); nativeScrollEnd();
            fx(() -> { stage.setMaximized(false); stage.setWidth(450); stage.setHeight(360); return null; });
            Thread.sleep(300); compact("native-minimum-window");
            command("help search"); nativeScrollEnd(); screen("04-native-minimum-help-end");
            command("search c/lesson s/" + "Advanced Mathematics ".repeat(14).strip());
            command("help");
            hover("lessonContext", "05-native-lesson-context");
            hover("lessonSummary", "06-native-lesson-summary-zero");
            command("lessons"); hover("lessonContext", "07-native-lesson-context-live");
            hover("lessonSummary", "08-native-lesson-summary-live");
            command("search c/student n/Alex");
            fx(() -> { people().getSelectionModel().select(0); return null; });
            command("history 1 lid/L1");
            hover("historyContext", "09-native-history-context");
            hover("historySummary", "10-native-history-summary-one");
            command("mark 1 lid/L1 d/2026-10-12 s/absent");
            command("help");
            hover("historySummary", "11-native-history-summary-live");
            byte[] saved = Files.readAllBytes(Path.of("data/ponhub.json"));
            Object selected = fx(() -> people().getSelectionModel().getSelectedItem());
            List<?> visible = fx(() -> List.copyOf(people().getItems()));
            List<?> history = fx(() -> List.copyOf(((ListView<?>) node("historyListView")).getItems()));
            String context = fx(() -> ((Label) node("historyContext")).getText());
            String draft = "editlesson lid/L1 s/Unfinished";
            fx(() -> { input().setText(draft); input().requestFocus(); return null; });
            click(menuButton("File")); require(fx(() -> menu("File").isShowing()), "Native File popup did not open");
            screen("12-native-file-ordinary"); key(KeyCode.DOWN); screen("13-native-file-keyboard-highlight");
            key(KeyCode.RIGHT); require(fx(() -> menu("Help").isShowing()), "Keyboard Right did not open Help");
            key(KeyCode.ESCAPE); require(fx(() -> !menu("Help").isShowing()), "Escape did not dismiss Help");
            click(menuButton("Help")); require(fx(() -> menu("Help").isShowing()), "Native Help popup did not open");
            screen("14-native-help-ordinary"); key(KeyCode.DOWN); screen("15-native-help-keyboard-highlight");
            key(KeyCode.ENTER); require(fx(() -> !menu("Help").isShowing()), "Enter did not invoke Help");
            require(fx(() -> result().getText().startsWith("PonHub commands available")), "Native menu Help not invoked");
            require(fx(() -> input().getText().equals(draft)), "Native Help menu lost draft");
            for (Node target : fx(() -> List.of(input(), result()))) {
                command("help search");
                fx(() -> { input().setText(draft); return null; });
                click(target); key(KeyCode.F1);
                require(fx(() -> result().getText().startsWith("PonHub commands available")), "Native F1 did not invoke Help");
                require(fx(() -> input().getText().equals(draft)), "Native F1 lost draft");
            }
            require(fx(() -> people().getSelectionModel().getSelectedItem() == selected), "Native Help lost selection");
            require(fx(() -> visible.equals(people().getItems())), "Native Help changed people filter");
            require(fx(() -> history.equals(((ListView<?>) node("historyListView")).getItems())), "Native Help changed history");
            require(fx(() -> context.equals(((Label) node("historyContext")).getText())), "Native Help changed context");
            require(fx(() -> node("historyListView").getParent().isVisible()), "Native Help changed active pane");
            require(Arrays.equals(saved, Files.readAllBytes(Path.of("data/ponhub.json"))), "Native read-only checks changed data");
            compact("native-help-draft-history"); screen("16-native-help-draft-history");
            log("PASS: real divider drags; all four real pointer hovers and live updates; File/Help popups; Down/Right/Escape/Enter; native F1 from input/feedback; preserved draft/filter/selection/history/context/data bytes");
            log("Limits: automated JavaFX Robot on Windows at observed 125%; not independent human acceptance or other OS/scales");
            passed = true;
        } catch (Throwable e) {
            log("FAIL=" + e); for (StackTraceElement line : e.getStackTrace()) log("  " + line);
            try { screen("failure"); } catch (Throwable ignored) { }
        } finally {
            log("Exact-JAR native affected checks=" + (passed ? "PASS" : "FAIL"));
            try { Files.write(output.resolve("result.txt"), report); } catch (Exception ignored) { }
            try { fx(() -> { robot.mouseRelease(MouseButton.PRIMARY); stage.hide(); javafx.application.Platform.exit(); return null; }); }
            catch (Exception ignored) { }
        }
    }
    static Menu menu(String text) { return ((MenuBar) node("menuBar")).getMenus().stream().filter(m -> m.getText().equals(text)).findFirst().orElseThrow(); }
    static Node menuButton(String text) throws Exception {
        return fx(() -> node("menuBar").lookupAll(".menu-button").stream()
                .filter(n -> n.lookup(".label") instanceof Label l && l.getText().equals(text)).findFirst().orElseThrow());
    }
    static void click(Node node) throws Exception {
        fx(() -> { Bounds b = node.localToScreen(node.getBoundsInLocal()); robot.mouseMove(b.getCenterX(), b.getCenterY());
            robot.mouseClick(MouseButton.PRIMARY); return null; }); Thread.sleep(180);
    }
    static void key(KeyCode key) throws Exception {
        fx(() -> { robot.keyPress(key); robot.keyRelease(key); return null; }); Thread.sleep(180);
    }
    static void hover(String id, String name) throws Exception {
        fx(() -> { Bounds b = input().localToScreen(input().getBoundsInLocal()); robot.mouseMove(b.getCenterX(), b.getCenterY()); return null; });
        Thread.sleep(200);
        fx(() -> { Label label = (Label) node(id); Bounds b = label.localToScreen(label.getBoundsInLocal());
            robot.mouseMove(b.getCenterX(), b.getCenterY()); return null; });
        Thread.sleep(1400);
        fx(() -> { Label label = (Label) node(id); Tooltip tooltip = label.getTooltip();
            require(tooltip != null && tooltip.isShowing(), "Real hover did not activate " + id);
            require(tooltip.getText().equals(label.getText()) && tooltip.isWrapText(), "Native tooltip stale " + id);
            log("Native hover=" + id + "; popup=" + tooltip.getWidth() + "x" + tooltip.getHeight() + "; text=" + tooltip.getText());
            return null; });
        screen(name);
    }
    static void nativeScrollEnd() throws Exception {
        click(result());
        fx(() -> { robot.keyPress(KeyCode.CONTROL); robot.keyPress(KeyCode.END); robot.keyRelease(KeyCode.END); robot.keyRelease(KeyCode.CONTROL); return null; });
        Thread.sleep(200);
        fx(() -> { ScrollPane s = (ScrollPane) result().lookup(".scroll-pane");
            Bounds v = s.lookup(".viewport").localToScene(s.lookup(".viewport").getBoundsInLocal());
            Bounds c = s.getContent().localToScene(s.getContent().getBoundsInLocal());
            require(result().getLength() == 1823 && result().getScrollTop() > 0 && c.getMaxY() <= v.getMaxY() + 2,
                    "Native Ctrl-End did not reach complete help end");
            log("Native Ctrl-End reached complete search-help end; height=" + ((Region) node("feedbackSection")).getHeight()); return null; });
    }
    static void drag(double fraction) throws Exception {
        Point2D start = fx(() -> { Node d = split().lookupAll(".split-pane-divider").stream()
                .filter(n -> n.getParent() == split()).findFirst().orElseThrow(); Bounds b = d.localToScreen(d.getBoundsInLocal());
            return new Point2D(b.getCenterX(), b.getCenterY()); });
        Point2D end = fx(() -> split().localToScreen(split().getWidth()/2, split().getHeight()*fraction));
        fx(() -> { robot.mouseMove(start); robot.mousePress(MouseButton.PRIMARY); return null; });
        for (int i=1;i<=20;i++) { double y=start.getY()+(end.getY()-start.getY())*i/20;
            fx(() -> { robot.mouseMove(start.getX(),y); return null; }); Thread.sleep(25); }
        fx(() -> { robot.mouseRelease(MouseButton.PRIMARY); return null; }); Thread.sleep(200);
        log("Native divider drag fraction=" + fraction + "; feedback=" + height());
    }
    static void screen(String name) throws Exception {
        WritableImage shot = fx(() -> {
            double x=stage.getX(), y=stage.getY(), maxX=x+stage.getWidth(), maxY=y+stage.getHeight();
            for (Window w : Window.getWindows()) if (w.isShowing() && w instanceof javafx.stage.PopupWindow) {
                x=Math.min(x,w.getX()); y=Math.min(y,w.getY());
                maxX=Math.max(maxX,w.getX()+w.getWidth()); maxY=Math.max(maxY,w.getY()+w.getHeight());
            }
            return robot.getScreenCapture(null,x,y,maxX-x,maxY-y,false);
        });
        BufferedImage png = new BufferedImage((int)shot.getWidth(), (int)shot.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y=0;y<png.getHeight();y++) for(int x=0;x<png.getWidth();x++) png.setRGB(x,y,shot.getPixelReader().getArgb(x,y));
        ImageIO.write(png,"png",output.resolve(name+".png").toFile());
    }
}
