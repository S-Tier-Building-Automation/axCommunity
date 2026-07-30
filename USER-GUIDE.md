# axCommunity User Guide

For station programmers installing and using the axCommunity module library.
Developer/build docs live in the [README](README.md) and
[CONTRIBUTING](CONTRIBUTING.md); the full component change history is in
[RelNotes.txt](N4/axCommunity-rt/src/relNotes/RelNotes.txt).

## What you get

~100 control, logic, math, conversion, string, time, BQL, HVAC, weather,
system, web and batch components for **Niagara 4.10 and newer**, all from one
set of jars (the build compiles against 4.10.x and stamps its dependencies
`>= 4.10`, so the same jars load on 4.10 through 4.15+).

## Install

1. Download the four module jars from the latest
   [GitHub Release](https://github.com/S-Tier-Building-Automation/axCommunity/releases):
   `axCommunity-rt.jar`, `axCommunity-wb.jar`, `axCommunity-ux.jar`,
   `axCommunity-doc.jar`. Install **all four** together.
2. *(Recommended)* Verify the signature before installing:

   ```powershell
   keytool -printcert -jarfile axCommunity-rt.jar
   ```

   The signer SHA-256 fingerprint must match an entry in
   [`cert/trusted-signer-fingerprints.txt`](cert/trusted-signer-fingerprints.txt)
   (the same allowlist the release pipeline enforces before publishing).
3. Copy the jars to `<niagara_home>\modules` (Supervisor) or install via
   **Software Manager** on a JACE, then restart the station/Workbench.
4. If `-wb` types (views/widgets) don't resolve after install, the signing
   certificate may not be trusted yet — import it into the Workbench
   **User Trust Store** (and the station's **System Trust Store** for
   station-side installs) and restart. Official releases are signed by the
   S-Tier Building Automation Sectigo certificate; local dev builds use the
   self-signed `axCommunityDev` cert (see README for that flow).

## Palette tour

The `axCommunity` palette has 14 folders. Component names match their type
names — press **F1** on any palette item for its bajadoc.

| Folder | Contents |
|--------|----------|
| **Batch** | BatchLinkCreator — bulk-create links from a CSV list (Workbench tool; run DryRunOnly first) |
| **BQL** | BqlNumericRecap, BqlTopNAvg (average of the N highest values), TrendAnalyzer, TrendDynamicAnalyzer, HistoryToCSV |
| **Conversion** | 23 converters: CSV/string/enum/numeric/time conversions, AbsTimeToDateParts, DatePartsToAbsTime, EpochToAbsTime, TimeSplitter, ChangeCase, AsciiHexDecConversion, FileToImage, OrdToPxView, BitsToStatusNumeric, and more |
| **Extensions** | FilterExt, FilterLogMeanExt (filter point extensions), Scale (point-extension scaling), UnAckAlarmState |
| **Hvac** | SimpleTstat, TstatA, TstatB (loop-control thermostats) |
| **Logic** | SuperOr (multi-input OR with flexible outputs), BooleanToggle/ToggleLatch/ToggleSwitch, latches (NumericLatchObject/Custom), FireOnChange, BooleanRotateOnExecute, PeakValueAndTstamp, Set*Action family (Boolean/Numeric/Brush/Font), WhoWhen* setpoints, Latest*, OneShot* selects, DynamicLink* (Numeric/Boolean/String) and DynamicLinks |
| **Math** | RoundUp, Modulo, triggered math (TrAdd/TrSubtract/TrMultiply/TrDivide/TrCopy), AvgMinMax (+ _v1), StatusNumericFifo (33-deep shift register with min/max/avg), AbsTimeOffset, LimitOutput, NumericConstantAdjustable, ProductionCounter, ShiftTargets |
| **String** | SuperConcatPlus (the workhorse string builder), SuperStringTest, ConcatLogData, ParentFolderInfo, ReplaceString, ReplaceNullOrBlankString, RetainLastValidString, StringBufferAndSelect |
| **System** | SysInfo (station/platform/network info + restart/reboot actions), StringToFile |
| **Time** | Stopwatch (+ _v2), EventAtTime |
| **Web** | GetHTTP (fetch a URL into a linkable string; set `httpsOnly` on untrusted networks) |
| **Weather** | FireFoxxWeather + support enums — **currently returns no live data** (see below) |
| **Widgets** | RevolvingTextLabel (+2), DropDownList, BoundLabelHighlight (Workbench/kitPx widgets) |
| **PxGraphics** | ~40 pre-built HVAC Px graphics — **requires the external johnGraphics module** (see below) |

## Known limitations

- **FireFoxxWeather** — the palette entry works, but the component still calls
  the defunct Yahoo Weather RSS API and returns no live data. A provider
  replacement is on the roadmap; until then treat it as a placeholder.
- **PxGraphics folder** — the graphics reference `module://johnGraphics/...`
  images and embed `kitPx` widgets. Install **johnGraphics** and ensure
  **kitPx-wb**/**kitPx-ux** are present, or those palette items won't resolve.
- **Widgets are Workbench/kitPx-era** — they render in Workbench and Px pages,
  not in HTML5 web profiles. An HTML5 (bajaux) story is a roadmap item.

## Getting help

- Per-component help: **F1** in Workbench (bajadoc).
- Component change history: [RelNotes.txt](N4/axCommunity-rt/src/relNotes/RelNotes.txt).
- Bugs: open an issue via the *Bug report* template. Security reports:
  [SECURITY.md](SECURITY.md) — never a public issue.
