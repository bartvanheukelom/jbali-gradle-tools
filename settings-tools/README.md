# jbali-gradle-settings-tools

Utility code for Gradle builds that can be used in - and must be loaded from - the settings file.

## Use as a submodule

Since it's not possible to include a build and use it in the settings, this repository includes precompiled
Jars which can be included as follows (replace 7.6 with your Gradle version):

    buildscript {
        dependencies {
            // unfortunately can't access GradleVersion.current() here,
            // but the included jar will check for version mismatch
            classpath(files("./gradle-tools/settings-tools/lib/gradle-7.6.jar"))
        }
    }

## Building

### For a new Gradle version

To build the Jar for a new Gradle version, without changing the tools themselves:

- Get the latest version number at https://gradle.org/releases/
- Update `recommendedGradleVersion` in `build.gradle.kts`
- Run `./gradlew wrapper`
- Recommended: (re)add `distributionSha256Sum=...` to `gradle/wrapper/gradle-wrapper.properties`
- Run `./gradlew build`

### For a new version of the tools

To build the Jar for a new version of these tools, i.e. after code changes:

- Update `toolsVersion` in `build.gradle.kts`
- Delete all current Jars in `lib`
- Run `./gradlew build`
- Note that this only builds the Jar for the current Gradle version. If you must support older versions,
  repeatedly follow the steps under "For a new Gradle version" for each.
- Alternatively, use an external installation of Gradle instead of the wrapper one to build the Jar for that version
