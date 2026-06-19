/*
 * axCommunity-rt — runtime module part.
 */

import com.tridium.gradle.plugins.module.util.ModulePart.RuntimeProfile.*

plugins {
  id("com.tridium.niagara-module")
  id("com.tridium.niagara-signing")
  id("com.tridium.niagara-annotation-processors")
  id("com.tridium.convention.niagara-home-repositories")
}

description = "Open source AX Community module"

moduleManifest {
  moduleName.set("axCommunity")
  runtimeProfile.set(rt)
  preferredSymbol.set("axc4")
}

dependencies {
  // NRE
  nre(":nre")

  // Niagara module dependencies (derived from the rt sources' imports)
  api(":baja")
  api(":control-rt")
  api(":gx-rt")
  api(":bql-rt")
  api(":platform-rt")
  api(":kitControl-rt")
  api(":alarm-rt")
  api(":driver-rt")
}

// Package non-Java resources that live alongside the sources (icons, bog
// fragments, release notes) into the module jar.
sourceSets {
  main {
    resources {
      srcDir("src")
      include("relNotes/**")
      include("org/axcommunity/niagara/graphics/**")
      include("org/axcommunity/niagara/bogs/**")
    }
  }
}
