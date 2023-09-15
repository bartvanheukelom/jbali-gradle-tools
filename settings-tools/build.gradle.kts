import org.gradle.util.GradleVersion

plugins {
    kotlin("jvm") version KotlinVersion.CURRENT.toString()
    `maven-publish`
}

group = "org.jbali"
check(name == "jbali-gradle-settings-tools")

// see README for what to do when changing these
val toolsVersion = "1.3"
val recommendedGradleVersion = "8.3"

val gradleVersion = GradleVersion.current().version
val variantVersion = "gradle-$gradleVersion"
version = "${toolsVersion}_$variantVersion"
logger.info("Version: $version")

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
        gradleVersion = recommendedGradleVersion
        distributionType = Wrapper.DistributionType.ALL  // get sources
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            // TODO determine this from the configured git remote
            url = uri("https://maven.pkg.github.com/bartvanheukelom/jbali-gradle-tools")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "bartvanheukelom"
                password = System.getenv("GITHUB_TOKEN")
                    ?: file("../.github-token").takeIf { it.exists() }?.readText()?.trim()
                    ?: System.console()?.readPassword("GitHub Personal Access Token: ")?.joinToString("")
                    ?: throw IllegalStateException("No GITHUB_TOKEN env var and no console available to prompt for token")
            }
        }
    }
}
