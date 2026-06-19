/*
 * axCommunity-ux — bajaux module part.
 *
 * Currently has no JavaScript/HBS sources in the upstream tree; kept as a
 * declared part so the module manifest layout matches upstream and future
 * bajaux content has a home.
 */

import com.tridium.gradle.plugins.module.util.ModulePart.RuntimeProfile.*

plugins {
  id("com.tridium.niagara-module")
  id("com.tridium.niagara-signing")
  id("com.tridium.convention.niagara-home-repositories")
}

description = "Open source AX Community module"

moduleManifest {
  moduleName.set("axCommunity")
  runtimeProfile.set(ux)
  preferredSymbol.set("axc4")
}

dependencies {
  nre(":nre")
  api(":baja")
}
