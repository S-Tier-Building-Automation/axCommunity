/*
 * axCommunity — Niagara 4 root build.
 *
 * Modernized from the original niagara-plugins:4.1.5 build to the Tridium
 * gradle plugins shipped with Niagara 4.15.x (7.6.x).
 */

import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

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

// Single source of truth for the module version. release-please rewrites the
// version literal on the marked line below when a Release PR merges
// (see release-please-config.json); it also feeds the Maven publication below.
val moduleVersion = "22.3.0" // x-release-please-version

vendor {
  // "vendor" attribute shown in Niagara when viewing a module or dist.
  defaultVendor("Community")

  // "vendorVersion" attribute on all module parts. 22.2.x is the S-Tier N4
  // maintenance line (Niagara 4.15.3); upstream was 22.02.01.01.
  defaultModuleVersion(moduleVersion)
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
  // NB: do NOT set `version` here. The Tridium jar task would then suffix the
  // archive (axCommunity-rt-<ver>.jar), but Niagara installs module jars
  // unversioned (axCommunity-rt.jar) — that name is assumed by ci.yml,
  // release-build.yml, and scripts/build-and-restart.ps1. The Maven publication
  // below carries the version on its own coordinates.
  repositories {
    mavenCentral()
  }

  // Publish each signed module part to this repo's GitHub Packages Maven registry
  // (in addition to attaching jars to the GitHub Release). Configured only for
  // the module-part projects, and only adds publishing tasks — it does not change
  // local `assemble`. The release-build CI job runs `gradlew publish` with the
  // Actions GITHUB_TOKEN; locally these env vars are unset and publish is a no-op
  // you simply don't invoke.
  plugins.withId("com.tridium.niagara-module") {
    apply(plugin = "maven-publish")
    extensions.configure<PublishingExtension> {
      publications {
        register<MavenPublication>("gpr") {
          groupId = "org.axcommunity"
          artifactId = project.name
          version = moduleVersion
          artifact(tasks.named("jar"))
        }
      }
      repositories {
        maven {
          name = "GitHubPackages"
          url = uri("https://maven.pkg.github.com/S-Tier-Building-Automation/axCommunity")
          credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
          }
        }
      }
    }
  }
}
