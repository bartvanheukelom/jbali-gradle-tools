import org.gradle.util.GradleVersion

plugins {
    kotlin("jvm") version KotlinVersion.CURRENT.toString()
}

group = "org.jbali"
check(name == "jbali-gradle-settings-tools")

val toolsVersion = "3"
val gradleVersion = GradleVersion.current().version
val variantVersion = "gradle-$gradleVersion"
version = "${toolsVersion}_$variantVersion"

repositories {
    mavenCentral()
}

kotlin {
    dependencies {
        implementation(gradleApi())
    }
}

tasks {
    jar {
        destinationDirectory.set(file("lib"))
        archiveFileName.set("$variantVersion.jar")

        manifest {
            attributes.let {
                it["Tools-Version"] = toolsVersion
                it["Gradle-Version"] = gradleVersion
                it["Dist-Version"] = project.version
            }
        }

    }
}
