# Security Policy

## Supported versions

Security fixes are applied to the latest 22.x release line only. The module
supports **Niagara 4.10 and newer** from a single set of jars — always run the
current release from the
[Releases page](https://github.com/S-Tier-Building-Automation/axCommunity/releases).

| Version | Supported |
|---------|-----------|
| 22.3.x  | Yes       |
| < 22.3  | No        |

## Reporting a vulnerability

Please **do not** open a public issue for security reports. Use GitHub's private
vulnerability reporting:

**[Report a vulnerability](https://github.com/S-Tier-Building-Automation/axCommunity/security/advisories/new)**

Include the module version, the Niagara version, whether the issue is reachable
from a wiresheet property/link or only from Workbench, and a reproduction or
affected component. You can expect an acknowledgment within a few days and a
fix-or-mitigation decision before any public disclosure.

## Scope notes for reporters and users

- **The product is signed code.** Official releases are signed with the S-Tier
  Building Automation certificate and verified against
  [`cert/trusted-signer-fingerprints.txt`](cert/trusted-signer-fingerprints.txt)
  before publication. Verify a downloaded jar with
  `keytool -printcert -jarfile axCommunity-rt.jar` and compare the SHA-256
  fingerprint against that file.
- **Station-side components are privileged by nature.** Components in the
  `web`, `weather`, `system`, and `bql` palette folders perform network fetches
  or file writes with the station process's OS rights. Their URL/path
  properties are linkable — anyone with write access to a wiresheet property is
  trusted input, not an attacker. Report issues where the privilege boundary is
  crossed *without* such access (e.g. MITM-influenced responses, unsafe parsing
  of remote content).
- **Module permissions are broad for historical reasons** and are being
  tightened; see `module-permissions.xml` in each module part.

## Build & release security

- CI never runs pull-request code from forks on the self-hosted Niagara build
  runner (the machine that also holds the signing identity).
- GitHub Actions are being pinned to full commit SHAs; dependency and action
  updates arrive via Dependabot.
