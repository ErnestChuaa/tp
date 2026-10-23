Ernest's released-v1.4 alpha checks — 23 October 2026

Public release: https://github.com/AY2627S1-CS2103T-F13-3/tp/releases/tag/v1.4
Tag/source c92c689ee8802fb2cb3957832c25dd9407099dbf. Downloaded ponhub.jar:
12,655,415 bytes; SHA-256
8fbd0e5414d91c006984489b71083445da41d22f76f37c2fbc49a681d79ac9d6.
Manifest: PonHub, 1.4, main ponhub.Main. The JAR was not altered/repackaged.
Oracle Java 25.0.4+7 / bundled JavaFX 17.0.7; Windows 11 amd64. Observed
2048x1152 logical / 2560x1440 physical at 125%. Display settings unchanged.

The public JAR was placed in a new writable app folder and launched using
java -javaagent:<separate diagnostic>=<evidence> -jar ponhub.jar, then
restarted against the same fictitious data. Commands use the real packaged
CommandBox ActionEvent/session/storage. This is packaged behavior coverage,
not native typing or broad platform/scaling acceptance. All started app
processes were closed. AlphaAgent is the text-only version of the probe;
native screenshot capture is omitted from this shared evidence.

PASS: two Students have independent attendance keys in one Lesson;
correction preserves key count; identical mark skips saving; weekday/date/
selector/missing-Lesson errors preserve bytes; unenrolment retains history;
former members cannot receive new occurrences but existing records can be
corrected; unmark removes one key and repeated unmark is a no-op; referenced
Lesson deletion is blocked; a directory at the save destination leaves the
failed draft and filtered people view intact; restoring the file/retrying
uses the first Parent ID; restart restores IDs, roster and retained absence;
read-only retrieval preserves bytes. Both result transcripts are retained.

Observed alpha presentation finding: the default window shows a horizontal
people scrollbar even with short names and Not provided contact values.
Recorded on existing #370 and labeled alpha-bug because its acceptance
already covers overflow. #377 is a candidate fix, not a verified resolution.
Application foreground was visually inspected during the original run;
screenshots are kept local and are not part of this public evidence.

The first probe searched only Labels for history text, missing ListCells.
Including ListCells fixed that diagnostic assertion with unchanged release
bytes. That was not a product missing-history bug. The saved runner was executed again without screenshot capture; both fresh
and reload phases pass, with the visible horizontal scrollbar also confirmed
by the actual control property in both transcripts.

Prepared by Ernest Chua with OpenAI Codex assistance under #327's proposed
attendance-state/persistence coverage. This is not independent acceptance of
Ernest's own history UI. One finding was recorded on an existing issue; no
five-report individual completion or v1.5 release acceptance is claimed.
Owners/deadlines remain unchanged. Retain this evidence-only fork branch;
these artifacts do not need to enter the product/master branch.
