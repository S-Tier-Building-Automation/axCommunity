/*
 * axCommunity — Niagara 4 build settings.
 *
 * Modernized from the original 2018-era niagara-plugins:4.1.5 build to the
 * Tridium gradle plugins shipped with Niagara 4.15.x (7.6.x).
 */

import com.tridium.gradle.plugins.settings.MultiProjectExtension
import com.tridium.gradle.plugins.settings.LocalSettingsExtension

pluginManagement {
  // Load local properties if they exist (gitignored)
  val localPropsFile = File(rootDir, "gradle.properties.local")
  val localProps = java.util.Properties()
  if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { localProps.load(it) }
  }

  val niagaraHome: Provider<String> = providers.gradleProperty("niagara_home").orElse(
    localProps.getProperty("niagara_home")?.let { providers.provider { it } }
      ?: providers.systemProperty("niagara_home").orElse(
        providers.environmentVariable("NIAGARA_HOME").orElse(
          providers.environmentVariable("niagara_home")
        )
      )
  )

  val gradlePluginHome: String = providers.gradleProperty("gradlePluginHome").orElse(
    providers.environmentVariable("GRADLE_PLUGIN_HOME").orElse(
      niagaraHome.map { "$it/etc/m2/repository" }
    )
  ).orNull ?: throw InvalidUserDataException(
    "Cannot derive 'gradlePluginHome'. Set 'niagara_home' in ${File(rootDir, "gradle.properties.local")} " +
      "(e.g. niagara_home=C:\\\\TAC\\\\Niagara-4.15.3.28) or define the NIAGARA_HOME environment variable."
  )

  val gradlePluginRepoUrl = "file:///${gradlePluginHome.replace('\\', '/')}"

  // Defaults match the plugins bundled with Niagara 4.15.3. Override via
  // 'niagara_plugin_version' in gradle.properties.local to build against a
  // different Niagara install whose bundled plugins use another version.
  val gradlePluginVersion: String = providers.gradleProperty("niagara_plugin_version").orNull
    ?: localProps.getProperty("niagara_plugin_version")
    ?: "7.6.22"
  val settingsPluginVersion = "7.6.3"

  repositories {
    maven(url = gradlePluginRepoUrl)
    gradlePluginPortal()
  }

  plugins {
    id("com.tridium.settings.multi-project") version (settingsPluginVersion)
    id("com.tridium.settings.local-settings-convention") version (settingsPluginVersion)

    id("com.tridium.niagara") version (gradlePluginVersion)
    id("com.tridium.vendor") version (gradlePluginVersion)
    id("com.tridium.niagara-module") version (gradlePluginVersion)
    id("com.tridium.niagara-signing") version (gradlePluginVersion)
    id("com.tridium.bajadoc") version (gradlePluginVersion)
    id("com.tridium.niagara-jacoco") version (gradlePluginVersion)
    id("com.tridium.niagara-annotation-processors") version (gradlePluginVersion)

    id("com.tridium.convention.niagara-home-repositories") version (gradlePluginVersion)
  }
}

plugins {
  id("com.tridium.settings.multi-project")
  id("com.tridium.settings.local-settings-convention")
}

configure<LocalSettingsExtension> {
  loadLocalSettings()
}

configure<MultiProjectExtension> {
  // Discover the axCommunity-rt/-wb/-ux/-doc module-part projects, each of which
  // lives in a subdirectory with a build file named "<projectName>.gradle.kts".
  // Bare findProjects() walks subdirectories only (it does not register the root).
  findProjects()
}

rootProject.name = "axCommunity"
