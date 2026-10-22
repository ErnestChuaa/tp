# Automated native acceptance explicitly requested by the user.
# Ernest Chua with OpenAI Codex assistance; no product inputs changed.
param([string]$JavaHome = 'C:\Users\ernes\tp\_temp\round1-reconcile-20261014\jdk\zulu25.36.205-ca-fx-jdk25.0.4.1-win_x64')
$ErrorActionPreference = 'Stop'
$acceptanceRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$nativeRunRoot = Join-Path $acceptanceRoot ('_temp\native-run-' + [DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss'))
$nativeClasses = Join-Path $nativeRunRoot 'classes'
$nativeJarFolder = Join-Path $nativeRunRoot 'fresh-jar'
$nativeEvidence = Join-Path $PSScriptRoot 'native'
New-Item -ItemType Directory -Path $nativeClasses, $nativeJarFolder, $nativeEvidence -Force | Out-Null
$nativeJar = Join-Path $nativeJarFolder 'ponhub.jar'
Copy-Item -LiteralPath (Join-Path $acceptanceRoot '_temp\fresh-jar\ponhub.jar') -Destination $nativeJar
$expectedDigest = '8fbd0e5414d91c006984489b71083445da41d22f76f37c2fbc49a681d79ac9d6'
if ((Get-FileHash -LiteralPath $nativeJar -Algorithm SHA256).Hash.ToLowerInvariant() -ne $expectedDigest) {
    throw 'Native JAR checksum mismatch'
}
& "$JavaHome\bin\javac.exe" -cp $nativeJar -d $nativeClasses (Join-Path $PSScriptRoot 'FinalPackageAgent.java') (Join-Path $PSScriptRoot 'NativePackageAgent.java')
if ($LASTEXITCODE -ne 0) { throw 'Native agent compilation failed' }
$nativeManifest = Join-Path $nativeRunRoot 'manifest.mf'
Set-Content -LiteralPath $nativeManifest -Value "Premain-Class: NativePackageAgent`n" -Encoding ascii
$nativeAgent = Join-Path $nativeRunRoot 'native-agent.jar'
& "$JavaHome\bin\jar.exe" cfm $nativeAgent $nativeManifest -C $nativeClasses .
if ($LASTEXITCODE -ne 0) { throw 'Native agent packaging failed' }
Push-Location -LiteralPath $nativeJarFolder
try {
    & "$JavaHome\bin\java.exe" "-javaagent:$nativeAgent=$nativeEvidence" -jar ponhub.jar *> (Join-Path $nativeRunRoot 'run.log')
} finally { Pop-Location }
$nativeResult = Join-Path $nativeEvidence 'result.txt'
Get-Content -LiteralPath $nativeResult
if (-not (Select-String -LiteralPath $nativeResult -Pattern '^Exact-JAR native affected checks=PASS$' -Quiet)) {
    throw "Native check did not pass. See $nativeResult"
}
if ((Get-FileHash -LiteralPath $nativeJar -Algorithm SHA256).Hash.ToLowerInvariant() -ne $expectedDigest) {
    throw 'JAR changed during native check'
}
