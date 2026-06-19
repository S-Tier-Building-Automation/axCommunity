/*
 * axCommunity-doc — documentation module part (packages the HTML docs).
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
  runtimeProfile.set(doc)
  preferredSymbol.set("axc4")
}

dependencies {
  nre(":nre")
  api(":baja")
}

// Package the documentation tree into the module jar.
sourceSets {
  main {
    resources {
      srcDir("src")
      include("doc/**")
    }
  }
}
