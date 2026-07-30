# License Notes — axCommunity

Status: **decision record** (adopted 2026-07-30). The short version: the
project stays on **GPLv2**, and this document says what that means in practice
for the Niagara community.

## Why GPLv2

The license is **inherited**, not chosen by the current maintainers. The
upstream SourceForge project (*Niagara AX Community Modules*, 2008–2022)
declared GPLv2, and this fork continues under it per the license's terms
(`LICENSE` is the verbatim GPLv2 text — "version 2" only, no "or later").

Relicensing to something more permissive (Apache-2.0/MIT) was considered and
is **not currently feasible**: the code carries contributions from 12+ named
individuals and companies across 2008–2022 with no CLA on file. Relicensing
would require affirmative consent from essentially all of them. If a
coordinated relicensing effort ever happens, it will be tracked publicly.

## What GPLv2 means for you

**If you install the signed jars on stations** (the normal integrator use):
using the unmodified module is just *use* — nothing is required of you.
Installing GPLv2 software on a customer site does not obligate you to open
anything.

**If you modify the module and give the modified jars to anyone** (including
a customer): GPLv2 requires you to make the corresponding source available
to them under GPLv2. The easiest compliance path is to contribute your fix
back upstream here — then you're distributing unmodified releases again.

**If you only modify it privately** (never distributed): no obligations.

Note the linking direction: axCommunity links *against* Tridium's proprietary
Niagara modules, not the other way around — this is the normal posture for
GPL Niagara modules and how such libraries have operated for years. This
document is practical guidance, not legal advice.

## Per-file marking

Every Java source file carries `SPDX-License-Identifier: GPL-2.0-only` so the
posture is unambiguous file-by-file. Original-author attribution lives in
`@author` Javadoc tags (preserved from the SourceForge era) and in
`RelNotes.txt`; do not remove either.

## Copyright

Jar metadata reads `Copyright © 2008-2026 axCommunity Project contributors`
(the first upstream commit was 2008; copyright remains with the individual
contributors — the phrasing asserts no transfer that never happened).

## Contributing

By submitting a contribution (PR, patch, snippet posted to an issue), you agree
it is distributed under GPLv2 — see [CONTRIBUTING.md](CONTRIBUTING.md).
