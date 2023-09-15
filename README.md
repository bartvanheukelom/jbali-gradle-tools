# jbali-gradle-tools

Utility code for gradle build scripts

## Upgrading Gradle

- Upgrade `settings-tools` first, see its README
- Update `recommendedGradleVersion` in `build.gradle.kts`
- Run `./gradlew wrapper`
- If you get "This build is untested with Kotlin version x.y.z", make that the `kotlinVersion` in `build.gradle.kts`
- Recommended: (re)add `distributionSha256Sum=...` to `gradle/wrapper/gradle-wrapper.properties`
- Run `./gradlew build`
