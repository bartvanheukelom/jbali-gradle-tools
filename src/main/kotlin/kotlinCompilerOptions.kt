package org.jbali.gradle

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun KotlinCommonToolOptions.compilerArg(arg: String) {
    @Suppress("SuspiciousCollectionReassignment")
    freeCompilerArgs += arg
}

@Deprecated("enabled by default in newer versions... but since when?")
fun KotlinCommonToolOptions.enableInlineClasses() {
    compilerArg("-XXLanguage:+InlineClasses")
}

/**
 * Adds compiler argument `-X${name}[=${value}]`
 */
fun KotlinCommonToolOptions.compilerXArg(name: String, value: Any? = null) {
    compilerArg(buildString {
        append("-X")
        append(name)
        if (value != null) {
            append('=')
            append(value.toString())
        }
    })
}


@Deprecated("enabled by default in newer versions... but since when?")
fun KotlinCommonToolOptions.inlineClasses() {
    compilerXArg("inline-classes")
}

/**
 * Enable parallel compilation of a single module, available since 1.6.20.
 *
 * [https://kotlinlang.org/docs/whatsnew1620.html#support-for-parallel-compilation-of-a-single-module-in-the-jvm-backend]
 *
 * @param n The number of threads you want to use. It should not be greater than your number of CPU cores;
 *           otherwise, parallelization stops being effective because of switching context between threads.
 *           0 to use a separate thread for each CPU core.
 */
fun KotlinCommonToolOptions.setBackendThreads(n: Int = 0) {
    compilerXArg("backend-threads", n)
}


// ----------- jvm-default ------------- //
// TODO shouldn't the receivers be KotlinToolOptions?

/**
 * Allows using [JvmDefault].
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("use jvmDefaultAll or jvmDefaultAllCompatibility")
fun KotlinCommonToolOptions.jvmDefaultEnable() {
    compilerXArg("jvm-default", "enable")
}

/**
 * Allows using [JvmDefault] in compatibility mode.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("use jvmDefaultAll or jvmDefaultAllCompatibility")
fun KotlinCommonToolOptions.jvmDefaultCompatibility() {
    compilerXArg("jvm-default", "compatibility")
}

/**
 * Make all non-abstract members of Kotlin interfaces `default` for the Java classes implementing them.
 * [https://kotlinlang.org/docs/java-to-kotlin-interop.html#default-methods-in-interfaces]
 */
fun KotlinCommonToolOptions.jvmDefaultAll() {
    compilerXArg("jvm-default", "all")
}

/**
 * [https://kotlinlang.org/docs/java-to-kotlin-interop.html#compatibility-mode-for-default-methods]
 */
fun KotlinCommonToolOptions.jvmDefaultAllCompatibility() {
    compilerXArg("jvm-default", "all-compatibility")
}


// ------------ JVM ------------- //

fun KotlinJvmOptions.setJvmTarget(javaVersion: JavaVersion) {
    jvmTarget = javaVersion.toString()
}

// ------------ Experimental -------- //

enum class Experimentals(val featureName: String) {
    Contracts("kotlin.contracts.ExperimentalContracts"),
    Experimental("kotlin.Experimental"),
    @Deprecated("since 1.7.0")
    RequiresOptIn("kotlin.RequiresOptIn")
}

fun KotlinCommonToolOptions.use(feature: Experimentals) {
    useExperimental(feature.featureName)
}

fun KotlinCommonToolOptions.useExperimental(feature: String) {
    compilerXArg("use-experimental", feature)
}

fun KotlinCommonToolOptions.optIn(feature: Experimentals) {
    compilerXArg("opt-in", feature.featureName)
}

// doesn't appear like it can be used, will complain "this class can only be used as..."
inline fun <reified C : Any> KotlinCommonToolOptions.useExperimental() {
    compilerXArg("use-experimental", C::class.qualifiedName!!)
}
