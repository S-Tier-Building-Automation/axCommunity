#Requires -Version 5.1
<#
.SYNOPSIS
  System-tray app to start/stop a GitHub Actions self-hosted runner (no Windows service).
#>
Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

# Only one tray instance per user session.
$script:InstanceMutex = New-Object System.Threading.Mutex($false, 'Local\axCommunity-GitHub-Runner-Tray')
if (-not $script:InstanceMutex.WaitOne(0, $false)) {
    [System.Windows.Forms.MessageBox]::Show(
        'The axCommunity runner tray app is already running.`n`nCheck the notification area (system tray) for the colored dot icon.',
        'axCommunity GitHub Runner',
        [System.Windows.Forms.MessageBoxButtons]::OK,
        [System.Windows.Forms.MessageBoxIcon]::Information
    ) | Out-Null
    exit 0
}

$script:AppName = 'axCommunity GitHub Runner'
$script:ConfigDir = Join-Path $env:APPDATA 'axCommunity\github-runner-tray'
$script:ConfigPath = Join-Path $script:ConfigDir 'config.json'
$script:LogPath = Join-Path $script:ConfigDir 'runner.log'
$script:DefaultConfigPath = Join-Path $PSScriptRoot 'config.default.json'

enum RunnerState {
    Stopped
    Starting
    Listening
    RunningJob
    Error
}

$script:State = [RunnerState]::Stopped
$script:RunnerProcess = $null
$script:NotifyIcon = $null
$script:StatusTimer = $null
$script:Config = $null
function Ensure-Config {
    if (-not (Test-Path $script:ConfigDir)) {
        New-Item -ItemType Directory -Path $script:ConfigDir -Force | Out-Null
    }

    if (-not (Test-Path $script:ConfigPath)) {
        if (Test-Path $script:DefaultConfigPath) {
            Copy-Item $script:DefaultConfigPath $script:ConfigPath
        } else {
            @{
                runnerRoot = 'C:\actions-runner\axCommunity-niagara-build'
                niagaraHome = 'C:\TAC\Niagara-4.15.3.28'
                niagaraUserHome = "$env:USERPROFILE\Niagara4.15\TAC"
                autoStartRunner = $true
                startWithWindows = $false
                runnerName = 'GitHub Actions runner'
            } | ConvertTo-Json | Set-Content -Path $script:ConfigPath -Encoding UTF8
        }
    }

    $script:Config = Get-Content $script:ConfigPath -Raw | ConvertFrom-Json

    if ([string]::IsNullOrWhiteSpace($script:Config.runnerRoot)) {
        throw "config.json is missing runnerRoot"
    }
}

function Write-RunnerLog {
    param([string]$Line)
    $stamp = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss')
    $entry = "[$stamp] $Line"
    Add-Content -Path $script:LogPath -Value $entry -Encoding UTF8
}

function New-StatusIcon {
    param(
        [ValidateSet('gray', 'green', 'yellow', 'red')]
        [string]$Color
    )

    $map = @{
        gray = [System.Drawing.Color]::FromArgb(160, 160, 160)
        green = [System.Drawing.Color]::FromArgb(34, 197, 94)
        yellow = [System.Drawing.Color]::FromArgb(234, 179, 8)
        red = [System.Drawing.Color]::FromArgb(239, 68, 68)
    }

    $bmp = New-Object System.Drawing.Bitmap 16, 16
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $brush = New-Object System.Drawing.SolidBrush $map[$Color]
    $g.FillEllipse($brush, 1, 1, 14, 14)
    $g.Dispose()
    $brush.Dispose()

    $icon = [System.Drawing.Icon]::FromHandle($bmp.GetHicon())
    return @($icon, $bmp)
}

function Set-RunnerState {
    param(
        [RunnerState]$NewState,
        [string]$Detail = ''
    )

    $script:State = $NewState
    $color = switch ($NewState) {
        ([RunnerState]::Stopped) { 'gray' }
        ([RunnerState]::Starting) { 'yellow' }
        ([RunnerState]::Listening) { 'green' }
        ([RunnerState]::RunningJob) { 'yellow' }
        ([RunnerState]::Error) { 'red' }
    }

    $label = switch ($NewState) {
        ([RunnerState]::Stopped) { 'Stopped' }
        ([RunnerState]::Starting) { 'Starting…' }
        ([RunnerState]::Listening) { 'Listening for jobs' }
        ([RunnerState]::RunningJob) { 'Running job' }
        ([RunnerState]::Error) { 'Error' }
    }

    if ($script:NotifyIcon) {
        if ($script:NotifyIcon.Tag) {
            $script:NotifyIcon.Tag.Dispose()
        }
        if ($script:NotifyIcon.Icon) {
            $script:NotifyIcon.Icon.Dispose()
        }

        $created = New-StatusIcon -Color $color
        $script:NotifyIcon.Icon = $created[0]
        $script:NotifyIcon.Tag = $created[1]

        $title = $script:Config.runnerName
        if ($Detail) {
            $tooltip = "$title`n$label - $Detail"
        } else {
            $tooltip = "$title`n$label"
        }
        $maxLen = [Math]::Min(63, $tooltip.Length)
        $script:NotifyIcon.Text = $tooltip.Substring(0, $maxLen)
    }
}

function Test-RunnerListener {
    $listener = Get-Process -Name 'Runner.Listener' -ErrorAction SilentlyContinue
    return $null -ne $listener
}

function Test-RunnerWorker {
    $worker = Get-Process -Name 'Runner.Worker' -ErrorAction SilentlyContinue
    return $null -ne $worker
}

function Update-StatusFromProcesses {
    if (Test-RunnerWorker) {
        if ($script:State -ne [RunnerState]::RunningJob) {
            Set-RunnerState -NewState ([RunnerState]::RunningJob)
        }
        return
    }

    if (Test-RunnerListener) {
        if ($script:State -in @([RunnerState]::Stopped, [RunnerState]::Starting, [RunnerState]::Error)) {
            Set-RunnerState -NewState ([RunnerState]::Listening)
        }
        return
    }

    if ($script:State -ne [RunnerState]::Stopped -and $null -eq $script:RunnerProcess) {
        Set-RunnerState -NewState ([RunnerState]::Stopped)
    }
}

function Invoke-RunnerOutputLine {
    param([string]$Line)

    if ([string]::IsNullOrWhiteSpace($Line)) { return }

    Write-RunnerLog $Line

    if ($Line -match 'Listening for Jobs') {
        Set-RunnerState -NewState ([RunnerState]::Listening)
    } elseif ($Line -match 'Running job:') {
        Set-RunnerState -NewState ([RunnerState]::RunningJob) -Detail ($Line -replace '.*Running job:\s*', '')
    } elseif ($Line -match 'completed with result:\s*(Failed|Canceled)') {
        Set-RunnerState -NewState ([RunnerState]::Error) -Detail $Matches[1]
    } elseif ($Line -match 'completed with result:\s*Succeeded') {
        if (Test-RunnerListener) {
            Set-RunnerState -NewState ([RunnerState]::Listening)
        }
    } elseif ($Line -imatch 'error|fatal|failed to connect') {
        Set-RunnerState -NewState ([RunnerState]::Error) -Detail ($Line.Substring(0, [Math]::Min(40, $Line.Length)))
    }
}

function Start-Runner {
    Ensure-Config

    if (Test-RunnerListener) {
        [System.Windows.Forms.MessageBox]::Show(
            'Runner.Listener is already running.',
            $script:AppName,
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Information
        ) | Out-Null
        Set-RunnerState -NewState ([RunnerState]::Listening)
        return
    }

    $runCmd = Join-Path $script:Config.runnerRoot 'run.cmd'
    if (-not (Test-Path $runCmd)) {
        [System.Windows.Forms.MessageBox]::Show(
            "run.cmd not found:`n$runCmd`n`nEdit config.json and set runnerRoot to your actions-runner folder.",
            $script:AppName,
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Error
        ) | Out-Null
        Set-RunnerState -NewState ([RunnerState]::Error) -Detail 'run.cmd missing'
        return
    }

    Set-RunnerState -NewState ([RunnerState]::Starting)
    Write-RunnerLog '--- Starting runner ---'

    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = 'cmd.exe'
    $psi.Arguments = '/c run.cmd'
    $psi.WorkingDirectory = $script:Config.runnerRoot
    $psi.UseShellExecute = $false
    $psi.CreateNoWindow = $true
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true

    if ($script:Config.niagaraHome) {
        $psi.Environment['NIAGARA_HOME'] = [string]$script:Config.niagaraHome
    }
    if ($script:Config.niagaraUserHome) {
        $psi.Environment['NIAGARA_USER_HOME'] = [string]$script:Config.niagaraUserHome
    }

    $proc = New-Object System.Diagnostics.Process
    $proc.StartInfo = $psi
    $proc.EnableRaisingEvents = $true

    $stdoutHandler = {
        if ($EventArgs.Data) {
            Invoke-RunnerOutputLine -Line $EventArgs.Data
        }
    }.GetNewClosure()

    $stderrHandler = {
        if ($EventArgs.Data) {
            Invoke-RunnerOutputLine -Line $EventArgs.Data
        }
    }.GetNewClosure()

    $exitHandler = {
        Write-RunnerLog 'Runner process exited.'
        $script:RunnerProcess = $null
        if ($script:State -ne [RunnerState]::Stopped) {
            if (Test-RunnerListener) {
                Set-RunnerState -NewState ([RunnerState]::Listening)
            } else {
                Set-RunnerState -NewState ([RunnerState]::Stopped)
            }
        }
    }.GetNewClosure()

    Register-ObjectEvent -InputObject $proc -EventName OutputDataReceived -Action $stdoutHandler | Out-Null
    Register-ObjectEvent -InputObject $proc -EventName ErrorDataReceived -Action $stderrHandler | Out-Null
    Register-ObjectEvent -InputObject $proc -EventName Exited -Action $exitHandler | Out-Null

    [void]$proc.Start()
    $proc.BeginOutputReadLine()
    $proc.BeginErrorReadLine()
    $script:RunnerProcess = $proc
}

function Stop-Runner {
    Write-RunnerLog '--- Stopping runner ---'

    foreach ($name in @('Runner.Worker', 'Runner.Listener')) {
        Get-Process -Name $name -ErrorAction SilentlyContinue | ForEach-Object {
            Write-RunnerLog "Stopping $($_.ProcessName) (pid $($_.Id))"
            Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
        }
    }

    if ($script:RunnerProcess -and -not $script:RunnerProcess.HasExited) {
        try {
            $script:RunnerProcess.Kill()
        } catch {
            # already gone
        }
    }
    $script:RunnerProcess = $null
    Set-RunnerState -NewState ([RunnerState]::Stopped)
}

function Open-Config {
    Ensure-Config
    Start-Process notepad.exe $script:ConfigPath
}

function Open-Log {
    Ensure-Config
    if (-not (Test-Path $script:LogPath)) {
        New-Item -ItemType File -Path $script:LogPath -Force | Out-Null
    }
    Start-Process notepad.exe $script:LogPath
}

function Open-RunnerFolder {
    Ensure-Config
    if (Test-Path $script:Config.runnerRoot) {
        Start-Process explorer.exe $script:Config.runnerRoot
    } else {
        [System.Windows.Forms.MessageBox]::Show(
            "Runner folder not found:`n$($script:Config.runnerRoot)",
            $script:AppName,
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Warning
        ) | Out-Null
    }
}

function Exit-TrayApp {
    Stop-Runner
    if ($script:StatusTimer) {
        $script:StatusTimer.Stop()
        $script:StatusTimer.Dispose()
    }
    if ($script:NotifyIcon) {
        $script:NotifyIcon.Visible = $false
        if ($script:NotifyIcon.Icon) { $script:NotifyIcon.Icon.Dispose() }
        if ($script:NotifyIcon.Tag) { $script:NotifyIcon.Tag.Dispose() }
        $script:NotifyIcon.Dispose()
    }
    [System.Windows.Forms.Application]::Exit()
}

function Initialize-Tray {
    Ensure-Config

    $menu = New-Object System.Windows.Forms.ContextMenuStrip

    $startItem = $menu.Items.Add('Start runner')
    $startItem.Add_Click({ Start-Runner })

    $stopItem = $menu.Items.Add('Stop runner')
    $stopItem.Add_Click({ Stop-Runner })

    [void]$menu.Items.Add('-')

    $configItem = $menu.Items.Add('Edit config…')
    $configItem.Add_Click({ Open-Config })

    $logItem = $menu.Items.Add('Open log…')
    $logItem.Add_Click({ Open-Log })

    $folderItem = $menu.Items.Add('Open runner folder…')
    $folderItem.Add_Click({ Open-RunnerFolder })

    [void]$menu.Items.Add('-')

    $startupItem = $menu.Items.Add('Run at Windows sign-in')
    $startupItem.Add_Click({
        $installScript = Join-Path $PSScriptRoot 'Install-StartupShortcut.ps1'
        Start-Process powershell.exe -ArgumentList @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-File', $installScript)
    })

    [void]$menu.Items.Add('-')

    $quitItem = $menu.Items.Add('Quit')
    $quitItem.Add_Click({ Exit-TrayApp })

    $created = New-StatusIcon -Color 'gray'
    $script:NotifyIcon = New-Object System.Windows.Forms.NotifyIcon
    $script:NotifyIcon.Icon = $created[0]
    $script:NotifyIcon.Tag = $created[1]
    $script:NotifyIcon.Text = $script:Config.runnerName
    $script:NotifyIcon.ContextMenuStrip = $menu
    $script:NotifyIcon.Visible = $true

    $script:StatusTimer = New-Object System.Windows.Forms.Timer
    $script:StatusTimer.Interval = 3000
    $script:StatusTimer.Add_Tick({ Update-StatusFromProcesses })
    $script:StatusTimer.Start()

    Set-RunnerState -NewState ([RunnerState]::Stopped)

    if ($script:Config.autoStartRunner -eq $true) {
        Start-Runner
    }
}

try {
    Initialize-Tray
    [System.Windows.Forms.Application]::Run()
} finally {
    Stop-Runner
    if ($script:InstanceMutex) {
        try { $script:InstanceMutex.ReleaseMutex() } catch { }
        $script:InstanceMutex.Dispose()
    }
}
