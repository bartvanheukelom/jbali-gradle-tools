import org.gradle.util.GradleVersion

plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

val gradleVersion =
        VersionSupport(
                name = "Gradle",
                supported = setOf(
                    "6.7.1",
                    "7.0"
                )
        ).check(GradleVersion.current().version)

val kotlinVersion =
        VersionSupport(
                name = "Kotlin",
                supported = setOf(
                    // comment notes Gradle version which bundles that Kotlin version
                    KotlinVersion(1, 3, 72), // 6.7.1
                    KotlinVersion(1, 4, 31)  // 7.0
                )
        ).check(KotlinVersion.CURRENT)

group = "org.jbali"
check(name == "jbali-gradle-tools")

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


// --------------- helpers -------------- //

data class VersionSupport<V : Any>(
        val name: String,
        val supported: Set<V>,
        val unsupported: Set<V> = emptySet()
) {
    fun check(current: V): V {
        if (current in unsupported) {
            throw IllegalStateException("This build does not support $name version $current")
        }
        if (current !in supported) {
            throw IllegalStateException("This build is untested with $name version $current")
        }
        return current
    }
}
