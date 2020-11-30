import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
    // TODO https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
//    `kotlin-dsl`
}

val supportedGradleVersions = setOf(
        "6.7.1"
)
check(org.gradle.util.GradleVersion.current().version in supportedGradleVersions) {
    "This build script is untested with Gradle version ${org.gradle.util.GradleVersion.current()}. Tested versions are $supportedGradleVersions"
}

val kotlinVersion = (gradle as ExtensionAware).extra["kotlinVersion"] as KotlinVersion

val kotlinPlugin = plugins.getPlugin(KotlinPluginWrapper::class.java)
val kotlinPluginVersion = kotlinPlugin.kotlinPluginVersion
check(kotlinPluginVersion == "$kotlinVersion") {
    "kotlinPluginVersion $kotlinPluginVersion != kotlinVersion $kotlinVersion"
}

group = "org.jbali"

repositories {

    jcenter()

    // for kotlin-gradle-plugin
    maven { url = uri("https://plugins.gradle.org/m2/") }

}

kotlin {

    dependencies {

        implementation(gradleApi())
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    }
    
}
