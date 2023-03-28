@file:OptIn(ExperimentalStdlibApi::class)

import java.io.File
import java.net.URI
import java.security.MessageDigest
import kotlin.math.min

plugins {
    `kotlin-dsl`
    `maven-publish`
}

val isWrapperBuild = gradle.startParameter.taskRequests.any {
    it.projectPath == null && it.rootDir == null && it.args.any(Regex(":?wrapper")::matches)
}
val recommendedGradleVersion = "8.0.2"
val gradleVersion =
        VersionSupport(
            name = "Gradle",
            supported = setOf(
                recommendedGradleVersion,
            )
        ).check(GradleVersion.current().version, isWrapperBuild)

val kotlinVersion =
        VersionSupport(
                name = "Kotlin",
                supported = setOf(
                    // comment notes Gradle versions which bundle that Kotlin version
                    KotlinVersion(1, 8, 10),   // 8.0
                )
        ).check(KotlinVersion.CURRENT, isWrapperBuild)

group = "org.jbali"
check(name == "jbali-gradle-tools")
val toolsVersion = properties["toolsVersion"] // TODO get from git
toolsVersion?.let {
    version = "${it}_gradle-${gradleVersion}"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    dependencies {
        implementation(gradleApi())
        // TODO this is not quite correct, because the kotlinVersion can be e.g. 1.3.72,
        //      but the user of these tools wants extensions for version 1.4.20 of the gradle plugin
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

tasks {
    val wrapper by existing(Wrapper::class) {
        gradleVersion = recommendedGradleVersion
        distributionType = Wrapper.DistributionType.ALL // get sources
        distributionSha256Sum = "47a5bfed9ef814f90f8debcbbb315e8e7c654109acd224595ea39fca95c5d4da"
    }
}

if (toolsVersion != null) {
    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                // TODO determine this from the configured git remote
                url = uri("https://maven.pkg.github.com/bartvanheukelom/jbali-gradle-tools")
                credentials {
                    username = System.getenv("GITHUB_ACTOR") ?: "bartvanheukelom"
                    password = System.getenv("GITHUB_TOKEN")
                        ?: file(".github-token").takeIf { it.exists() }?.readText()?.trim()
                        ?: System.console()?.readPassword("GitHub Personal Access Token: ")?.joinToString("")
                        ?: throw IllegalStateException("No GITHUB_TOKEN env var and no console available to prompt for token")
                }
            }
        }
    }
} else {
    tasks.named("publish") {
        doFirst {
            throw IllegalStateException("Cannot publish without toolsVersion property set")
        }
    }
}


// --------------- helpers -------------- //

data class VersionSupport<V : Any>(
        val name: String,
        val supported: Set<V>,
        val unsupported: Set<V> = emptySet()
) {
    fun check(current: V, skip: Boolean = false): V {
        if (!skip) {
            if (current in unsupported) {
                throw IllegalStateException("This build does not support $name version $current")
            }
            if (current !in supported) {
                throw IllegalStateException("This build is untested with $name version $current")
            }
        }
        return current
    }
}
