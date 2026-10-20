Round 3 shared-help and defensive evidence - 20 October 2026

Prepared by OpenAI Codex for Ernest Chua (ErnestChuaa), under the existing
Developer Guide acknowledgement. This records agent-operated checks, not
independent teammate review or human-operated native acceptance.

Source and handoff
------------------
Tested source: 9d7ae406e1364bd5b39369545053fe6ec3273888 (PR #348).
Base: team master 493380a444e82709af8c77297482aea5ed33946a, including the
merged person/Lesson editing routes and #320's cards/navigation integration.
Only shared guidance and its regressions change in #348. Feature ownership,
parser grammar, storage format and historical release notes are preserved.
The obsolete UG sentence is corrected; occurrence cancellation remains future
scope. Lesson-help now explicitly rejects blank supplied values. Both guides
describe optional person clears and protected-session restrictions consistently.

#265 is ready for non-author review/integration through #348. The #270 bounded
owner checks below pass on that exact proposed source. Integration handoff
remains conditional on #348's substantive non-author review, green current CI
and merge. These checks must not be delayed until namespace or candidate
creation; retain the later final-candidate run separately. No issue closure,
merge, release or cross-platform native acceptance is claimed here.

Runtime and artifact
--------------------
Windows; Azul Zulu Java/JavaFX 25.0.4.1, the installed jdk+fx runtime used by
the course CI configuration. Wrapper Gradle 9.6.1. The normal shell's Oracle
Java is not the runtime selected for these checks.
Local validation JAR: 12,661,556 bytes; SHA-256
c8d438fc8bc346a1e39562912669e0da4d01ba4743b9cae8aa9dca126ee523bf.
This is not #271's exact final candidate. The harness invokes packaged classes
with -cp and -ea; it does not launch java -jar or operate a native app window.

Checks and actual observations
-----------------------------
PASS: gradlew.bat --no-daemon check coverage shadowJar, with JAVA_HOME set to
Zulu jdk+fx 25.0.4.1. 896 tests in 130 suites, 0 failures, 0 errors, 20 skips
from existing platform assumptions (symlink/POSIX facilities). No tests,
thresholds, skips, dependencies or runtime settings were changed to get a pass.
Both Checkstyle tasks, .github/run-checks.sh and git diff --check pass.
test-results.json names the executed cases and records their outcomes.
Retained log copies normalize line endings and trailing whitespace only.

PASS: packaged-router harness Help270Check.java and packaged-router.log:
- All 17 normal routes supply matching lower/uppercase topic help (34 topics)
  under Turkish default locale, without exit effects or operational saves.
- 98 checked ParseException/CommandException rejections cover malformed and
  unknown routes/help, edit required-field/selector/value restrictions,
  blank/repeated/unknown Lesson values, orphan tp/, invalid time ranges,
  missing Lesson and retained-attendance weekday changes.
- Actual NUL, backspace and ESC at boundary/embedded selector positions are
  exercised through delete, edit, enrol, unenrol, mark, unmark, history and
  lessons. Rejections retain bytes/snapshot/current filtered positions and
  search context, with zero writer calls. The existing full-suite integration
  tests additionally retain list identity/listener and protection precedence.
- The typed weekday rule preserves the existing distinct mark/unmark message
  and showlesson message. Previous selected details stay intact on failure.
- Clearing optional email/address/Student-phone makes one successful save,
  retains S1 and its Lesson/attendance links, survives reload, then repeating
  the identical clear makes no write. Identical Lesson edits make no write.
- Missing-file help/list remains unwritten. Corrupt, legacy and future-version
  sessions advertise only help/list/exit, reject both editing topics, preserve
  rejected bytes and check protection before malformed mutation parsing.

PASS: existing production-scene UI tests on the integrated shell and cards:
- Real Help MenuItem.fire and F1 events from command/result controls return
  the same current catalogue, retain an unfinished Lesson-edit draft and
  selected S3 in the filtered Student list, preserve preferences and bytes.
- Typed edit/editlesson topic variants clear successful submitted input;
  protected topic/mutation failures retain input, show recoverable error
  styling/messages and preserve rejected bytes. No crash is observed.
- MainWindowResultResizeTest's three scene checks pass on this source:
  programmatic divider extremes, logical layout sizes, stable split/draft/
  selection/data across command/menu/F1, and synthetic text reachability.
These are automated production-scene checks, not native input/scaling evidence.

PASS: guide source/render verification:
- Frozen Bundler 2.5.11 and the repository Gemfile.lock; ruby:3.3 with the
  existing ponhub-jekyll-gems cache; bundle _2.5.11_ exec jekyll build --baseurl ''.
- 10 rendered pages, 415 local targets, 33 image targets; no missing file or
  fragment, and no inconsistent table column counts (audit.json/audit.py).
- Connected Chrome, actual localhost render of this source. Inspected
  screenshots and read-only DOM measurements at 2052x1042 and 800x900 cover
  UG command-summary edit rows, shared help/optional-clear/protected guidance,
  corrected Lesson paragraph and its editlesson link, and DG manual-help
  checklist. Text/tables wrap legibly; no page overflow. All images finished
  loading on the inspected guide. Temporary viewport override was reset.
  Screenshot observations were made in the task's browser transcript; this
  evidence directory does not contain exported browser screenshots.

Defensive source audit (no additional behavior change)
------------------------------------------------------
CanonicalCommandDefinition supplies unique typed routes/order/eligibility and
the exhaustive session switch. The session checks protected eligibility before
mutation parsing, and normalizes help topics with Locale.ROOT.
ParserUtil.parseIndex rejects ISO controls before trimming. PersonEditParser and
LessonEditParser keep validation in checked input failures; staged commands
translate domain exceptions to CommandException with original causes.
CanonicalCommandExecutor persists before publication, preserves causes for IO
and AccessDeniedException and skips unchanged-state saves. Constructors and
boundaries use requireNonNull/requireAllNonNull and model validation.
CanonicalLogicManager logs command execution; MainWindow logs success/failure
and renders checked feedback; MainApp and storage helpers log relevant IO and
recovery failures. Existing executor/transaction/integration tests cover these
boundaries, failure rollback and successful retry.
Gradle development run enables assertions and the test task enables them by
default. There are currently no Java assert statements in src/main/java at
the tested SHA. This observation is not assertion execution coverage. No new
programmer invariant was introduced by the shared-help change; no gratuitous
assertions, exception wrappers or test quotas were added for this check.

Remaining stages and limits
---------------------------
#317's independent native help/drag/display matrix remains incomplete, per
its owner's current evidence. Reuse unchanged automated evidence rather than
claiming an inaccessible desktop is tested. This turn's CUA inventory exposes
Chrome and no native apps; browser guide checks do not establish native UI
acceptance. #321 must complete its affected native redesign rerun, including
the existing Windows history-row follow-up. This report does not resolve it.
#270's retained final-candidate run and #272's independent exact-#271 acceptance
remain later stages. No later candidate is required for this source handoff.
#220 UML and #236 icon retain their separate alongside scope.

The original #265 deadline 19 October 2026 21:00 SGT and #270 deadline
20 October 2026 12:00 SGT are unchanged and overdue. Owners, v1.4 milestone,
review/merge gates and target dates are preserved.

Course verification
-------------------
Fetched live on 20 October 2026 using HTTPS after the search fetch returned a
cache miss. Week 10 starts 19 October. The course v1.4 deadline is Thursday
22 October 23:59 SGT; the team's earlier deadlines remain in force.
https://nus-cs2103-ay2627-s1.github.io/website/
https://nus-cs2103-ay2627-s1.github.io/website/schedule/week10/project.html
https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixE-gitHub.html
