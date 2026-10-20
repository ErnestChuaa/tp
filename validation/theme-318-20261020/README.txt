Native theme acceptance evidence — 20 October 2026

This retained fork verification branch is evidence only. It is not the
team master, the #334 shell, a release candidate or a publication gate.
The two theme PRs remain independently reviewable in #338 and #339.

Tested code is 05b2d8ed: team master 42debf09 plus #338/4bdb0e18 and
#339/6cda3bde. The packaged application ran via java -javaagent:... -jar
ponhub.jar with opt-in JavaFX Robot instrumentation. No production class
was replaced or transformed. Input, hover, press, selection, scrolling,
window resize and menu actions used native Robot events. Screenshots are
native desktop captures restricted to the synthetic app's client area.

The actual physical screen was 2560x1440 at Windows 125% scale. Both
JavaFX output scale and AWT native display mode/transform were recorded.
Windows display settings were not changed and no rendering-scale flag
was used. Other actual OS scales (100% and 150%) remain NOT RUN.

The 125% current and former fixtures passed error/recovery, real popup
normal/hover/disabled states, selected black text on orange, people
selection, full long-contact/history/detail scrolling, explicit retained
attendance, F1 and Help-menu draft preservation, end-of-help reachability,
and native window/divider dragging. The protected fixture passed the full
warning and native orange default/hover/pressed OK button with black text,
then the help/list recovery routes. Fixture and JAR SHA-256 hashes were
unchanged afterward. All names and records shown are synthetic fixtures.

Native testing found faded disabled context-menu text. The shown-popup
regression failed at 2.486:1 contrast before repair; #338/4bdb0e18 keeps
disabled items opaque on the neutral surface. The regression and full
wrapper check/coverage/shadowJar pass. Palette: 830 tests, 20 platform
skips. Combined: 857 tests, 20 platform skips. Zero failures/errors.

An eligible non-author still needs to re-review the five CSS lines and
contrast regression at 4bdb0e18, plus the final native readiness evidence.
Do not treat these results as #319/#334 or #317/#321 acceptance.

Prepared by Ernest with OpenAI Codex assistance, covered by the shared
DG acknowledgement. The instrumentation is a local acceptance aid and
adds no application dependency. See manifest.json and profile logs for
the exact configuration and observations.
