param([Parameter(Mandatory=$true)][string]$JarPath,
      [Parameter(Mandatory=$true)][string]$JavaHome,
      [Parameter(Mandatory=$true)][string]$RunRoot)
$ErrorActionPreference='Stop'
$alphaSource=(Resolve-Path -LiteralPath $JarPath).Path
$alphaRun=[IO.Path]::GetFullPath($RunRoot)
if(Test-Path -LiteralPath $alphaRun){throw 'Choose a new disposable run directory'}
$alphaApp=Join-Path $alphaRun 'app'
$alphaClasses=Join-Path $alphaRun 'classes'
New-Item -ItemType Directory -Path $alphaApp,$alphaClasses -Force | Out-Null
$alphaJar=Join-Path $alphaApp 'ponhub.jar'
Copy-Item -LiteralPath $alphaSource -Destination $alphaJar
$alphaHash=(Get-FileHash -LiteralPath $alphaJar -Algorithm SHA256).Hash.ToLowerInvariant()
if($alphaHash -ne '8fbd0e5414d91c006984489b71083445da41d22f76f37c2fbc49a681d79ac9d6'){
    throw 'Not the tested public v1.4 artifact'
}
& "$JavaHome\bin\java.exe" -version 2>&1 | Set-Content -LiteralPath (Join-Path $alphaRun 'java-version.txt')
& "$JavaHome\bin\javac.exe" -cp $alphaJar -d $alphaClasses (Join-Path $PSScriptRoot 'AlphaAgent.java')
if($LASTEXITCODE -ne 0){throw 'Diagnostic compilation failed'}
$alphaManifest=Join-Path $alphaRun 'agent.mf'
Set-Content -LiteralPath $alphaManifest -Value "Premain-Class: AlphaAgent`n" -Encoding ascii
$alphaAgent=Join-Path $alphaRun 'agent.jar'
& "$JavaHome\bin\jar.exe" cfm $alphaAgent $alphaManifest -C $alphaClasses .
if($LASTEXITCODE -ne 0){throw 'Diagnostic packaging failed'}
foreach($phase in @('first','reload')) {
    $alphaEvidence=Join-Path $alphaRun $phase
    New-Item -ItemType Directory -Path $alphaEvidence | Out-Null
    $alphaArgs=@("`"-javaagent:$alphaAgent=$alphaEvidence`"",'-jar',"`"$alphaJar`"")
    if($phase -eq 'reload'){$alphaArgs=@('-Dalpha.reload=true')+$alphaArgs}
    $alphaProcess=Start-Process -FilePath "$JavaHome\bin\java.exe" -ArgumentList $alphaArgs -WorkingDirectory $alphaApp -PassThru -WindowStyle Hidden -RedirectStandardOutput (Join-Path $alphaEvidence 'stdout.log') -RedirectStandardError (Join-Path $alphaEvidence 'stderr.log')
    try {
        if(-not $alphaProcess.WaitForExit(60000)){throw 'Bounded alpha check timed out'}
        Get-Content -LiteralPath (Join-Path $alphaEvidence 'result.txt')
        if(-not(Select-String -LiteralPath (Join-Path $alphaEvidence 'result.txt') -Pattern '^Released-JAR alpha checks=PASS$' -Quiet)){
            throw 'Alpha assertion failed'
        }
    }finally{if(-not $alphaProcess.HasExited){Stop-Process -Id $alphaProcess.Id}}
}
if((Get-FileHash -LiteralPath $alphaJar -Algorithm SHA256).Hash.ToLowerInvariant() -ne $alphaHash){
    throw 'JAR changed'
}
