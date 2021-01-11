package org.jbali.gradle

import org.gradle.api.plugins.PluginAware

@Suppress("EnumEntryName")
enum class KotlinCompilerPlugin {

    /**
     * [https://kotlinlang.org/docs/all-open-plugin.html]
     */
    allopen,

    /**
     * [https://kotlinlang.org/docs/no-arg-plugin.html#jpa-support]
     */
    jpa,

    /**
     * [https://kotlinlang.org/docs/all-open-plugin.html#spring-support]
     */
    spring,

    serialization,

    /**
     * [https://kotlinlang.org/docs/no-arg-plugin.html]
     */
    noarg;

    // TODO https://kotlinlang.org/docs/sam-with-receiver-plugin.html
    // TODO https://kotlinlang.org/docs/kapt.html#using-in-gradle

    val gradlePluginId = "org.jetbrains.kotlin.plugin.$name"

    fun applyTo(project: PluginAware) {
        project.pluginManager.apply(gradlePluginId)
    }
}
