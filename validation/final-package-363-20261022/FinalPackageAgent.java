import java.awt.image.BufferedImage;
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
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Exact-JAR diagnostics by Ernest Chua with OpenAI Codex assistance. No OS input simulation. */
public class FinalPackageAgent {
    static final List<String> report = new ArrayList<>();
    static Stage stage;
    static Path output;
    public static void premain(String arg, Instrumentation instrumentation) {
        output = Path.of(arg);
        Thread worker = new Thread(FinalPackageAgent::audit, "final-package-audit");
        worker.setDaemon(true);
        worker.start();
    }
    static void audit() {
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
            require(stage != null, "Application did not show production window");
            fx(() -> { for (Screen s : Screen.getScreens()) log("Screen=" + s.getBounds()
                    + "; workArea=" + s.getVisualBounds() + "; outputScale=" + s.getOutputScaleX());
                log("JavaFX=" + System.getProperty("javafx.runtime.version")); return null; });
            Thread.sleep(300);
            require(!Files.exists(Path.of("data/ponhub.json")), "Fresh launch wrote operational data");
            compact("fresh-startup");
            capture("01-fresh-startup", fx(() -> stage.getScene().getRoot()));
            command("add r/tutor n/Mei Lim p/92345678");
            command("add r/student n/Alex Tan l/S2 pp/91234567");
            command("add r/student n/Jamie Tan l/S2 pp/91234567");
            command("addlesson d/Mon st/1600 et/1730 s/Math tu/Mei Lim rm/R1");
            command("list r/student");
            command("enrol 1 lid/L1");
            command("enrol 2 lid/L1");
            command("mark 1 lid/L1 d/2026-10-05 s/present");
            compact("short-feedback");
            fx(() -> { stage.setMaximized(true); return null; });
            Thread.sleep(400);
            compact("maximized");
            capture("02-maximized", fx(() -> stage.getScene().getRoot()));
            command("help search");
            scrollToEnd();
            fx(() -> { split().setDividerPositions(0.55); return null; });
            Thread.sleep(150);
            double expanded = height();
            require(expanded > 180, "Divider model did not expand feedback");
            command("list r/student");
            command("help search");
            require(Math.abs(height() - expanded) <= 1, "Commands moved chosen divider");
            scrollToEnd();
            capture("03-expanded-search-help", fx(() -> stage.getScene().getRoot()));
            fx(() -> { split().setDividerPositions(0); return null; });
            Thread.sleep(150);
            compact("divider-shrunk");
            command("search c/lesson s/" + "Advanced Mathematics ".repeat(14).strip());
            heading("lessonContext", "04-long-lesson-context");
            heading("lessonSummary", "05-lesson-summary-zero");
            command("lessons");
            heading("lessonContext", "06-lessons-context-live");
            require(fx(() -> ((Label) node("lessonSummary")).getText().contains("1")), "Lesson count stale");
            heading("lessonSummary", "07-lesson-summary-live");
            command("search c/student n/Alex");
            fx(() -> { people().getSelectionModel().select(0); return null; });
            command("history 1 lid/L1");
            heading("historyContext", "08-history-context");
            heading("historySummary", "09-history-summary-one");
            command("mark 1 lid/L1 d/2026-10-12 s/absent");
            require(fx(() -> ((Label) node("historySummary")).getText().contains("2")), "History count stale");
            heading("historySummary", "10-history-summary-live");
            byte[] saved = Files.readAllBytes(Path.of("data/ponhub.json"));
            Object selected = fx(() -> people().getSelectionModel().getSelectedItem());
            List<?> visible = fx(() -> List.copyOf(people().getItems()));
            List<?> history = fx(() -> List.copyOf(((ListView<?>) node("historyListView")).getItems()));
            String context = fx(() -> ((Label) node("historyContext")).getText());
            String draft = "editlesson lid/L1 s/Unfinished";
            fx(() -> { input().setText(draft); return null; });
            for (Node target : fx(() -> List.of(input(), result()))) {
                fx(() -> { Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                        false, false, false, false)); return null; });
                require(fx(() -> input().getText().equals(draft)), "F1 event lost draft");
            }
            menuChecks();
            fx(() -> { ((MenuBar) node("menuBar")).getMenus().get(1).getItems().get(0).fire(); return null; });
            require(fx(() -> input().getText().equals(draft)), "Help menu handler lost draft");
            require(fx(() -> people().getSelectionModel().getSelectedItem() == selected), "Help lost selection");
            require(fx(() -> visible.equals(people().getItems())), "Help changed people filter");
            require(fx(() -> history.equals(((ListView<?>) node("historyListView")).getItems())), "Help changed history");
            require(fx(() -> context.equals(((Label) node("historyContext")).getText())), "Help changed context");
            require(fx(() -> node("historyListView").getParent().isVisible()), "Help switched result pane");
            fx(() -> { stage.setMaximized(false); stage.setWidth(450); stage.setHeight(360); return null; });
            Thread.sleep(300);
            compact("minimum-window");
            command("help search");
            scrollToEnd();
            fx(() -> { input().setText(draft); Event.fireEvent(input(), new KeyEvent(KeyEvent.KEY_PRESSED,
                    "", "", KeyCode.F1, false, false, false, false)); return null; });
            capture("13-compact-help-draft-history", fx(() -> stage.getScene().getRoot()));
            require(Arrays.equals(saved, Files.readAllBytes(Path.of("data/ponhub.json"))), "Read-only checks changed data");
            log("PASS: F1 synthetic events from input/feedback and Help handler preserve draft/filter/selection/history/context/data bytes");
            log("NOT RUN: native divider drag, OS-keyboard popup navigation/F1, pointer-hover activation, other OS/scale configurations");
            log("Scene/tooltip/popup snapshots are rendered controls, not native desktop screenshots or human acceptance");
            passed = true;
        } catch (Throwable e) {
            log("FAIL=" + e);
            for (StackTraceElement line : e.getStackTrace()) log("  " + line);
        } finally {
            log("Exact-JAR automated affected checks=" + (passed ? "PASS" : "FAIL"));
            try { Files.write(output.resolve("result.txt"), report); } catch (Exception ignored) { }
            try { fx(() -> { stage.hide(); Platform.exit(); return null; }); } catch (Exception ignored) { }
        }
    }
    static Node node(String id) { return stage.getScene().lookup("#" + id); }
    static TextField input() { return (TextField) node("commandTextField"); }
    static TextArea result() { return (TextArea) node("resultDisplay"); }
    static ListView<?> people() { return (ListView<?>) node("personListView"); }
    static SplitPane split() { return (SplitPane) node("resultSplitPane"); }
    static double height() throws Exception { return fx(() -> ((Region) node("feedbackSection")).getHeight()); }
    static void compact(String label) throws Exception {
        fx(() -> {
            double h = ((Region) node("feedbackSection")).getHeight();
            log(label + ": scene=" + stage.getScene().getWidth() + "x" + stage.getScene().getHeight()
                    + "; feedback=" + h + "; records=" + ((Region) node("recordsSplitPane")).getHeight());
            require(h >= 80 && h <= 100, "Feedback not compact: " + h);
            require(((Region) node("recordsSplitPane")).getHeight() >= 120, "Records lost minimum");
            for (String id : List.of("viewNavigation", "commandTextField", "resultDisplay", "personListView",
                    "statusbarPlaceholder")) {
                Bounds b = node(id).localToScene(node(id).getBoundsInLocal());
                require(b.getWidth() > 0 && b.getHeight() > 0 && b.getMinY() >= 0
                        && b.getMaxY() <= stage.getScene().getHeight() + 1, "Unreachable " + id + ": " + b);
            }
            return null;
        });
    }
    static void command(String text) throws Exception {
        fx(() -> { input().setText(text); Event.fireEvent(input(), new ActionEvent()); return null; });
        Thread.sleep(100);
        require(fx(() -> input().getText().isEmpty()), "Command rejected: " + text + ": " + fx(() -> result().getText()));
        String feedback = fx(() -> result().getText());
        log("Command=" + text + "; feedback=" + (feedback.length() > 250 ? feedback.length() + " characters" : feedback.replace('\n', '|')));
    }
    static void heading(String id, String name) throws Exception {
        fx(() -> {
            Label label = (Label) node(id);
            Tooltip tooltip = label.getTooltip();
            require(tooltip != null && tooltip.textProperty().isBound()
                    && tooltip.getText().equals(label.getText()) && tooltip.isWrapText(), "Heading stale: " + id);
            tooltip.show(label, stage.getX() + 50, stage.getY() + 80);
            Region root = (Region) tooltip.getScene().getRoot();
            root.applyCss(); root.layout();
            log(id + ": popup=" + tooltip.getWidth() + "x" + tooltip.getHeight() + "; text=" + tooltip.getText());
            captureOnFx(name, root);
            tooltip.hide();
            return null;
        });
    }
    static void scrollToEnd() throws Exception {
        fx(() -> { ScrollBar bar = result().lookupAll(".scroll-bar").stream().map(ScrollBar.class::cast)
                .filter(b -> b.getOrientation() == Orientation.VERTICAL).findFirst().orElseThrow();
            if (bar.isVisible()) bar.setValue(bar.getMax()); return null; });
        Thread.sleep(100);
        fx(() -> { ScrollPane scroll = (ScrollPane) result().lookup(".scroll-pane");
            Bounds v = scroll.lookup(".viewport").localToScene(scroll.lookup(".viewport").getBoundsInLocal());
            Bounds c = scroll.getContent().localToScene(scroll.getContent().getBoundsInLocal());
            require(result().getLength() == 1823
                    && c.getMaxY() <= v.getMaxY() + 2 && c.getMaxY() >= v.getMinY(), "Help end unreachable");
            log("Complete 1823-character search-help end reached; feedback=" + ((Region) node("feedbackSection")).getHeight());
            return null; });
    }
    static void menuChecks() throws Exception {
        fx(() -> {
            for (Menu menu : ((MenuBar) node("menuBar")).getMenus()) {
                menu.show();
                ContextMenu popup = Window.getWindows().stream().filter(w -> w instanceof ContextMenu && w.isShowing())
                        .map(w -> (ContextMenu) w).findFirst().orElseThrow();
                Region root = (Region) popup.getScene().getRoot(); root.applyCss(); root.layout();
                Node item = root.lookup(".menu-item");
                for (boolean focused : new boolean[]{false, true}) {
                    item.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), focused); root.applyCss();
                    Label label = (Label) item.lookup(".label"); Color background = null; double opacity = 1;
                    for (Node n = label; n != null; n = n.getParent()) {
                        opacity *= n.getOpacity();
                        if (background == null && n instanceof Region r && r.getBackground() != null)
                            for (var fill : r.getBackground().getFills())
                                if (fill.getFill() instanceof Color c && c.getOpacity() == 1) background = c;
                    }
                    require(background != null, "No menu background");
                    Color text = background.interpolate((Color) label.getTextFill(), opacity);
                    double ratio = (Math.max(luminance(text), luminance(background)) + .05)
                            / (Math.min(luminance(text), luminance(background)) + .05);
                    require(ratio >= 4.5, "Unreadable menu: " + menu.getText());
                    log("Menu=" + menu.getText() + "; focused=" + focused + "; contrast=" + ratio);
                    captureOnFx("menu-" + menu.getText() + "-" + focused, root);
                }
                menu.hide();
            }
            return null;
        });
    }
    static double luminance(Color c) { return .2126 * linear(c.getRed()) + .7152 * linear(c.getGreen()) + .0722 * linear(c.getBlue()); }
    static double linear(double v) { return v <= .04045 ? v / 12.92 : Math.pow((v + .055) / 1.055, 2.4); }
    static void capture(String name, Node root) throws Exception { fx(() -> { captureOnFx(name, root); return null; }); }
    static void captureOnFx(String name, Node root) throws Exception {
        WritableImage shot = root.snapshot(null, null);
        BufferedImage png = new BufferedImage((int) shot.getWidth(), (int) shot.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < png.getHeight(); y++) for (int x = 0; x < png.getWidth(); x++)
            png.setRGB(x, y, shot.getPixelReader().getArgb(x, y));
        ImageIO.write(png, "png", output.resolve(name + ".png").toFile());
    }
    static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    static void log(String text) { report.add(text); System.out.println(text); }
    static <T> T fx(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action); Platform.runLater(task); return task.get(15, TimeUnit.SECONDS);
    }
}
