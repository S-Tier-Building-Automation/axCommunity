/*
 * axCommunity-wb — workbench module part.
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
  runtimeProfile.set(wb)
  preferredSymbol.set("axc4")
}

dependencies {
  nre(":nre")

  api(":baja")
  api(":control-rt")
  api(":gx-rt")
  api(":converters-rt")
  api(":bajaui-wb")
  api(":workbench-wb")
  api(":kitPx-wb")
  api(project(":axCommunity-rt"))
}

// Package widget/view image resources into the module jar.
sourceSets {
  main {
    resources {
      srcDir("src")
      include("org/axcommunity/niagara/graphics/**")
    }
  }
}
