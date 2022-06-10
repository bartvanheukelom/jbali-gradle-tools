import org.gradle.util.GradleVersion

plugins {
    kotlin("jvm") version KotlinVersion.CURRENT.toString()
}

group = "org.jbali"
check(name == "jbali-gradle-settings-tools")

// if you change this, remove all the old jars from lib!
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
    
    val wrapper by existing(Wrapper::class) {
        // after updating this, run `./gradlew wrapper && ./gradlew build`, then commit the jar
        gradleVersion = "7.4.2"
        // get sources
        distributionType = Wrapper.DistributionType.ALL
    }
    
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
