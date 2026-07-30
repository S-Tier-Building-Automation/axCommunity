## What does this PR do?

<!-- One or two sentences. If it fixes an issue, link it: "Fixes #123". -->

## PR title check

The PR title must be a **conventional commit** — it becomes the squash-merge
subject and drives release-please versioning:

- [ ] Title matches `feat:` / `fix:` / `feat!:` / `docs:` / `ci:` / `build:` / `chore:` / `refactor:` (e.g. `fix: correct FIFO average with negative values`)

## Component checklist

Skip what doesn't apply (docs-only PRs, CI changes, etc.):

- [ ] New component types are registered in `N4/axCommunity-rt/module-include.xml`
- [ ] Class-level Javadoc added/updated (this is the Workbench F1 bajadoc)
- [ ] `RelNotes.txt` entry added under `Unreleased` with attribution
- [ ] Fast-changing persisted outputs are `Flags.TRANSIENT`
- [ ] Background work uses `AxcExecutor` (no `new Thread()`, no blocking I/O on clock/engine threads)
- [ ] New URL/file-path properties are treated as operator input (timeouts, caps, traversal rejection)

## Verification

- [ ] `.\gradlew.bat clean` then `.\gradlew.bat assemble` succeed locally (separate invocations)
- [ ] Deployed and exercised on a station — Niagara version(s) tested: <!-- e.g. 4.10.11.12, 4.15.3 -->
- [ ] I agree my contribution is distributed under the project's GPLv2 license
