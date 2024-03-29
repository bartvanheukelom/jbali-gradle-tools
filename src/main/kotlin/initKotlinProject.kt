package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType

private object ProjectInited {
    const val PROP = "org.jbali.gradle.projectInited"
}

/**
 * Sets the project [group] and checks its [name] (which can only be set in `settings.gradle*`).
 *
 * If [acceptableKotlinVersions] is given, checks whether the actual Kotlin version is one of them.
 *
 * @throws IllegalStateException if any of these conditions fail.
 */
fun Project.initKotlinProject(
        group: String,
        name: String = this.name,
        acceptableKotlinVersions: Set<KotlinVersion>? = null,
        acceptableKotlinVersionStrings: Set<String>? = null
) {

    try {
        
        this.group = group
        check(this.name == name) {
            "Name of $this should be '$name'. The project name is taken from the directory name, but can be overriden in `settings.gradle.*`."
        }

        // get kotlinVersion to check consistency between declared and plugin version
        kotlinVersion

        val akvs = mutableSetOf<String>()
        acceptableKotlinVersions?.let {
            akvs += it.map { it.toString() }
        }
        acceptableKotlinVersionStrings?.let {
            akvs += it
        }

        if (akvs.isNotEmpty()) {
            val fp = "$group.$name.forceKotlinVersion"
            val force = projectOrBuildProp(fp).isTrueProp
            if (kotlinVersionString !in akvs) {
                val err = "kotlinVersionString $kotlinVersionString is not in acceptableKotlinVersions $akvs"
                if (force) {
                    logger.warn("$err, but forceKotlinVersion is in effect")
                } else {
                    error("$err.\nAdd it, or set project/build property ${fp}=true.")
                }
            } else {
                if (force) {
                    logger.warn("forceKotlinVersion has been applied but is redundant, since kotlinVersionString $kotlinVersionString is in acceptableKotlinVersions $akvs")
                }
            }
        }

        tasks.withType<Jar>().configureEach {
            archiveBaseName.set("$group.$name")
        }

        extensions.extraProperties.set(ProjectInited.PROP, ProjectInited)

        if (isRoot) {

            // consolidate build dirs
            subprojects {
                val p: Project = this
//                println("buildDir = $rootBuildDir / $p.path")
                p.buildDir = rootProject.buildDir / ("_" + p.path.replace(":", "/"))
            }

        }

    } catch (e: Throwable) {
        throw RuntimeException("Error in initKotlinProject $group:$name : $e", e)
    }

}

@Deprecated(message = "renamed", replaceWith = ReplaceWith("initKotlinProject(group, name, acceptableKotlinVersions)"))
fun Project.initProject(
        group: String,
        name: String,
        acceptableKotlinVersions: Set<KotlinVersion>? = null,
        acceptableKotlinVersionStrings: Set<String>? = null
) = initKotlinProject(
        group = group,
        name = name,
        acceptableKotlinVersions = acceptableKotlinVersions,
        acceptableKotlinVersionStrings = acceptableKotlinVersionStrings
)

fun Project.checkInited() {
    try {
        val ip = extensions.extraProperties.get(ProjectInited.PROP)
        check(ip == ProjectInited) {
            "Unexpected value: $ip"
        }
    } catch (e: Throwable) {
        throw IllegalStateException("$this.checkInited() failed: $e", e)
    }
}
