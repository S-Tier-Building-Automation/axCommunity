# axCommunity — Niagara Community Modules

A library of modules for the Tridium **Niagara 4** framework. This repository is a
maintained fork of the **Niagara AX Community Modules** project, imported from
SourceForge with its full commit history preserved.

> Niagara 4 programmers are encouraged to use and expand the library.

## Provenance

- **Upstream:** [niagaraaxcommun on SourceForge](https://sourceforge.net/projects/niagaraaxcommun/)
  (SVN: `https://svn.code.sf.net/p/niagaraaxcommun/code/`)
- **Imported at:** SVN revision **r250** (last upstream change 2022-02-16)
- **History:** All 250 upstream revisions (2008–2022) were replayed into Git,
  preserving the original author, date, and commit message for each revision.
  Each imported commit records its source revision in a trailing
  `svn-revision: <n>` line.

## License

Distributed under the **GNU General Public License, version 2 (GPLv2)** — the
license declared by the upstream SourceForge project. See [`LICENSE`](LICENSE)
and [`LICENSE-NOTES.md`](LICENSE-NOTES.md) (what GPLv2 means for integrators,
and why the project stays on it).

## Docs

- [`USER-GUIDE.md`](USER-GUIDE.md) — install + palette/component catalog for station programmers
- [`SECURITY.md`](SECURITY.md) — vulnerability reporting & release verification
- [`CONTRIBUTING.md`](CONTRIBUTING.md) — dev setup, PR conventions, component idiom
- [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md)

## Layout

| Path | Description |
|------|-------------|
| `N4/` | Active Niagara **4** build — Gradle multi-module (`axCommunity-rt`, `-wb`, `-ux`, `-doc`) with `gradlew` and Groovy build scripts (Gradle 4.10.3 toolchain). |

Pre-N4 Niagara AX sources from the import live under [`archive/AX/`](archive/AX/) for history only; they are obsolete and not maintained.

## Status

Active maintenance under S-Tier Building Automation supports **Niagara 4.10 and
newer** from a **single set of jars**. Current module version: **22.3.0** (vendor
`Community`).

Compatibility is achieved by compiling against the **oldest** supported Niagara
(4.10.11.12): each `module.xml` stamps its Tridium dependencies as `>= 4.10`, so
the same jars load on 4.10 through 4.15+.

## Build (local)

Requires a licensed Niagara **4.10.x** install (compile against the oldest
version you intend to support; tested on 4.10.11.12) and a full **JDK 8** —
signing uses `jarsigner`, which a Niagara JRE does not bundle.

```powershell
cd N4
copy gradle.properties.example gradle.properties.local
# Edit gradle.properties.local — set niagara_home to your 4.10.x install path
.\gradlew.bat clean --console=plain
.\gradlew.bat assemble --console=plain
```

> Run `clean` and `assemble` as separate invocations — the Gradle 4.10.x
> toolchain can otherwise execute `clean` out of order and wipe the fresh jars.

Signed JARs are written under `N4/axCommunity-*/build/libs/`. With no `signing.*`
configured, the build signs with an auto-generated self-signed dev cert (alias
**axCommunityDev**); trust it once in the Workbench **User Trust Store** so
`-wb` types appear. Official release signing is configured on the self-hosted
runner (see below).

Install all four parts on a station or supervisor: `axCommunity-rt`, `-wb`, `-ux`, `-doc`.

## Troubleshooting (dev machine)

- **Workbench won't launch / platform "Connection refused" on a machine with two Niagara versions.** A global `NIAGARA_HOME`/`NIAGARA_USER_HOME` pinned to one version breaks the other (Workbench dies with an `AccessControlException` on `niagara.user.home`; the `Niagara` service's `niagarad` won't start). Don't set those env vars globally — the launchers self-locate. See the **Multi-version machine & platform troubleshooting** section of [`CLAUDE.md`](CLAUDE.md) for the exact symptoms and the per-service fix.
- **`-wb` types don't appear after install.** The dev-cert (`axCommunityDev`) must be trusted: export it as PEM (`keytool -exportcert -rfc -alias axCommunityDev -file axCommunityDev.pem -keystore "%USERPROFILE%\.gradle\axCommunity\dev-signing.jks" -storepass changeit`) and import it into the Workbench **User Trust Store**, then restart Workbench. If it still fails, confirm the *deployed* jar is signed (`jarsigner -verify <niagara_home>\modules\axCommunity-rt.jar`) — a stale unsigned copy in `modules/` is the usual culprit.

## Known limitations (22.3.0)

- **FireFoxxWeather** — palette entry is fixed, but the component still calls the
  defunct Yahoo Weather RSS API. It will not return live weather until replaced
  with a new data source.
- **PxGraphics palette folder** — HVAC graphics reference the external
  **johnGraphics** module (`module://johnGraphics/...`) and embed **kitPx**
  widget types (`kitPx:BoundLabel`, etc.). Install **johnGraphics** and ensure
  **kitPx-wb** / **kitPx-ux** are on the station or supervisor or those palette
  items will fail to resolve.

## CI (self-hosted runner)

GitHub Actions can compile and sign `N4/` on a Windows PC with Niagara installed.
See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

For day-to-day use without installing the runner as a Windows **service**, use the
tray app in [`tools/github-runner-tray/`](tools/github-runner-tray/) — it starts
`run.cmd` in your user session and shows listening/job status in the notification area.

## PR & release automation

The full PR→release lifecycle runs on GitHub Actions:

- **PRs** are gated by a conventional-commit title check (**PR Gate**) and the
  build (**CI Gate**); outdated review threads auto-resolve, and PRs labelled
  `automerge` (plus Dependabot patch/minor bumps) merge themselves once green.
- **Releases** are cut by [release-please](https://github.com/googleapis/release-please):
  merge PRs with conventional titles (`feat:`, `fix:`, `feat!:`), and it maintains
  a Release PR that bumps the version and `CHANGELOG.md`. Merging that PR tags a
  `vX.Y.Z` release; a self-hosted runner then builds, officially signs, verifies,
  and attaches the four module jars and publishes them to GitHub Packages.

The signed jars on each [GitHub Release](https://github.com/S-Tier-Building-Automation/axCommunity/releases)
are verified against the trusted signer fingerprint in
[`cert/trusted-signer-fingerprints.txt`](cert/trusted-signer-fingerprints.txt) before upload.
See the **CI/CD & release automation** section of [`CLAUDE.md`](CLAUDE.md) for the
full workflow map and one-time setup steps.

## Release history

See [`N4/axCommunity-rt/src/relNotes/RelNotes.txt`](N4/axCommunity-rt/src/relNotes/RelNotes.txt)
for the full component changelog. The **22.3.0** entry documents the move to a
single Niagara 4.10+ compatible artifact.
