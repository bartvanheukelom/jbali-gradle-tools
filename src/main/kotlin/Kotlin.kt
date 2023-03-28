package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion



/**
 * The version of the Kotlin plugin, compiler and stdlib used for this project.
 *
 * NOT the version of Kotlin used by Gradle and the build scripts.
 */
val Project.kotlinVersionString get() =
    (gradle as ExtensionAware).extensions.extraProperties.opt("pluginVersions.org.jetbrains.kotlin") as? String
        ?: getKotlinPluginVersion()

/**
 * The parsed version of the Kotlin plugin, compiler and stdlib used for this project.
 * Any suffix, like for EAP versions, is removed. Use [kotlinVersionString] for the full, raw version string.
 *
 * NOT the version of Kotlin used by Gradle and the build scripts.
 */
val Project.kotlinVersion: KotlinVersion? get() =
    (this as ExtensionAware).extensions.extraProperties.properties.getOrPut("kotlinVersion") {

        kotlinVersionString // 1.4.0[-eap-123]
            .substringBeforeLast('-') // 1.4.0
            .split('.') // ["1", "4", "0"]
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



// old code snippets that may be useful in the future:

//        check(getKotlinPluginVersion() == "$kotlinVersionString") {
//            "kotlinPluginVersion ${getKotlinPluginVersion()} != kotlinVersionString $kotlinVersionString"
//        }
//        check(kotlinVersionString.startsWith(declaredKotlinVersion.toString()))
//        declaredKotlinVersion

//val Project.kotlinEAP get() =
//        (gradle as ExtensionAware).extensions.extraProperties["kotlinEAP"] as Boolean

//val Project.declaredKotlinVersion: KotlinVersion? get() =
//        (gradle as ExtensionAware).extensions.extraProperties["kotlinVersion"] as KotlinVersion?

// copied from kotlin-gradle-plugin, modified
//fun Project.getKotlinPluginVersion(): String =
//        plugins.asSequence().mapNotNull { (it as? KotlinBasePluginWrapper)?.kotlinPluginVersion }.firstOrNull()
//            ?: throw IllegalStateException("Couldn't detect kotlinPluginVersion from $this's plugins $plugins")
