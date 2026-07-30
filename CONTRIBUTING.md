# Contributing to axCommunity

Thanks for helping keep the Niagara AX Community Modules alive on Niagara 4.
This project is a maintained fork of the original SourceForge library (imported
at SVN r250) — contributions from the original era and new ones alike live here.

## Ground rules

- **License.** The project is GPLv2 (inherited from upstream — see
  [LICENSE-NOTES.md](LICENSE-NOTES.md)). By contributing you agree your
  contribution is distributed under the same license. Keep existing `@author`
  attribution intact and add your own when you author or substantially modify
  a component.
- **Component conventions.** Components use the manual Baja slot pattern
  (`newProperty`/`newAction`/`newTopic` with hand-written getters/setters and a
  `TYPE` footer) — **not** the `@NiagaraType` annotation processor. Every new
  component type must be registered by hand in
  [`N4/axCommunity-rt/module-include.xml`](N4/axCommunity-rt/module-include.xml)
  or it will not load.
- **Every component change needs a [`RelNotes.txt`](N4/axCommunity-rt/src/relNotes/RelNotes.txt)
  entry** (`- ADDED`/`- MODIFIED`/`- FIXED` + your attribution) under the
  `Unreleased` heading, and class-level Javadoc (this becomes the F1 bajadoc in
  Workbench).
- **Fast-changing persisted state must be `Flags.TRANSIENT`** (see
  `extensions/BFilterExt.java`) to avoid `config.bog` churn and flash wear on
  embedded controllers.
- **Background work goes through `org.axcommunity.niagara.util.AxcExecutor`** —
  never `new Thread()`, never blocking I/O on the station clock or engine
  thread. Components that fetch URLs or write files must treat their
  URL/path properties as operator input (timeouts, size caps, traversal
  rejection).

## Development setup

You need a **licensed Niagara 4.10.x install** (the build compiles against the
oldest supported version so one set of jars runs on 4.10 through 4.15+) and a
full **JDK 8** (a Niagara JRE has no `jarsigner`). Tridium modules are not
redistributable, so this requirement can't be avoided — it's the same for
every Niagara module project.

```powershell
cd N4
copy gradle.properties.example gradle.properties.local
# edit gradle.properties.local: set niagara_home to your 4.10.x install
.\gradlew.bat clean --console=plain      # separate invocation (toolchain quirk)
.\gradlew.bat assemble --console=plain   # compile + dev-sign all four parts
```

Signed jars land in `N4/axCommunity-*/build/libs/`, signed with an
auto-generated self-signed dev cert (`axCommunityDev`). Trust it once in the
Workbench **User Trust Store** or `-wb` types won't appear — see the README
troubleshooting section. `scripts/build-and-restart.ps1` runs the full
build → deploy → restart → Workbench loop.

**There is no automated test suite.** Verify changes by deploying to a station
and exercising the component on a wire sheet. Say in your PR which Niagara
version(s) you tested on.

## Pull requests

- **The PR title must be a conventional commit** — it's linted by the PR Gate
  and becomes the squash-merge subject, which release-please reads to version
  the next release:
  - `feat:` new component or capability (minor bump)
  - `fix:` bug fix (patch bump)
  - `feat!:` / `BREAKING CHANGE:` (major bump)
  - `docs:`, `ci:`, `build:`, `chore:`, `refactor:` (no version bump)
- **Checks:** the CI Gate runs a hosted structural smoke-check; a full
  compile+sign runs on the maintainer's self-hosted Niagara runner. For
  security, pull requests from forks **do not** run on that runner — a
  maintainer will pull your branch into a same-repo PR after review if a full
  build is needed. Don't be alarmed if the Niagara build shows skipped on your
  fork PR.
- Small, single-purpose PRs merge fastest. The `automerge` label is available
  for trivial changes once the gates are green.
- New palette entries: keep folders alphabetical and reuse the existing
  graphics rather than adding new icon files where possible.

## Reporting issues

- **Bugs:** use the *Bug report* issue template — include the component name,
  module version, Niagara version, and whether the problem is on a station or
  in Workbench.
- **Security:** never open a public issue — see [SECURITY.md](SECURITY.md).
- **Ideas:** the *Feature request* template; check
  [RelNotes.txt](N4/axCommunity-rt/src/relNotes/RelNotes.txt) and the README
  known-limitations section first (some gaps are already documented).
