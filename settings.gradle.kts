pluginManagement {
    // TODO automatically use the correct version for the gradle distribution
    val kotlinVersion = KotlinVersion(1, 3, 72)
    val kotlinEAPSuffix = null

    // ============ shared TODO find way to share this =========== //

    val kotlinVersionString = "$kotlinVersion${kotlinEAPSuffix ?: ""}"
    @Suppress("SENSELESS_COMPARISON")
    val kotlinEAP = kotlinEAPSuffix != null

    (gradle as ExtensionAware).extra.let { e ->

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

    repositories {
        jcenter()
        gradlePluginPortal()

        if (kotlinEAP) {
            maven("https://dl.bintray.com/kotlin/kotlin-eap")
            maven("https://kotlin.bintray.com/kotlinx")
        }
    }

    plugins {
        listOf(
                "org.jetbrains.kotlin.jvm",
                "org.jetbrains.kotlin.multiplatform",
                "org.jetbrains.kotlin.plugin.serialization"
        ).forEach {
            id(it) version kotlinVersionString
        }
    }

}

rootProject.name = "jbali-gradle-tools"
