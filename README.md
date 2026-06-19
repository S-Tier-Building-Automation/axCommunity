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

## CI (self-hosted runner)

GitHub Actions can compile and sign `N4/` on a Windows PC with Niagara installed.
See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

For day-to-day use without installing the runner as a Windows **service**, use the
tray app in [`tools/github-runner-tray/`](tools/github-runner-tray/) — it starts
`run.cmd` in your user session and shows listening/job status in the notification area.

## Status

Active maintenance under S-Tier Building Automation targets **Niagara 4.15.3** —
building and signing the `N4/` module parts for current Niagara 4.x releases.
