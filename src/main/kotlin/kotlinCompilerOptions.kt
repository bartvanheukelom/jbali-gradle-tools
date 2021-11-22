package org.jbali.gradle

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun KotlinCommonToolOptions.compilerArg(arg: String) {
    @Suppress("SuspiciousCollectionReassignment")
    freeCompilerArgs += arg
}

fun KotlinCommonToolOptions.enableInlineClasses() {
    compilerArg("-XXLanguage:+InlineClasses")
}


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


fun KotlinCommonToolOptions.inlineClasses() {
    compilerXArg("inline-classes")
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
