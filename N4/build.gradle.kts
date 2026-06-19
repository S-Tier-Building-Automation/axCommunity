/*
 * axCommunity — Niagara 4 root build.
 *
 * Modernized from the original niagara-plugins:4.1.5 build to the Tridium
 * gradle plugins shipped with Niagara 4.15.x (7.6.x).
 */

// Load local properties if they exist (gitignored)
val localPropsFile = file("gradle.properties.local")
if (localPropsFile.exists()) {
  val localProps = java.util.Properties()
  localPropsFile.inputStream().use { localProps.load(it) }
  localProps.forEach { key, value ->
    extra.set(key.toString(), value)
  }
}

plugins {
  // Base Niagara plugin
  id("com.tridium.niagara")

  // The vendor plugin provides the vendor {} extension to set the default group
  // for Maven publishing, the default vendor attribute, and the default module
  // and dist version.
  id("com.tridium.vendor")

  // The signing plugin configures signing of all modules and dists.
  id("com.tridium.niagara-signing")

  // Configures !bin/ext and !modules as flat-file Maven repositories so modules
  // can compile against the installed Niagara.
  id("com.tridium.convention.niagara-home-repositories")
}

vendor {
  // "vendor" attribute shown in Niagara when viewing a module or dist.
  defaultVendor("Community")

  // "vendorVersion" attribute on all module parts. Tracks the upstream
  // SourceForge release lineage (last upstream was 22.02.01.01).
  defaultModuleVersion("22.2.1")
}

signingServices {
  signingProfileFactory {
    // Allow the Niagara default profile (~/.tridium/security/niagara.signing.xml)
    // when no explicit signing.profile is set in gradle.properties.local. This
    // lets developers build without a SafeNet token using the auto-generated dev
    // cert ("Niagara4Modules").
    allowDefaultProfile.set(project.findProperty("signing.profile") == null)
  }
}

niagaraSigning {
  val signingProfile = project.findProperty("signing.profile")?.toString()
  val defaultAlias = if (signingProfile != null) "axCommunity" else "Niagara4Modules"
  val signingAlias = project.findProperty("signing.alias")?.toString() ?: defaultAlias

  aliases.set(listOf(signingAlias))
  if (signingProfile != null) {
    signingProfileFile.set(file(signingProfile))
  }
}

subprojects {
  repositories {
    mavenCentral()
  }
}
