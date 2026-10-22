Frozen v1.4 affected-package checks -- 22 October 2026
Ernest Chua (ErnestChuaa), with OpenAI Codex assistance

Status
------
Automated and native affected checks PASS on the exact downloaded final JAR.
The user explicitly requested PowerShell/script-based native testing after the
initial computer-use surface exposed browser controls only. Run-NativeCheck.ps1
compiles a separate diagnostic agent and drives the actual application with
JavaFX Robot. The completed native/result.txt and real screen captures supersede
the earlier native NOT RUN status in the initial automated-run transcripts.
Ernest's bounded #363/#369 final-package handoff is ready for #274 publication.

Identity
--------
Application source: c92c689ee8802fb2cb3957832c25dd9407099dbf.
Fresh immutable download from Vincent's freeze commit:
https://raw.githubusercontent.com/gugrusaurus/tp/5abc52528bdfd608f001b2f0aa26fc60436123a7/validation/final-candidate-20261022/ponhub.jar
ponhub.jar: 12,655,415 bytes; SHA-256
8fbd0e5414d91c006984489b71083445da41d22f76f37c2fbc49a681d79ac9d6.
Manifest: PonHub / 1.4 / ponhub.Main. See package-identity.json.
The JAR is not rebuilt, modified or repackaged by this evidence increment.
Production source/build/dependency inputs are unchanged. No release/tag created.

Checks performed
----------------
1. Frozen-source Gradle wrapper, installed Azul Java25+FX:
   gradlew.bat --offline --no-daemon test
     --tests ponhub.ui.MainWindowResultResizeTest
     --tests ponhub.ui.CanonicalMainWindowTest
     --tests ponhub.ui.ThemeMenuContrastTest
     --tests ponhub.ui.ResultHeadingTooltipTest
     checkstyleMain checkstyleTest
   PASS: 31 tests, zero failures/errors/skips; both Checkstyle tasks.
   focused-results.json and focused-check.log retain actual results. This is
   a focused run, not a new full-suite or cross-platform CI claim.
2. Exact JAR launched with java -javaagent:<diagnostic-agent>=<output> -jar
   ponhub.jar from a fresh writable folder containing only the downloaded JAR.
   The agent observes production controls, submits CommandBox ActionEvents,
   sets the divider model, fires F1 KeyEvents and invokes Help's real handler.
   It does not use Robot, native input synthesis or OS screen capture.
   PASS under both installed Java 25 runtimes on Windows 11 amd64:
   - Azul 25.0.4.1+1-LTS; JavaFX 25.0.4+100 (result.txt).
   - Oracle 25.0.4+7-LTS-189; packaged JavaFX 17.0.7+2 (oracle-result.txt).
   Observed screen 2048x1152 logical pixels, outputScale 1.25. No display
   settings were changed. This does not cover the full course scaling matrix.
3. Fresh/short-feedback and maximized startup keep feedback at 80 logical px.
   Scene sizes: 725.6x562.4 and 2048x1128.8. Minimum window scene 436x322.4
   retains feedback 80 and records 120.8, reachable navigation/input/footer.
   Setting the divider model expands to 554.4 and shrinks to 80; commands/help
   preserve that height. This does not establish native dragging behavior.
   Complete 1,823-character help search reaches its end at compact height and
   minimum window; it fits in the expanded maximized panel without scrolling.
4. All four production tooltips bind to their current heading text. A long
   Lesson query wraps completely; lessons changes context/count from 0 to 1.
   Student/Lesson history context appears and the open count updates from 1
   to 2 after a committed second-date mark. Existing focused tests additionally
   cover query/Student/selected-Lesson/date/error/help transitions and compact
   tooltip skins. Popups here are shown programmatically, not by pointer hover.
5. Actual File/Help popup skins pass ordinary/highlighted text contrast:
   15.223:1 and 9.808:1. Focus is a CSS pseudo-class for this rendering check.
   Synthetic F1 events from input/feedback and the Help menu handler preserve
   unfinished draft, people filter/order/selection, active history/context and
   all operational bytes. This does not establish OS keyboard menu navigation.
6. Scene/control PNGs were visually inspected: startup, maximized short
   feedback, expanded complete help, wrapped Lesson query, live Lesson/history
   counts/context, compact Help/draft/history, highlighted Help popup. At the
   minimum size the records scroll independently and the long footer path
   truncates. These are rendered-node snapshots, not native screen captures.

The first diagnostic attempt assumed long help must have a visible scrollbar
even when the maximized expanded panel could fit every line. It stopped on
that harness assertion. initial-harness-result.txt retains the failed attempt;
the corrected assertion checks that all 1,823 characters and the complete end
are reachable whether scrolling is necessary or not. Both final runs start
again from separate empty JAR folders and pass. No production fix was made.

Materials/rendering reconciliation
----------------------------------
Reviewed exact #376 head 9ae891b5f98cce82d75c72b150e1bf71ec45bbf4 against
c92c689e: two Markdown files only, with no source/build/guide/image change.
All nine immutable source-guide links and their referenced rendered anchors
resolve in the locally built guides. Both historical PNGs match the recorded
SHA-256; the release notes embed no older screenshot. Old provenance remains
under its original ed32fe49 / 5e24df51... identity, not these new bytes.

Frozen-lockfile Jekyll build PASS using ruby:3.3 / Bundler 2.5.11 and the
existing ponhub-jekyll-gems volume. The first attempt selected the unused
ponhub-jekyll-bundle volume and lacked Bundler; correcting the cache mount
produced the successful repository build. No dependency/lockfile changed.
Chrome inspected GitHub's exact-commit release notes and historical-provenance
render at observed 2052x1042 viewport. New UI wording and identity/history
separation are visible and the 1012px article has no horizontal overflow.
The same head's Jekyll output was served at http://127.0.0.1:8876:
- UG Moving between results: desktop 2052x986, compact observed 854x600,
  and 450x600; tooltip paragraph, navigation table and surrounding text.
- DG Active Student attendance history checks (#100): desktop 2052x986 and
  compact observed 854x600; full tooltip/manual-check paragraph and list.
Screenshots and DOM measurements inspected; paragraphs wrap, table remains
usable and there is no document-level horizontal overflow. Requested 853px
override was observed as 854px. Temporary viewport override was reset.
These browser checks establish guide/material rendering, not native app tests.

While these checks ran, Zhi Yu substantively reviewed/approved #376 and it
merged as 8ce4440ead2374e65ec5477df28fe20a55477913. Its head Java CI run
39769661417 passed. This evidence does not claim that review or merge as
Ernest's action; it supplies supplementary rendered/source checks.

Completed native check and release handoff
------------------------------------------
Run-NativeCheck.ps1 launched the exact verified JAR in another fresh writable
folder with Azul 25.0.4.1+1-LTS / JavaFX 25.0.4+100, Windows 11 amd64.
Observed 2048x1152 logical screen at outputScale 1.25. Actual native screen
captures include window decorations and live tooltips/popups, without editing.
No screen scaling or system setting was changed.

PASS: fresh and maximized startup feedback 80 logical px; real mouse divider
drag expands to 555.2 and shrinks to 80; list/help keep the chosen split. Real
Ctrl-End reaches the complete help-search end at maximized compact feedback
and the minimum 436x322.4 scene. Navigation, both scrollable result viewports,
command input and footer remain reachable; the long footer path truncates.

PASS: actual pointer hover opens all four current tooltips at the minimum
window. A long query remains complete after help replaces its feedback;
lessons changes the context and count to All lessons / 1. History identifies
Student S1 / L1 and the displayed count updates from 1 to 2 after a committed
second-date mark, remaining available after help. Native screenshots inspected
for full wrapping text and preserved result-list space.

PASS: real File/Help clicks show readable ordinary popup labels. Real Down
highlights the item, Right changes File to Help, Escape dismisses it and Enter
invokes Help. Native F1 from command input and feedback each replaces search
guidance with the overview, preserving the unfinished draft, people filter,
order, selected Alex, active history/context and operational bytes. Both
ordinary/highlighted popup screenshots and final draft/history were inspected.

These are automated native Windows acceptance results, not an independent
human sign-off. Other OS/scales remain NOT RUN for these new bytes. Older
native/platform evidence stays under its original identity. The first native
pass succeeded; a second fresh run adds explicit hover-after-help coverage and
also passes. No product change or new JAR is needed.

native-check.txt / Launch-NativeCheck.ps1 remain available for optional manual
reproduction. Run-NativeCheck.ps1 is the executed automated native script;
NativePackageAgent.java extends the initial diagnostic helper without altering
application classes. The native report carries its own timing and measurements.

#363 remains Ernest / v1.4 / due 22 October 2026 15:30 SGT, overdue unchanged.
#363's acceptance/handoff is now complete; record the actual overdue completion
without changing its due date. #369 remains integrated/closed; these results
reconcile its headings with the combined package without reopening it. #274 publication and
public-download size/hash/manifest/launch/link verification remain Zhi Yu's
work after affected acceptance. Vincent retains #275 closeout. #362 remains
cancelled. No full human testing round or later v1.5 task is added.

Course verified by direct HTTPS fetch after search-service cache misses:
Week 10 (19-25 October), v1.4 alpha release due 22 October 23:59 SGT.
https://nus-cs2103-ay2627-s1.github.io/website/schedule/week10/project.html
Appendix E, current constraints and expectations were also read. Only the
bounded final-package and materials checks above were undertaken in this run.

Reproduction
------------
Compile FinalPackageAgent.java with javac -cp <exact-ponhub.jar> into a separate
directory. Package those classes with jar cfm diagnostic-agent.jar <manifest>
where the manifest contains 'Premain-Class: FinalPackageAgent'. Launch with
java -javaagent:<absolute-agent-path>=<absolute-output-dir> -jar ponhub.jar
from a new folder containing only the verified JAR; do not reuse its saved
data/preferences. The diagnostic fixture intentionally writes fictitious
records before establishing a read-only data-byte baseline.
