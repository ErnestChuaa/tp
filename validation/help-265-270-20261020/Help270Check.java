package seedu.address.logic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonId;
import seedu.address.storage.JsonPonHubDataStorage;
import seedu.address.storage.ProtectedPonHubDataStorage;

/** Disposable verification of packaged classes; does not operate a native application window. */
public class Help270Check {
    public static void main(String[] args) throws Exception {
        Path directory = Path.of(args[0]).toAbsolutePath();
        Files.createDirectories(directory);
        Path path = directory.resolve("normal.json");
        check(!Files.exists(path), "Use a fresh disposable directory");
        CanonicalPeopleSession seed = new CanonicalPeopleSession(path);
        for (String command : List.of("add r/tutor n/Mei Lim p/92345678",
                "add r/student n/Alex Tan l/P1 pp/91234567 p/81234567 e/alex@example.com a/10 Main Road",
                "add r/student n/Jamie Tan l/P2 pp/91234567",
                "addlesson d/Mon st/1600 et/1730 s/Math tu/Mei Lim rm/R1", "list r/student",
                "enrol 1 lid/L1", "mark 1 lid/L1 d/2026-10-05 s/present")) {
            seed.execute(command);
        }
        AtomicInteger writes = new AtomicInteger();
        JsonPonHubDataStorage storage = new JsonPonHubDataStorage(path);
        CanonicalPeopleSession session = new CanonicalPeopleSession(new ProtectedPonHubDataStorage(path), state -> {
            writes.incrementAndGet();
            storage.saveData(state);
        });
        session.execute("search c/student n/Alex");
        session.execute("showlesson lid/L1 d/2026-10-05");
        var before = session.getDataSnapshot();
        var visible = List.copyOf(session.getPeople());
        var context = session.personSearchContextProperty().get();
        var lessons = List.copyOf(session.getDisplayedLessons());
        byte[] bytes = Files.readAllBytes(path);
        check(visible.size() == 1 && visible.getFirst().getId().equals(new PersonId("S1")), "Current index S1");
        Locale previous = Locale.getDefault();
        int helpCount = 0;
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            check(session.execute("help").getFeedbackToUser().equals(
                    CanonicalCommandCatalog.getOverview(session.getDataLoadError())), "Normal overview");
            for (String word : session.getCommandWords()) {
                for (String topic : List.of(word, word.toUpperCase(Locale.ROOT))) {
                    var result = session.execute("help " + topic);
                    check(result.getFeedbackToUser().equals(CanonicalCommandCatalog.getUsage(word, false)
                            .orElseThrow()) && !result.isExit(), "Topic " + topic);
                    helpCount++;
                }
            }
        } finally {
            Locale.setDefault(previous);
        }
        System.out.println("PASS normal overview and " + helpCount + " lower/uppercase topics under Turkish locale");
        int errors = 0;
        for (String command : List.of("unknown", "clear", "find Alex", "HELP", "help unknown", "help clear",
                "help add edit", "help\nedit", "edit", "edit 1", "edit 1 n/", "edit 1 l/", "edit 1 pp/",
                "edit 1 x/value", "edit 1 n/Alex n/Jamie", "editlesson", "editlesson lid/L1",
                "editlesson lid/L1 s/", "editlesson lid/L1 tp/92345678", "editlesson lid/L1 x/value",
                "editlesson lid/L1 st/1800", "editlesson lid/L99 s/Math", "editlesson lid/L1 d/Tue")) {
            reject(session, command);
            errors++;
        }
        for (String control : List.of("\0", "\b", "\u001b")) {
            for (String index : List.of(control + "1", "1" + control, "1" + control + "1")) {
                for (String command : List.of("delete " + index, "edit " + index + " n/Alex",
                        "enrol " + index + " lid/L1", "unenrol " + index + " lid/L1",
                        "mark " + index + " lid/L1 d/2026-10-05 s/present",
                        "unmark " + index + " lid/L1 d/2026-10-05", "history " + index,
                        "lessons si/" + index)) {
                    check(reject(session, command) instanceof ParseException, "Controls use checked parsing feedback");
                    check(writes.get() == 0 && before.equals(session.getDataSnapshot())
                            && Arrays.equals(bytes, Files.readAllBytes(path)), "Control rejection preserves data");
                    check(visible.equals(session.getPeople()) && context.equals(session.personSearchContextProperty()
                            .get()), "Control rejection preserves current positions/context");
                    errors++;
                }
            }
        }
        String weekday = "Attendance date 2026-10-06 does not match lesson L1's Mon schedule.";
        for (String command : List.of("mark 1 lid/L1 d/2026-10-06 s/present", "unmark 1 lid/L1 d/2026-10-06",
                "showlesson lid/L1 d/2026-10-06")) {
            String expected = command.startsWith("showlesson")
                    ? "Date 2026-10-06 must be a Mon for lesson L1." : weekday;
            check(reject(session, command).getMessage().equals(expected), "Typed weekday feedback");
            errors++;
        }
        check(session.execute("editlesson lid/L1 s/Math").getFeedbackToUser().equals("No changes to lesson: L1."),
                "No-op Lesson edit");
        check(writes.get() == 0 && before.equals(session.getDataSnapshot()) && visible.equals(session.getPeople())
                && context.equals(session.personSearchContextProperty().get())
                && lessons.equals(session.getDisplayedLessons()) && Arrays.equals(bytes, Files.readAllBytes(path)),
                "All help/failure/no-op paths preserve bytes, snapshot, people and selected Lesson details");
        System.out.println("PASS " + errors + " malformed/domain/control/weekday rejections; zero writes; retry remains usable");
        session.execute("edit 1 e/ a/ p/");
        var student = session.getPeople().getFirst();
        check(student.getId().equals(new PersonId("S1")) && student.getContactDetails().getPhone().isEmpty()
                && student.getContactDetails().getEmail().isEmpty() && student.getContactDetails().getAddress().isEmpty(),
                "Allowed optional clears");
        check(writes.get() == 1 && before.lessons().equals(session.getDataSnapshot().lessons())
                && before.attendance().equals(session.getDataSnapshot().attendance()), "One save, stable links/history");
        session.execute("edit 1 e/ a/ p/");
        check(writes.get() == 1, "Repeated optional clear is a no-op");
        check(new CanonicalPeopleSession(path).getDataSnapshot().equals(session.getDataSnapshot()), "Restart");
        System.out.println("PASS optional e/a/Student-phone clear; one write; repeated clear no write; reload matches");
        for (String status : List.of("missing", "corrupt", "legacy", "future")) {
            Path protectedPath = directory.resolve(status + ".json");
            String content = switch (status) {
            case "corrupt" -> "{broken";
            case "legacy" -> "{\"persons\":[]}";
            default -> "{\"schemaVersion\":99}";
            };
            if (!status.equals("missing")) {
                Files.writeString(protectedPath, content);
            }
            CanonicalPeopleSession checked = new CanonicalPeopleSession(new ProtectedPonHubDataStorage(protectedPath),
                    state -> { throw new AssertionError("Read/protected routes cannot write"); });
            checked.execute("help");
            checked.execute("list");
            checked.execute("help EXIT");
            if (!status.equals("missing")) {
                check(checked.getCommandWords().equals(List.of("help", "list", "exit")), "Protected catalogue");
                for (String command : List.of("edit", "edit\ninvalid", "editlesson", "editlesson\ninvalid",
                        "unknown\ninvalid", "delete 1\0")) {
                    Exception error = reject(checked, command);
                    check(error instanceof CommandException && error.getMessage().equals(
                            checked.getDataLoadError().orElseThrow()), "Protection before argument parsing");
                }
                for (String topic : List.of("edit", "EDIT", "editlesson", "EDITLESSON")) {
                    check(reject(checked, "help " + topic) instanceof ParseException, "Unavailable protected topic");
                }
                check(Files.readString(protectedPath).equals(content), "Rejected bytes retained");
            } else {
                check(!Files.exists(protectedPath), "Read-only missing store remains missing");
            }
        }
        System.out.println("PASS missing/corrupt/legacy/future startup; protected help/list/exit only and parsing precedence");
        System.out.println("ALL PACKAGED ROUTER CHECKS PASS; native UI and final-candidate acceptance are separate");
    }

    private static Exception reject(CanonicalPeopleSession session, String command) throws Exception {
        try {
            session.execute(command);
        } catch (ParseException | CommandException expected) {
            check(expected.getMessage() != null && !expected.getMessage().isBlank(), "Recoverable message");
            return expected;
        }
        throw new AssertionError("Expected rejection of " + command);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
