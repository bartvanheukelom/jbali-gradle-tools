package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper

// copied from kotlin-gradle-plugin, modified
fun Project.getKotlinPluginVersion(): String =
        plugins.asSequence().mapNotNull { (it as? KotlinBasePluginWrapper)?.kotlinPluginVersion }.firstOrNull()
            ?: throw IllegalStateException("Couldn't detect kotlinPluginVersion from $this's plugins $plugins")

val Project.kotlinVersionString get() =
    (gradle as ExtensionAware).extensions.extraProperties.opt("pluginVersions.org.jetbrains.kotlin") as? String
        ?: getKotlinPluginVersion()

//val Project.kotlinEAP get() =
//        (gradle as ExtensionAware).extensions.extraProperties["kotlinEAP"] as Boolean

//val Project.declaredKotlinVersion: KotlinVersion? get() =
//        (gradle as ExtensionAware).extensions.extraProperties["kotlinVersion"] as KotlinVersion?

val Project.kotlinVersion: KotlinVersion? get() =
    (this as ExtensionAware).extensions.extraProperties.properties.getOrPut("kotlinVersion") {
//        check(getKotlinPluginVersion() == "$kotlinVersionString") {
//            "kotlinPluginVersion ${getKotlinPluginVersion()} != kotlinVersionString $kotlinVersionString"
//        }
//        check(kotlinVersionString.startsWith(declaredKotlinVersion.toString()))
//        declaredKotlinVersion
        
        kotlinVersionString
            .substringBeforeLast('-')
            .split('.')
            .let { KotlinVersion(
                it[0].toInt(),
                it[1].toInt(),
                it[2].toInt(),
            ) }
            .also {
                logger.info("kotlinVersionString = '$kotlinVersionString', kotlinVersion = $it")
            }
        
    } as KotlinVersion?

// ripped from org.gradle.kotlin.dsl (RepositoryHandlerExtensions.kt)
private fun RepositoryHandler.maven(url: Any) =
        maven {
            setUrl(url)
        }

fun Project.kotlinRepositories() {
    with (repositories) {
        mavenCentral()
    }
}
