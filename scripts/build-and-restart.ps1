<#
.SYNOPSIS
    Dev build cycle for axCommunity: close Workbench -> stop Niagara -> build +
    dev-sign the modules -> deploy to niagara_home/modules -> start Niagara ->
    launch Workbench.

.DESCRIPTION
    Builds the N4 module parts with the default Niagara dev certificate
    ("Niagara4Modules" — no signing.profile required), copies the signed jars
    into <niagara_home>/modules, and restarts the Niagara Windows service so the
    new build loads for testing.

    Must run as Administrator (service stop/start); self-elevates if needed.

.PARAMETER NiagaraHome
    Niagara install path. Default: read from N4/gradle.properties.local, else
    newest C:\Niagara\Niagara-* / C:\TAC\Niagara-*.

.PARAMETER SkipBuild
    Skip the Gradle build (just redeploy existing jars + restart).

.PARAMETER NoWorkbench
    Do not launch Workbench afterward.

.EXAMPLE
    .\scripts\build-and-restart.ps1
.EXAMPLE
    .\scripts\build-and-restart.ps1 -NoWorkbench
#>
param(
    [string]$NiagaraHome,
    [switch]$SkipBuild,
    [switch]$NoWorkbench
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
                $found = Get-ChildItem $base -Directory -Filter 'Niagara-*' -EA SilentlyContinue |
                    Sort-Object Name -Descending | Select-Object -First 1
                if ($found) { $NiagaraHome = $found.FullName; break }
            }
        }
    }
    if (-not $NiagaraHome) { $NiagaraHome = "C:\TAC\Niagara-4.15.3.28" }
}

# Gradle needs a JDK; Niagara bundles one under <home>\jre (includes javac).
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) {
    $bundledJdk = Join-Path $NiagaraHome 'jre'
    if (Test-Path (Join-Path $bundledJdk 'bin\javac.exe')) { $env:JAVA_HOME = $bundledJdk }
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
Write-Host "axCommunity Build & Restart (dev cert: Niagara4Modules)" -ForegroundColor Green
Write-Host "  Project:      $ProjectRoot"
Write-Host "  Niagara Home: $NiagaraHome"
Write-Host "  Service:      $ServiceName"

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
        $buildArgs = @("-Pniagara_home=$NiagaraHome", "clean", "assemble", "--no-daemon", "--console=plain")
        Write-Host "  Running: gradlew $($buildArgs -join ' ')"
        & .\gradlew.bat @buildArgs
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  BUILD FAILED (exit $LASTEXITCODE). Service is stopped; fix and re-run." -ForegroundColor Red
            exit $LASTEXITCODE
        }
    } finally { Pop-Location }

    Write-Step "Step 3b: Deploying jars to $ModulesDir"
    foreach ($part in $Parts) {
        $src = Join-Path $N4Dir "$part\build\libs\$part.jar"
        if (-not (Test-Path $src)) { Write-Host "  BUILD FAILED: missing $src" -ForegroundColor Red; exit 1 }
        Copy-Item $src (Join-Path $ModulesDir "$part.jar") -Force
        $item = Get-Item (Join-Path $ModulesDir "$part.jar")
        Write-Host ("  Deployed: {0} ({1:N0} bytes)" -f "$part.jar", $item.Length)
    }
    Write-Host "  Build + deploy successful." -ForegroundColor Green
} else { Write-Step "Step 3: Build skipped (-SkipBuild)" }

# ---------- Step 4: Start Niagara ----------
Write-Step "Step 4: Starting Niagara service"
$svc = Get-Service -Name $ServiceName -EA SilentlyContinue
if (-not $svc) { Write-Host "  WARNING: service '$ServiceName' not found." -ForegroundColor Yellow }
elseif ($svc.Status -ne 'Running') {
    Start-Service -Name $ServiceName
    $svc.WaitForStatus('Running', [TimeSpan]::FromSeconds(60))
    Write-Host "  Service started; waiting 15s for station init..."
    Start-Sleep -Seconds 15
    Write-Host "  Service running." -ForegroundColor Green
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
$alias = 'Niagara4Modules'
$pf = Join-Path $N4Dir 'gradle.properties.local'
if (Test-Path $pf) {
    $am = Select-String -Path $pf -Pattern '^\s*signing\.alias\s*=\s*(.+)$'
    if ($am) { $alias = $am.Matches[0].Groups[1].Value.Trim() }
}
Write-Host "Signed with dev cert: $alias. If -wb types don't appear, trust that cert in the Workbench User Trust Store."
Stop-Transcript | Out-Null
