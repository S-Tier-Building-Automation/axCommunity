#Requires -Version 5.1
<#
.SYNOPSIS
  Add or remove a Startup folder shortcut for the GitHub runner tray app.
#>
[CmdletBinding()]
param(
    [switch]$Remove
)

$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$launcher = Join-Path $scriptDir 'Start-RunnerTray.cmd'
$startup = [Environment]::GetFolderPath('Startup')
$shortcutPath = Join-Path $startup 'axCommunity GitHub Runner.lnk'

if (-not (Test-Path $launcher)) {
    throw "Launcher not found: $launcher"
}

if ($Remove) {
    if (Test-Path $shortcutPath) {
        Remove-Item $shortcutPath -Force
        Write-Host "Removed startup shortcut: $shortcutPath"
    } else {
        Write-Host "No startup shortcut found."
    }
    exit 0
}

$wsh = New-Object -ComObject WScript.Shell
$shortcut = $wsh.CreateShortcut($shortcutPath)
$shortcut.TargetPath = $launcher
$shortcut.WorkingDirectory = $scriptDir
$shortcut.WindowStyle = 7  # Minimized
$shortcut.Description = 'axCommunity self-hosted GitHub Actions runner (tray app)'
$shortcut.Save()

Write-Host "Created startup shortcut: $shortcutPath"
Write-Host "The tray app will launch when you sign in to Windows."
