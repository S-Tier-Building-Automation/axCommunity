# axCommunity — Niagara AX Community Modules

A library of modules for the Tridium Niagara framework. This repository is a
maintained fork of the **Niagara AX Community Modules** project, imported from
SourceForge with its full commit history preserved.

> AX programmers are encouraged to use and expand the library.

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

| Path  | Description |
|-------|-------------|
| `AX/` | Legacy Niagara **AX** module (Ant `build.xml`, Eclipse `.project`, lexicon, palette, `src/`). |
| `N4/` | Niagara **4** port — a Gradle multi-module build (`axCommunity-rt`, `-wb`, `-ux`, `-doc`) with `gradlew`, `build.gradle`, `vendor.gradle`, and `environment.gradle`. |

## Status

Active maintenance has resumed under S-Tier Building Automation, with the goal
of building and signing the `N4/` modules for current Niagara 4.x releases
(targeting **Niagara 4.15.3**). Build/signing modernization is tracked
separately from this initial import.
