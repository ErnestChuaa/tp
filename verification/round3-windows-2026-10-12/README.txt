Round 3 Windows evidence -- Ernest, 12 October 2026

Download round3-windows-evidence.zip in this folder using GitHub's Download
raw file button. The ZIP is 13,918,226 bytes and is the unchanged updated
bundle used for exact 1280x720 / 150% acceptance.

SHA-256:
7e1688ed6c126ba0df6aea39178f1b6f25dc629bf87910b85313b637b51bab9b

On macOS, verify the download with:
  shasum -a 256 round3-windows-evidence.zip

Start with round3-exact-acceptance-report.txt inside the ZIP, then inspect
screenshots/720-150-* and exact-setup/720-custom-active.png. The exact run
uses Windows custom 150% scaling after a sign-out/sign-in; it is not an
application scale override. The earlier report and setup-pending notes are
preserved as history and are superseded by the exact acceptance report.

The ZIP also contains 1080p/100% and 125% evidence, a matching packaged JAR,
synthetic fixture, UI snapshots, input transcript, metrics, application logs,
saved preferences, restoration evidence and regression XML. All 465 files
listed in its manifest were verified against their SHA-256 hashes.

acceptance-report.txt and actions.log beside the ZIP are copies for browser
inspection. evidence-manifest.csv describes the files inside the ZIP.

Source tested: 444de5283ef26bf98c3d3c4fd8af0616ffeb4fb8
JAR SHA-256:
ef7439586efced26861a6e9cdd170a92eb076c2da792ff8bda54944014cc1dce

Canonical acceptance report:
https://github.com/AY2627S1-CS2103T-F13-3/tp/issues/128#issuecomment-6125702826

Independent teammate acceptance remains pending. This evidence branch
publishes the artifacts; it adds no new product implementation.
