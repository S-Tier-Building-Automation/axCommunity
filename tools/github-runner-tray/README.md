# GitHub Runner Tray App

Lightweight Windows tray app to run a **self-hosted GitHub Actions runner** in your user session — no Windows service, no terminal window left open.

Use this for repos like [axCommunity](https://github.com/S-Tier-Building-Automation/axCommunity) that need a local Niagara install for CI (`niagara-build` label).

## What it does

- Starts `run.cmd` from your actions-runner folder (hidden, no console)
- Shows tray status: **stopped** (gray), **listening** (green), **running job** (yellow), **error** (red)
- Writes runner output to `%APPDATA%\axCommunity\github-runner-tray\runner.log`
- Passes `NIAGARA_HOME` / `NIAGARA_USER_HOME` from config into the runner process
- Optional auto-start of the runner when the tray app opens
- Optional “run at Windows sign-in” shortcut (Startup folder — still not a service)

## Quick start

> **Use only one launcher.** Either this tray app **or** a terminal running `run.cmd` — not both. GitHub allows one active listener per runner registration; two launchers fight over the same registration and look like "two runners" in Task Manager even when only one is online.

1. **Register the runner once** (if you have not already):

   ```powershell
   mkdir C:\actions-runner\axCommunity-niagara-build
   cd C:\actions-runner\axCommunity-niagara-build
   # Download actions-runner-win-x64-*.zip from GitHub repo Settings → Actions → Runners
   # Extract here, then:
   .\config.cmd --url https://github.com/S-Tier-Building-Automation/axCommunity --token <TOKEN> --name DESKTOP-xxx-axc --labels niagara-build --unattended
   ```

2. **Edit config** (first launch creates it from `config.default.json`):

   Path: `%APPDATA%\axCommunity\github-runner-tray\config.json`

   | Field | Purpose |
   |-------|---------|
   | `runnerRoot` | Folder containing `run.cmd` |
   | `niagaraHome` | Niagara install path (CI build) |
   | `niagaraUserHome` | Niagara user home (signing trust) |
   | `autoStartRunner` | Start runner when tray app opens |
   | `startWithWindows` | Reserved; use Install-StartupShortcut instead |
   | `runnerName` | Tray tooltip title |

3. **Launch the tray app**:

   Double-click `Start-RunnerTray.cmd`, or:

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\RunnerTray.ps1
   ```

4. **Run at sign-in** (optional):

   ```powershell
   .\Install-StartupShortcut.ps1
   ```

   Remove with `-Remove`.

## Tray menu

| Item | Action |
|------|--------|
| Start runner | Runs `run.cmd` |
| Stop runner | Stops `Runner.Listener` / `Runner.Worker` |
| Edit config… | Opens `config.json` in Notepad |
| Open log… | Opens `runner.log` |
| Open runner folder… | Opens `runnerRoot` in Explorer |
| Run at Windows sign-in | Creates Startup shortcut (may prompt UAC) |
| Quit | Stops runner and exits |

## vs Windows service

| | Tray app | `svc.cmd` service |
|---|----------|-------------------|
| Runs when | You are signed in | Always (SYSTEM) |
| Admin for install | No | Yes |
| Survives logoff | No | Yes |
| Good for | Dev PC / occasional CI | Dedicated build machine |

For a headless build box, install the official service after registering the runner:

```powershell
cd C:\actions-runner\axCommunity-niagara-build
.\svc.cmd install
.\svc.cmd start
```

## Requirements

- Windows 10/11
- PowerShell 5.1+ (built in)
- GitHub Actions runner already configured in `runnerRoot`

## Repo CI

Set repository variables so the `build-niagara` job runs on your runner:

- `NIAGARA_BUILD_ENABLED=true`
- `NIAGARA_HOME` / `NIAGARA_USER_HOME` (used by the workflow; tray app also sets them for local `run.cmd`)

Label the runner `niagara-build` when registering.
