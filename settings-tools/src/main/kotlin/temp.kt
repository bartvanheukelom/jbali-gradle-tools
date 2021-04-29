package org.jbali.gradle.settings

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware

private fun RepositoryHandler.maven(url: Any) =
    maven { it.setUrl(url) }

private fun Settings.testPlugins() {

    pluginManagement.apply {


        val kotlinVersion = KotlinVersion(1, 5, 0)
        val kotlinEAPSuffix = null

        plugins.apply {
            id("org.jetbrains.dokka").version("1.4.32")
        }

        // ============ shared TODO find way to share this =========== //

        val kotlinVersionString = "$kotlinVersion${kotlinEAPSuffix ?: ""}"
        @Suppress("SENSELESS_COMPARISON")
        val kotlinEAP = kotlinEAPSuffix != null

        (gradle as ExtensionAware).extensions.extraProperties.let { e ->

            // TODO make wrapper class with suffix included
            // this one preserves it as KotlinVersion
            check(!e.has("kotlinVersion"))
            e["kotlinVersion"] = kotlinVersion
            // but this one includes the suffix
            check(!e.has("kotlinVersionString"))
            e["kotlinVersionString"] = kotlinVersionString

            check(!e.has("kotlinEAP"))
            e["kotlinEAP"] = kotlinEAP

        }

        repositories.apply {
            mavenCentral()
            gradlePluginPortal()

            if (kotlinEAP) {
                maven("https://dl.bintray.com/kotlin/kotlin-eap")
                maven("https://kotlin.bintray.com/kotlinx")
            }
        }

        plugins.apply {
            listOf(
                "org.jetbrains.kotlin.jvm",
                "org.jetbrains.kotlin.multiplatform",
                "org.jetbrains.kotlin.plugin.serialization"
            ).forEach {
                id(it).version(kotlinVersionString)
            }
        }

    }

}
