<#
.SYNOPSIS
    Dev build cycle for axCommunity: close Workbench -> stop Niagara -> build +
    dev-sign the modules -> deploy to niagara_home/modules -> start Niagara ->
    launch Workbench.

.DESCRIPTION
    Builds the N4 module parts and signs them with an auto-generated self-signed
    dev certificate (alias "axCommunityDev", via jarsigner — no signing config
    required), copies the signed jars into <niagara_home>/modules, and restarts
    the Niagara Windows service so the new build loads for testing.

    Compile against the OLDEST supported Niagara (4.10.x) so the jars stay
    forward-compatible (load on 4.10 through 4.15+). Signing needs a full JDK 8
    (a Niagara JRE has no jarsigner); this script auto-selects one.

    Must run as Administrator (service stop/start); self-elevates if needed.

.PARAMETER NiagaraHome
    Niagara install path. Default: read from N4/gradle.properties.local, else
    newest C:\Niagara\niagara-* / C:\TAC\niagara-*.

.PARAMETER SkipBuild
    Skip the Gradle build (just redeploy existing jars + restart).

.PARAMETER NoWorkbench
    Do not launch Workbench afterward.

.PARAMETER StationFoxPort
    TCP port polled after restart to confirm the station is accepting
    connections (default 4911 = foxs). Override if your station differs.

.PARAMETER StationReadyTimeoutSec
    Max seconds to wait for StationFoxPort before continuing anyway (default 30).

.EXAMPLE
    .\scripts\build-and-restart.ps1
.EXAMPLE
    .\scripts\build-and-restart.ps1 -NoWorkbench
#>
param(
    [string]$NiagaraHome,
    [switch]$SkipBuild,
    [switch]$NoWorkbench,
    # Port polled after restart to detect the station is accepting connections
    # (default foxs = 4911). If your station uses a different port, override it.
    [int]$StationFoxPort = 4911,
    [int]$StationReadyTimeoutSec = 30
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$ServiceName = "Niagara"
$WbProcessName = "wb", "wb_w"
$ScriptRoot = $PSScriptRoot
$ProjectRoot = Split-Path $ScriptRoot -Parent
$N4Dir = Join-Path $ProjectRoot "N4"
$LogFile = Join-Path $ProjectRoot "build-and-restart.log"
$Parts = @("axCommunity-rt", "axCommunity-wb", "axCommunity-ux", "axCommunity-doc")

# Resolve the active signing alias from gradle.properties.local (commented lines
# are skipped). Falls back to the auto-generated self-signed dev cert when none
# is set (see N4/gradle/signing.gradle).
$SigningAlias = "axCommunityDev"
$PropsFileForAlias = Join-Path $N4Dir "gradle.properties.local"
if (Test-Path $PropsFileForAlias) {
    $aliasMatch = Select-String -Path $PropsFileForAlias -Pattern '^\s*signing\.alias\s*=\s*(.+)$'
    if ($aliasMatch) { $SigningAlias = $aliasMatch.Matches[0].Groups[1].Value.Trim() }
}

# Machine-local station port: read the optional station_fox_port key from
# gradle.properties.local unless -StationFoxPort was passed explicitly. Lets a
# machine whose station isn't on 4911 set it once alongside niagara_home.
if (-not $PSBoundParameters.ContainsKey('StationFoxPort') -and (Test-Path $PropsFileForAlias)) {
    $portMatch = Select-String -Path $PropsFileForAlias -Pattern '^\s*station_fox_port\s*=\s*(\d+)\s*$'
    if ($portMatch) { $StationFoxPort = [int]$portMatch.Matches[0].Groups[1].Value }
}

# ---------- Resolve Niagara home ----------
if (-not $NiagaraHome) {
    $propsFile = Join-Path $N4Dir "gradle.properties.local"
    if (Test-Path $propsFile) {
        $m = Select-String -Path $propsFile -Pattern '^\s*niagara_home\s*=\s*(.+)$'
        if ($m) { $NiagaraHome = $m.Matches[0].Groups[1].Value.Trim() -replace '/', '\' }
    }
    if (-not $NiagaraHome) {
        foreach ($base in 'C:\Niagara', 'C:\TAC') {
            if (Test-Path $base) {
                # Windows FS is case-insensitive so this matches 'niagara-*' too.
                $found = Get-ChildItem $base -Directory -Filter 'niagara-*' -EA SilentlyContinue |
                    Sort-Object { try { [Version]($_.Name -replace '(?i)^niagara-', '') } catch { [Version]'0.0' } } -Descending |
                    Select-Object -First 1
                if ($found) { $NiagaraHome = $found.FullName; break }
            }
        }
    }
    if (-not $NiagaraHome) { $NiagaraHome = "C:\TAC\niagara-4.10.11.12" }
}

# Gradle needs a JDK with javac; signing needs jarsigner. A Niagara JRE has
# javac but NOT jarsigner, so prefer a full JDK 8. Order: current JAVA_HOME (if
# it has jarsigner), newest Eclipse Adoptium JDK, else the Niagara JRE (the build
# then warns that signing needs a JDK / -Psigning.jdkHome).
function Test-HasTool { param($home, $tool) $home -and (Test-Path (Join-Path $home "bin\$tool.exe")) }
if (-not (Test-HasTool $env:JAVA_HOME 'jarsigner')) {
    $jdk = $null
    $adoptium = 'C:\Program Files\Eclipse Adoptium'
    if (Test-Path $adoptium) {
        $jdk = Get-ChildItem $adoptium -Directory -EA SilentlyContinue |
            Where-Object { Test-HasTool $_.FullName 'jarsigner' } |
            Sort-Object Name -Descending | Select-Object -First 1
    }
    if ($jdk) { $env:JAVA_HOME = $jdk.FullName }
    elseif (-not (Test-HasTool $env:JAVA_HOME 'javac')) {
        $bundledJdk = Join-Path $NiagaraHome 'jre'
        if (Test-HasTool $bundledJdk 'javac') { $env:JAVA_HOME = $bundledJdk }
    }
}

$WbExeStd = Join-Path $NiagaraHome "bin\wb.exe"
$WbExe = if (Test-Path $WbExeStd) { $WbExeStd } else { Join-Path $NiagaraHome "bin\wb_w.exe" }
$ModulesDir = Join-Path $NiagaraHome "modules"

function Write-Step { param([string]$m) Write-Host ""; Write-Host "=== $m ===" -ForegroundColor Cyan }
function Test-Administrator {
    $id = [Security.Principal.WindowsIdentity]::GetCurrent()
    (New-Object Security.Principal.WindowsPrincipal($id)).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# ---------- Self-elevate ----------
if (-not (Test-Administrator)) {
    Write-Host "Not Administrator. Re-launching elevated (accept the UAC prompt)..." -ForegroundColor Yellow
    $argList = "-ExecutionPolicy Bypass -File `"$($MyInvocation.MyCommand.Path)`""
    if ($SkipBuild) { $argList += " -SkipBuild" }
    if ($NoWorkbench) { $argList += " -NoWorkbench" }
    if ($NiagaraHome) { $argList += " -NiagaraHome `"$NiagaraHome`"" }
    $argList += " -StationFoxPort $StationFoxPort -StationReadyTimeoutSec $StationReadyTimeoutSec"
    Remove-Item -Path $LogFile -Force -EA SilentlyContinue
    $elevated = Start-Process powershell -Verb RunAs -ArgumentList $argList -PassThru

    # Wait only for the elevated process to exit. Reading the transcript while it
    # is still being written can raise a file-sharing violation (fatal under
    # ErrorActionPreference=Stop), so read the log once, after exit.
    $deadline = (Get-Date).AddMinutes(30); $completed = $false
    while ((Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 2
        if (-not (Get-Process -Id $elevated.Id -EA SilentlyContinue)) { $completed = $true; break }
    }
    $logText = if (Test-Path $LogFile) { Get-Content $LogFile -Raw -EA SilentlyContinue } else { "" }
    if ($logText) {
        Write-Host ""; Write-Host "--- Output from elevated process ---" -ForegroundColor Cyan
        Write-Host $logText
    }
    if (-not $completed) { Write-Host "ERROR: Timed out waiting for elevated run." -ForegroundColor Red; exit 1 }
    if ($logText -match "BUILD FAILED|ERROR:") { exit 1 }
    exit 0
}

# ---------- Elevated: start logging ----------
try { Stop-Transcript -EA SilentlyContinue } catch {}
Start-Transcript -Path $LogFile -Force | Out-Null

if (-not $NoWorkbench -and -not (Test-Path $WbExe)) {
    Write-Host "ERROR: Workbench not found at $WbExe" -ForegroundColor Red; exit 1
}

$startTime = Get-Date
Write-Host ""
Write-Host "axCommunity Build & Restart (signing alias: $SigningAlias)" -ForegroundColor Green
Write-Host "  Project:      $ProjectRoot"
Write-Host "  Niagara Home: $NiagaraHome"
Write-Host "  Service:      $ServiceName"
if ($SigningAlias -eq "S-Tier Building Automation llc") {
    Write-Host "  Official signing via Windows-MY store (cert must be in Cert:\CurrentUser\My)." -ForegroundColor Cyan
    Write-Host "  If the key is on a disconnected eToken, connect it + set a USER-level SAFENET_PIN." -ForegroundColor Cyan
}

# ---------- Step 1: Close Workbench ----------
Write-Step "Step 1: Closing Workbench"
$wbProc = Get-Process -Name $WbProcessName -EA SilentlyContinue
if ($wbProc) {
    $wbProc | ForEach-Object { $_.CloseMainWindow() | Out-Null }
    $wbProc | Wait-Process -Timeout 15 -EA SilentlyContinue
    $wbProc = Get-Process -Name $WbProcessName -EA SilentlyContinue
    if ($wbProc) { $wbProc | Stop-Process -Force; Start-Sleep -Seconds 2 }
    Write-Host "  Workbench closed." -ForegroundColor Green
} else { Write-Host "  Workbench not running." }

# ---------- Step 2: Stop Niagara ----------
Write-Step "Step 2: Stopping Niagara service"
$svc = Get-Service -Name $ServiceName -EA SilentlyContinue
if (-not $svc) { Write-Host "  WARNING: service '$ServiceName' not found." -ForegroundColor Yellow }
elseif ($svc.Status -eq 'Running') {
    Stop-Service -Name $ServiceName -Force
    $svc.WaitForStatus('Stopped', [TimeSpan]::FromSeconds(60))
    $timeout = 30
    while ((Get-Process -Name "niagarad" -EA SilentlyContinue) -and $timeout -gt 0) { Start-Sleep -Seconds 1; $timeout-- }
    Write-Host "  Service stopped." -ForegroundColor Green
} else { Write-Host "  Service already stopped ($($svc.Status))." }

# ---------- Step 3: Build + deploy ----------
if (-not $SkipBuild) {
    Write-Step "Step 3: Building + dev-signing modules"
    Push-Location $N4Dir
    try {
        # Note: no --no-daemon. The single-use daemon's shutdown can emit a
        # spurious "Could not dispatch a message to the daemon" non-zero exit
        # even on a successful build; the reusable daemon avoids that.
        #
        # clean and assemble run as separate invocations: with the Gradle 4.10.x
        # toolchain a combined `clean assemble` can execute clean out of order and
        # wipe the freshly built jars.
        $commonArgs = @("-Pniagara_home=$NiagaraHome", "--console=plain")
        Write-Host "  Running: gradlew clean $($commonArgs -join ' ')"
        & .\gradlew.bat clean @commonArgs
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  BUILD FAILED (clean, exit $LASTEXITCODE). Service is stopped; fix and re-run." -ForegroundColor Red
            exit $LASTEXITCODE
        }
        Write-Host "  Running: gradlew assemble $($commonArgs -join ' ')"
        & .\gradlew.bat assemble @commonArgs
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  BUILD FAILED (assemble, exit $LASTEXITCODE). Service is stopped; fix and re-run." -ForegroundColor Red
            exit $LASTEXITCODE
        }
    } finally { Pop-Location }

    Write-Step "Step 3b: Verifying signatures + deploying jars to $ModulesDir"
    # Gate on jarsigner -verify so an unsigned / mis-signed build never reaches
    # the station (a common cause of "-wb types missing" head-scratching).
    $jarsigner = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\jarsigner.exe' } else { $null }
    $canVerify = $jarsigner -and (Test-Path $jarsigner)
    if (-not $canVerify) {
        Write-Host "  NOTE: jarsigner not found under JAVA_HOME; skipping the signature gate." -ForegroundColor Yellow
    }
    $deployed = 0; $unchanged = 0
    foreach ($part in $Parts) {
        $src = Join-Path $N4Dir "$part\build\libs\$part.jar"
        if (-not (Test-Path $src)) { Write-Host "  BUILD FAILED: missing $src" -ForegroundColor Red; exit 1 }

        if ($canVerify) {
            $vout = & $jarsigner -verify $src 2>&1 | Out-String
            if ($vout -notmatch 'jar verified') {
                Write-Host "  SIGNATURE CHECK FAILED for $part.jar — not deploying:" -ForegroundColor Red
                Write-Host ("    " + ((($vout -split "`r?`n") | Where-Object { $_ }) -join "`n    "))
                exit 1
            }
        }

        # Best-effort idempotent deploy: skip parts whose bytes are byte-identical
        # to what's already installed (less flash wear, and it's obvious which
        # parts actually changed). Falls through to a copy whenever they differ.
        $dest = Join-Path $ModulesDir "$part.jar"
        $srcHash = (Get-FileHash $src -Algorithm SHA256).Hash
        $destHash = if (Test-Path $dest) { (Get-FileHash $dest -Algorithm SHA256).Hash } else { $null }
        if ($srcHash -eq $destHash) {
            Write-Host ("  Unchanged: {0} (skipped)" -f "$part.jar")
            $unchanged++
            continue
        }
        Copy-Item $src $dest -Force
        $item = Get-Item $dest
        Write-Host ("  Deployed:  {0} ({1:N0} bytes)" -f "$part.jar", $item.Length)
        $deployed++
    }
    Write-Host ("  Build + deploy successful ($deployed changed, $unchanged unchanged).") -ForegroundColor Green
} else { Write-Step "Step 3: Build skipped (-SkipBuild)" }

# ---------- Step 4: Start Niagara ----------
Write-Step "Step 4: Starting Niagara service"
$svc = Get-Service -Name $ServiceName -EA SilentlyContinue
if (-not $svc) { Write-Host "  WARNING: service '$ServiceName' not found." -ForegroundColor Yellow }
elseif ($svc.Status -ne 'Running') {
    Start-Service -Name $ServiceName
    $svc.WaitForStatus('Running', [TimeSpan]::FromSeconds(60))
    # Adaptive readiness: poll the station's fox port instead of a blind sleep —
    # returns as soon as the station accepts connections (usually faster), and
    # caps at StationReadyTimeoutSec so a differently-configured station never
    # hangs the run.
    Write-Host "  Service started; waiting for station on port $StationFoxPort (up to ${StationReadyTimeoutSec}s)..."
    $ready = $false
    $deadline = (Get-Date).AddSeconds($StationReadyTimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $client = New-Object Net.Sockets.TcpClient
            $async = $client.BeginConnect('127.0.0.1', $StationFoxPort, $null, $null)
            if ($async.AsyncWaitHandle.WaitOne(1000) -and $client.Connected) { $ready = $true }
            $client.Close()
        } catch {}
        if ($ready) { break }
        Start-Sleep -Milliseconds 500
    }
    if ($ready) { Write-Host "  Station reachable on port $StationFoxPort." -ForegroundColor Green }
    else { Write-Host "  Port $StationFoxPort not detected within ${StationReadyTimeoutSec}s (station may use a different port); continuing." -ForegroundColor Yellow }
} else { Write-Host "  Service already running." }

# ---------- Step 5: Launch Workbench ----------
if (-not $NoWorkbench) {
    Write-Step "Step 5: Launching Workbench"
    if (Get-Process -Name $WbProcessName -EA SilentlyContinue) { Write-Host "  Workbench already running." }
    else { Start-Process -FilePath $WbExe; Write-Host "  Workbench launched." -ForegroundColor Green }
} else { Write-Step "Step 5: Workbench launch skipped (-NoWorkbench)" }

$elapsed = [math]::Round(((Get-Date) - $startTime).TotalSeconds)
Write-Host ""
Write-Host "=== Complete! (${elapsed}s) ===" -ForegroundColor Green
Write-Host "axCommunity-rt/-wb/-ux/-doc deployed and Niagara restarted."
Write-Host "Signed with: $SigningAlias. If -wb types don't appear, trust that self-signed dev cert in the Workbench User Trust Store."
Stop-Transcript | Out-Null
