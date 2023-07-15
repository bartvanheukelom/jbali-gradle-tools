package org.jbali.gradle

import org.gradle.api.artifacts.dsl.RepositoryHandler


// TODO more specific name and make this name an extension of DependencyHandlerScope
object Kotlin {
    private const val modulePrefix = "org.jetbrains.kotlin:kotlin-"

    object StdLib {
        const val common = "${modulePrefix}stdlib-common"
        const val js = "${modulePrefix}stdlib-js"
        const val jvm = "${modulePrefix}stdlib"
        const val jdk8 = "${modulePrefix}stdlib-jdk8"
    }

    object Test {
        const val annotationsCommon = "${modulePrefix}test-annotations-common"
        const val common = "${modulePrefix}test-common"
        const val js = "${modulePrefix}test-js"
        const val jvm = "${modulePrefix}test"
        const val junit = "${modulePrefix}test-junit"
    }

    const val reflect = "${modulePrefix}reflect"

}

object KotlinX {
    object Serialization {
        const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json"
    }

    object SerializationRuntime {
        const val common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common"
        const val js = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js"
        const val jvm = "org.jetbrains.kotlinx:kotlinx-serialization-runtime"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core"
    }

}

/**
 * Add the private repository for the kotlinx.html dependencies.
 * Source: [https://github.com/kotlin/kotlinx.html/wiki/Getting-started]
 */
fun RepositoryHandler.kotlinxHtmlJbSpace() {
    maven { setUrl("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

/**
 * For a given Kotlin version, contains the latest compatible versions of essential libraries:
 * Serialization, Coroutines, Dokka.
 */
data class KotlinLinkedLibVersions(
    // https://github.com/Kotlin/kotlinx.serialization/releases
    val serialization: String,
    // https://github.com/Kotlin/kotlinx.coroutines/releases
    val coroutines: String,
    // https://github.com/Kotlin/dokka/releases
    // https://mvnrepository.com/artifact/org.jetbrains.dokka/dokka-gradle-plugin
    val dokka: String,
)

val kotlinLinkedLibVersions: Map<KotlinVersion, KotlinLinkedLibVersions> = mapOf(
    //                                                serialz  coro          dokka
    KotlinVersions.V1_8_10 to KotlinLinkedLibVersions("1.5.0", "1.7.0-Beta", "1.8.10"),
    KotlinVersions.V1_9_0  to KotlinLinkedLibVersions("1.5.1", "1.7.2",      "1.8.20"),
)
