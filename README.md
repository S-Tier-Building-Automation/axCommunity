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
license declared by the upstream SourceForge project. See [`LICENSE`](LICENSE).

## Layout

| Path | Description |
|------|-------------|
| `N4/` | Active Niagara **4** build — Gradle multi-module (`axCommunity-rt`, `-wb`, `-ux`, `-doc`) with `gradlew` and Kotlin DSL build scripts. |

Pre-N4 Niagara AX sources from the import live under [`archive/AX/`](archive/AX/) for history only; they are obsolete and not maintained.

## Status

Active maintenance under S-Tier Building Automation targets **Niagara 4.15.3**.
Current module version: **22.2.2** (vendor `Community`).

## Build (local)

Requires a licensed Niagara **4.15.x** install (tested on 4.15.3.28).

```powershell
cd N4
copy gradle.properties.example gradle.properties.local
# Edit gradle.properties.local — set niagara_home to your install path
.\gradlew.bat clean assemble
```

Signed JARs are written under `N4/axCommunity-*/build/libs/`. With no SafeNet
profile configured, signing uses the dev cert **Niagara4Modules** (must be trusted
in your Niagara user home).

Install all four parts on a station or supervisor: `axCommunity-rt`, `-wb`, `-ux`, `-doc`.

## Known limitations (22.2.2)

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
for the full component changelog. The **22.2.2** entry documents this N4 4.15.3
maintenance release.
