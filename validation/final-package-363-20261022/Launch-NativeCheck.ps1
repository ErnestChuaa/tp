# Manual exact-JAR check; Ernest Chua with OpenAI Codex assistance.
$ErrorActionPreference = 'Stop'
$acceptanceRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$manualFixture = Join-Path $acceptanceRoot '_temp\manual-jar'
$acceptanceJava = 'C:\Users\ernes\tp\_temp\round1-reconcile-20261014\jdk\zulu25.36.205-ca-fx-jdk25.0.4.1-win_x64\bin\java.exe'
$manualJar = Join-Path $manualFixture 'ponhub.jar'
if ((Get-FileHash -LiteralPath $manualJar -Algorithm SHA256).Hash.ToLowerInvariant() -ne '8fbd0e5414d91c006984489b71083445da41d22f76f37c2fbc49a681d79ac9d6') {
    throw 'Manual fixture JAR checksum mismatch'
}
Push-Location -LiteralPath $manualFixture
try {
    & $acceptanceJava -version
    & $acceptanceJava -jar $manualJar
} finally {
    Pop-Location
}
