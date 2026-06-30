# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`axCommunity` is a single Niagara 4 module (module name **`axCommunity`**, vendor **`Community`**, symbol `axc4`) — a maintained fork of the SourceForge "Niagara AX Community Modules" project, ported from Niagara AX to **Niagara 4.15.3**. It is a library of ~100 control/logic/conversion/HVAC components that station programmers drop onto wire sheets. Current version **22.2.2**. Distributed under **GPLv2**.

All active work lives under [`N4/`](N4/). The pre-N4 AX sources under [`archive/AX/`](archive/AX/) are kept for history only — **do not modify them**.

## Build & dev commands

All Gradle commands run from the `N4/` directory. Requires a licensed Niagara **4.15.x** install (the build compiles against its JARs and signs against its cert) and **JDK 8** (Niagara 4.15.3 bundles Zulu 8 under `<niagara_home>/jre`; `JAVA_HOME` must point at a JDK 8, not 11+).

```powershell
cd N4
.\gradlew.bat clean assemble      # compile + dev-sign all four module parts
.\gradlew.bat projects            # fast config-only sanity check (no compile)
```

Signed JARs land in `N4/axCommunity-<part>/build/libs/`. There is **no automated test suite** — the `test`/`moduleTest` tasks have no sources, and `BSuperStringTest` is a runtime *component*, not a JUnit test. Verify changes by deploying to a station and exercising them in Workbench.

**Full dev loop** (build → deploy to `<niagara_home>/modules` → restart Niagara service → launch Workbench), from the repo root:

```powershell
.\scripts\build-and-restart.ps1            # self-elevates for the service stop/start
.\scripts\build-and-restart.ps1 -SkipBuild # redeploy existing jars + restart only
.\scripts\build-and-restart.ps1 -NoWorkbench
```

### Local config (required, gitignored)

`N4/gradle.properties.local` sets machine paths and signing. Copy from [`N4/gradle.properties.example`](N4/gradle.properties.example). `niagara_home` is mandatory; the build derives the Tridium Gradle plugin repo from `<niagara_home>/etc/m2/repository`.

`signing.profile` + `signing.alias` select the code-signing cert (the build wires `signing.alias` into the plugin's `aliases`). With no `signing.profile`, the build falls back to Niagara's auto-generated dev cert **`Niagara4Modules`**. For any `-wb` types to appear in Workbench, the signing cert must be trusted in the Workbench User Trust Store.

This repo's `gradle.properties.local` defaults to **official signing** with the Sectigo OV cert `S-Tier Building Automation llc` (via `STier-API-N4/cert/safenet_signing_profile_windows.xml`, `storetype=Windows-MY`), mirroring STier-API-N4. The cert + private key are installed in this machine's user store (`Cert:\CurrentUser\My`, valid to 2026-08-30), so the build signs all four parts **non-interactively** — no SafeNet token connection or `SAFENET_PIN` prompt is needed here. (The profile references `$env:SAFENET_PIN`; that only matters if the key is moved to a disconnected eToken, in which case connect the token and set `SAFENET_PIN` at **user** level via `setx` so it survives the UAC elevation in `build-and-restart.ps1`.) Verify a signature with `keytool -printcert -jarfile <jar>` or `jarsigner -verify <jar>` (JDK, not the JRE — the JRE has no `jarsigner`). For fast local iteration, flip `gradle.properties.local` to the commented-out **STierDev** dev-cert lines.

## Module-part layout

The module is split into four parts by Niagara **runtime profile**, each its own Gradle subproject with a `<name>.gradle.kts` build file:

| Part | Profile | Contents | Notable deps |
|------|---------|----------|--------------|
| `axCommunity-rt` | `rt` | All ~104 component classes (the bulk of the module) | baja, control-rt, gx-rt, bql-rt, kitControl-rt, alarm-rt, driver-rt, file-rt, converters-rt |
| `axCommunity-wb` | `wb` | Workbench views, widgets, Px graphics; depends on `-rt` | bajaui-wb, workbench-wb, **kitPx-wb** |
| `axCommunity-ux` | `ux` | bajaux — currently empty, kept as a placeholder for future JS/HBS content | baja |
| `axCommunity-doc` | `doc` | Packages the HTML doc tree | baja |

Install **all four** parts together on a station/supervisor. Build settings (plugin versions, `niagara_home` resolution, project discovery via `findProjects()`) live in [`N4/settings.gradle.kts`](N4/settings.gradle.kts); vendor/version/signing defaults in [`N4/build.gradle.kts`](N4/build.gradle.kts).

## Component conventions

Components are classic **Baja `BComponent`/`BPointExtension` subclasses using the manual slot pattern** — *not* the `@NiagaraType` annotation processor (the processor plugin is enabled but unused). When reading or writing a component, follow the existing idiom:

- Slots declared via `newProperty(...)` / `newAction(...)` / `newTopic(...)` with hand-written getter/setter pairs.
- Type registration footer: `public Type getType() { return TYPE; }` and `public static final Type TYPE = Sys.loadType(BFoo.class);`.
- **Every new component type must be registered by hand in [`N4/axCommunity-rt/module-include.xml`](N4/axCommunity-rt/module-include.xml)** with a `<type name="Foo" class="org.axcommunity.niagara.<pkg>.BFoo" />` entry — there is no annotation-driven registration, so a component absent from this file will not load.
- Source packages under `org.axcommunity.niagara.*` group by function: `extensions`, `logic`, `math`, `conversion`, `string`, `time`, `bql`, `hvac`, `weather`, `system`, `web`, `batch`.
- Persisted-state slots that change fast (e.g. filter history) should be `Flags.TRANSIENT` to avoid `config.bog` writes and flash wear on embedded controllers — see [`BFilterExt`](N4/axCommunity-rt/src/org/axcommunity/niagara/extensions/BFilterExt.java) for the rationale.

Resources (icons, bog fragments, relNotes, Px graphics) are pulled into the jar via explicit `sourceSets { main { resources { srcDir("src"); include(...) } } }` blocks in each part's build file — a new resource directory won't be packaged unless its `include` pattern is added there.

Record component changes in [`N4/axCommunity-rt/src/relNotes/RelNotes.txt`](N4/axCommunity-rt/src/relNotes/RelNotes.txt).

## Known runtime limitations (22.2.2)

- **FireFoxxWeather** — palette entry works but the component still calls the defunct Yahoo Weather RSS API; returns no live data until repointed at a new source.
- **PxGraphics palette folder** — HVAC graphics reference the external **johnGraphics** module and embed **kitPx** widgets (`kitPx:BoundLabel`, etc.). Those palette items only resolve if `johnGraphics` and `kitPx-wb`/`kitPx-ux` are installed on the station/supervisor.

## CI/CD & release automation

The PR→release lifecycle is automated with GitHub Actions. Workflows live in [`.github/workflows/`](.github/workflows/):

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| `ci.yml` | push/PR | Hosted smoke-check + (opt-in) self-hosted `assemble`+sign. Emits the **CI Gate** required status. The real compile/sign needs a self-hosted runner with Niagara (set repo var `NIAGARA_BUILD_ENABLED=true`); hosted runners can't resolve the Tridium plugins. |
| `pr-checks.yml` | PR | Conventional-commit **PR title** lint + the **PR Gate** required status. |
| `pr-resolve-threads.yml` | PR push | Resolves outdated, unaddressed review threads. |
| `auto-merge.yml` | PR (`pull_request_target`) | Enables native squash auto-merge for PRs labelled `automerge`, and for Dependabot patch/minor bumps. |
| `release-please.yml` | push to main | [release-please](https://github.com/googleapis/release-please) maintains a Release PR (version bump + `CHANGELOG.md`); merging it tags + cuts a Release, then calls `release-build.yml`. |
| `release-build.yml` | called by release-please / manual | Self-hosted: build + **official-sign** the four jars, verify signers against `cert/trusted-signer-fingerprints.txt`, attach to the Release, and publish to GitHub Packages (Maven). |
| `stale.yml` | daily cron | Marks/closes inactive PRs & issues. |

Key facts for working on this:

- **Versioning is conventional-commit driven.** Squash-merge uses the PR title as the commit subject, so the PR title's type (`feat`→minor, `fix`→patch, `feat!`/`BREAKING CHANGE`→major) is what release-please reads. The single source of truth for the version is the `val moduleVersion = "..." // x-release-please-version` line in [`N4/build.gradle.kts`](N4/build.gradle.kts); release-please rewrites that literal. Don't bump it by hand. The manifest is [`.release-please-manifest.json`](.release-please-manifest.json); config is [`release-please-config.json`](release-please-config.json). Release tags are `vX.Y.Z`.
- **Module jars stay unversioned** (`axCommunity-rt.jar`, not `-22.2.2.jar`) — Niagara installs them that way, and ci.yml/release-build.yml/`build-and-restart.ps1` assume it. Never set `version` on the subprojects in `build.gradle.kts` (it would suffix the archive). The Maven publication carries the version on its own coordinates (`org.axcommunity:axCommunity-<part>:<version>`).
- **Signing in CI:** `release-build.yml` generates a Windows-MY signing profile into the runner's temp dir at runtime — signing profiles are **never committed** because the Tridium plugin rewrites them in place with a concrete storepass (`cert/safenet_signing_profile*.xml` and `cert/*.windows-my` are git-ignored for that reason). It then verifies every jar's signer SHA-256 against `cert/trusted-signer-fingerprints.txt` before shipping; rotating the cert means adding the new fingerprint there.
- **Required checks:** branch protection should require **CI Gate** + **PR Gate** (both report on every PR). `ci.yml` has no `pull_request` paths filter, on purpose — a required check that never reports would stall auto-merge forever.

One-time maintainer setup (repo settings, not code): enable **Allow auto-merge**; set branch protection requiring CI Gate + PR Gate; enable the `niagara-build` self-hosted runner for releases (+ `NIAGARA_BUILD_ENABLED=true` for the CI build gate); optionally set repo secret `SAFENET_PIN` if the signing key becomes token-backed. The self-hosted runner can be run via the tray app in [`tools/github-runner-tray/`](tools/github-runner-tray/).
