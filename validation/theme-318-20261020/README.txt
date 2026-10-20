Native theme acceptance evidence — 20 October 2026

Completed theme matrix: actual Windows 100%, 125% and 150% scaling at
2560x1440 physical resolution. Current/former/protected fixtures pass at
every scale: nine native runs. The same higher physical resolution meets
the course minimums; no 1920x1080 or 1280x720 mode was substituted or
claimed. Native windows were resized from 740x600 to 540x480 logical units.

The user explicitly approved temporary Windows scale changes. Settings
were changed through the Windows UI, and a fresh JVM at each test start
asserted the real output scale. No forced JavaFX rendering-scale flag was
used. The original 125% setting was restored and checked in both Windows
Display Settings and a separate fresh-JVM probe. Resolution stayed at
2560x1440. No sign-out, restart, registry edit or other setting change.

Tested code is 05b2d8ed: team master 42debf09 plus #338/4bdb0e18 and
#339/6cda3bde. The packaged app ran via java -javaagent:... -jar ponhub.jar
with opt-in JavaFX Robot instrumentation. No production class was replaced
or transformed. Input, hover, press, selection, scrolling, resize and menu
actions used native events. The published screenshots are native captures
restricted to the synthetic application's client area.

The current/former runs cover real popup normal/hover/disabled states,
black selected text on orange, command errors and recovery, people
selection, full long-contact/detail/history reachability, explicit retained
attendance, F1/Help-menu drafts, end-of-help navigation and native window/
divider dragging. The protected runs cover the complete warning, native
orange default/hover/pressed OK button with black text, and help/list
recovery routes. Fixture and JAR SHA-256 hashes stayed unchanged.

Native testing found faded disabled context-menu text. The shown-popup
regression failed at 2.486:1 contrast before repair; #338/4bdb0e18 keeps
disabled items opaque on the neutral surface. Palette wrapper checks pass
830 tests; combined checks pass 857 tests. Both have zero failures/errors
and 20 platform skips. Checkstyle, coverage, packaging and repository
checks pass. Both current PR heads pass all three Java CI platforms and
Codecov patch. No code change followed the repair's passing checks.

The six root screenshots/logs show the 125% run; scale-100 and scale-150
contain the additional runs. Individual logs conservatively describe only
their own configuration; manifest.json records the aggregate nine-run
matrix. All displayed names and records are synthetic. Windows-settings
screenshots stay out of the published artifacts.

This retained fork verification branch is evidence only. It is not team
master, #334's shell or a release candidate. Theme native checks are now
complete. An eligible non-author must re-review d96d43dc..4bdb0e18 before
merging #338. Ben's #339 review remains applicable to its unchanged head.
Keep #319/#334 and #317/#321 acceptance and the team's merge gates.

Prepared by Ernest with OpenAI Codex assistance, covered by the shared DG
acknowledgement. The local instrumentation adds no product dependency.
