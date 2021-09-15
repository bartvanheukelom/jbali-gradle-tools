package org.jbali.gradle.settings

import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.plugin.management.PluginManagementSpec

interface ManagePluginsContext : PluginManagementSpec {
    val properties: ExtraPropertiesExtension
}

fun Settings.managePlugins(config: ManagePluginsContext.() -> Unit) =
    object : ManagePluginsContext, PluginManagementSpec by pluginManagement {
        override val properties get() = this@managePlugins.properties
    }.config()

fun ManagePluginsContext.recommendedRepositories() {
    with(repositories) {
        mavenCentral()
        gradlePluginPortal()
    }
}

fun ManagePluginsContext.versionsFromProperties() {
    with (resolutionStrategy) {
        eachPlugin { p ->
            p.useVersion(pluginVersionFromProps(p.requested.id.id) ?: p.requested.version)
        }
    }
}

private fun ManagePluginsContext.pluginVersionFromProps(path: String): String? =
    properties.opt("pluginVersions.$path")?.toString()
        ?: path.takeIf { it != "" }
            ?.let { pluginVersionFromProps(path.substringBeforeLast('.', "")) }
