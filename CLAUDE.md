# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`axCommunity` is a single Niagara 4 module (module name **`axCommunity`**, vendor **`Community`**, symbol `axc4`) — a maintained fork of the SourceForge "Niagara AX Community Modules" project, ported from Niagara AX to **Niagara 4**. It supports **Niagara 4.10 and newer from one set of jars**: the build compiles against the oldest supported install (4.10.11.12) and stamps each `module.xml` dependency as `>= 4.10`, so the jars load on 4.10 through 4.15+. It is a library of ~100 control/logic/conversion/HVAC components that station programmers drop onto wire sheets. Current version **22.3.0**. Distributed under **GPLv2**.

All active work lives under [`N4/`](N4/). The pre-N4 AX sources under [`archive/AX/`](archive/AX/) are kept for history only — **do not modify them**.

## Build & dev commands

All Gradle commands run from the `N4/` directory. Requires a licensed Niagara **4.10.x** install — compile against the **oldest** version you support (tested on 4.10.11.12) so the jars stay forward-compatible. The build uses the discrete 4.10 toolchain (`niagara-module-plugin` 3.0.18 applied by class, Gradle **4.10.3** wrapper, Groovy build scripts). Needs a full **JDK 8**: `javac` compiles and `jarsigner` signs — a Niagara **JRE has no jarsigner**, so point `JAVA_HOME` at a JDK 8 (e.g. Eclipse Adoptium), not the bundled JRE.

```powershell
cd N4
.\gradlew.bat clean --console=plain      # separate invocation (see note below)
.\gradlew.bat assemble --console=plain   # compile + dev-sign all four module parts
.\gradlew.bat projects                   # fast config-only sanity check (no compile)
```

Run `clean` and `assemble` as **separate** invocations — the Gradle 4.10.x toolchain can otherwise execute `clean` out of order and wipe the fresh jars. `scripts/build-and-restart.ps1`, `ci.yml`, and `release-build.yml` all do this.

Signed JARs land in `N4/axCommunity-<part>/build/libs/`. There is **no automated test suite** — the `test`/`moduleTest` tasks have no sources, and `BSuperStringTest` is a runtime *component*, not a JUnit test. Verify changes by deploying to a station and exercising them in Workbench.

**Full dev loop** (build → deploy to `<niagara_home>/modules` → restart Niagara service → launch Workbench), from the repo root:

```powershell
.\scripts\build-and-restart.ps1            # self-elevates for the service stop/start
.\scripts\build-and-restart.ps1 -SkipBuild # redeploy existing jars + restart only
.\scripts\build-and-restart.ps1 -NoWorkbench
```

### Local config (required, gitignored)

`N4/gradle.properties.local` sets machine paths and signing. Copy from [`N4/gradle.properties.example`](N4/gradle.properties.example). `niagara_home` is mandatory and should point at the **oldest supported (4.10.x)** install; the build resolves module dependencies from `<niagara_home>/modules` and the plugin repo from `<niagara_home>/etc/m2/repository`. Note: `gradle.properties.local` intentionally beats the `NIAGARA_HOME` env var, since that env var often points at a newer install (4.15) while the build must target 4.10.

**Signing** is done by the JDK's `jarsigner` (see [`N4/gradle/signing.gradle`](N4/gradle/signing.gradle)), producing the standard `META-INF/<ALIAS>.SF`/`.RSA` that Niagara verifies against its trust store. With **no `signing.*`** set, the build auto-generates a self-signed dev cert (alias **`axCommunityDev`**, stored under the Gradle user home and reused) and signs with it — trust it once in the Workbench **User Trust Store** so `-wb` types appear. For **official** signing set:

- `signing.keystore` — a keystore path, or `NONE` for an OS store such as Windows-MY
- `signing.alias` — cert alias / friendly name (e.g. `S-Tier Building Automation llc`)
- `signing.storetype` — e.g. `Windows-MY`, `PKCS12`, `JKS`
- `signing.storepass` / `signing.keypass` — optional (omit for a Windows-MY cert)
- `signing.tsa` — optional RFC-3161 timestamp URL (recommended for releases)
- `signing.jdkHome` — optional; a JDK 8 with `jarsigner` if `JAVA_HOME` is a JRE

Verify a signature with `keytool -printcert -jarfile <jar>` (the Niagara JRE has `keytool`) or `jarsigner -verify <jar>` (needs a JDK). Official release signing with the Sectigo cert runs on the self-hosted runner (`release-build.yml`), not from a committed profile.

## Module-part layout

The module is split into four parts by Niagara **runtime profile**, each its own Gradle subproject with a `<name>.gradle` build file:

| Part | Profile | Contents | Notable deps |
|------|---------|----------|--------------|
| `axCommunity-rt` | `rt` | All ~104 component classes (the bulk of the module) | baja, control-rt, gx-rt, bql-rt, kitControl-rt, alarm-rt, driver-rt, file-rt, converters-rt |
| `axCommunity-wb` | `wb` | Workbench views, widgets, Px graphics; depends on `-rt` | bajaui-wb, workbench-wb, **kitPx-wb** |
| `axCommunity-ux` | `ux` | bajaux — currently empty, kept as a placeholder for future JS/HBS content | baja |
| `axCommunity-doc` | `doc` | Packages the HTML doc tree | baja |

Install **all four** parts together on a station/supervisor. Build settings (`niagara_home` resolution, plugin management, project discovery) live in [`N4/settings.gradle`](N4/settings.gradle); the root [`N4/build.gradle`](N4/build.gradle) applies the toolchain (`gradle/niagara.gradle`), signing (`gradle/signing.gradle`), and publishing (`gradle/publish.gradle`) to each part; vendor, module version, and the minimum-Niagara `niagaraDepVersion` live in [`N4/vendor.gradle`](N4/vendor.gradle).

## Component conventions

Components are classic **Baja `BComponent`/`BPointExtension` subclasses using the manual slot pattern** — *not* the `@NiagaraType` annotation processor (the processor plugin is enabled but unused). When reading or writing a component, follow the existing idiom:

- Slots declared via `newProperty(...)` / `newAction(...)` / `newTopic(...)` with hand-written getter/setter pairs.
- Type registration footer: `public Type getType() { return TYPE; }` and `public static final Type TYPE = Sys.loadType(BFoo.class);`.
- **Every new component type must be registered by hand in [`N4/axCommunity-rt/module-include.xml`](N4/axCommunity-rt/module-include.xml)** with a `<type name="Foo" class="org.axcommunity.niagara.<pkg>.BFoo" />` entry — there is no annotation-driven registration, so a component absent from this file will not load.
- Source packages under `org.axcommunity.niagara.*` group by function: `extensions`, `logic`, `math`, `conversion`, `string`, `time`, `bql`, `hvac`, `weather`, `system`, `web`, `batch`.
- Persisted-state slots that change fast (e.g. filter history) should be `Flags.TRANSIENT` to avoid `config.bog` writes and flash wear on embedded controllers — see [`BFilterExt`](N4/axCommunity-rt/src/org/axcommunity/niagara/extensions/BFilterExt.java) for the rationale.

Resources (icons, bog fragments, relNotes, Px graphics) are pulled into the jar via an explicit `jar { from("src") { include ... } }` block in each part's build file — a new resource directory won't be packaged unless its `include` pattern is added there.

Record component changes in [`N4/axCommunity-rt/src/relNotes/RelNotes.txt`](N4/axCommunity-rt/src/relNotes/RelNotes.txt).

## Known runtime limitations (22.3.0)

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

- **Versioning is conventional-commit driven.** Squash-merge uses the PR title as the commit subject, so the PR title's type (`feat`→minor, `fix`→patch, `feat!`/`BREAKING CHANGE`→major) is what release-please reads. The single source of truth for the version is the `version = '...' // x-release-please-version` line in [`N4/vendor.gradle`](N4/vendor.gradle); release-please rewrites that literal. Don't bump it by hand. The manifest is [`.release-please-manifest.json`](.release-please-manifest.json); config is [`release-please-config.json`](release-please-config.json) (its `extra-files` points at `vendor.gradle`). Release tags are `vX.Y.Z`.
- **Module jars stay unversioned** (`axCommunity-rt.jar`, not `-22.3.0.jar`) — Niagara installs them that way, and ci.yml/release-build.yml/`build-and-restart.ps1` assume it. `project.version` **is** set (from `vendor.gradle`) so it can drive the `module.xml` vendorVersion, but `gradle/niagara.gradle` overrides `jar.archiveName = "${project.name}.jar"` to strip the version from the file name. The Maven publication carries the version on its own coordinates (`org.axcommunity:axCommunity-<part>:<version>`).
- **Signing in CI:** signing uses `jarsigner`, not a Tridium plugin profile, so **nothing signing-related is committed**. `release-build.yml` writes a `gradle.properties.local` with `signing.keystore=NONE` + `signing.storetype=Windows-MY` + the cert `signing.alias`, and runs `jarsigner` against the runner's Windows cert store. It then verifies every jar's signer SHA-256 against `cert/trusted-signer-fingerprints.txt` before shipping; rotating the cert means adding the new fingerprint there. The runner needs a full JDK 8 (with `jarsigner`) — set the `NIAGARA_JDK_HOME` repo var if `JAVA_HOME` is a Niagara JRE.
- **Required checks:** branch protection should require **CI Gate** + **PR Gate** (both report on every PR). `ci.yml` has no `pull_request` paths filter, on purpose — a required check that never reports would stall auto-merge forever.

One-time maintainer setup (repo settings, not code): enable **Allow auto-merge**; set branch protection requiring CI Gate + PR Gate; enable the `niagara-build` self-hosted runner for releases (+ `NIAGARA_BUILD_ENABLED=true` for the CI build gate); optionally set repo secret `SAFENET_PIN` if the signing key becomes token-backed. The self-hosted runner can be run via the tray app in [`tools/github-runner-tray/`](tools/github-runner-tray/).
