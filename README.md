# jbali-gradle-tools

Utility code for gradle build scripts

## Upgrading Gradle

- Upgrade `settings-tools` first, see its README
- Update `recommendedGradleVersion` and `kotlinVersion` in `build.gradle.kts`
- Run `./gradlew wrapper`
- Recommended: (re)add `distributionSha256Sum=...` to `gradle/wrapper/gradle-wrapper.properties`
- Run `./gradlew build`
