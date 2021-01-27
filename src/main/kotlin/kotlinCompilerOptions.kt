package org.jbali.gradle

import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions


fun KotlinCommonToolOptions.enableInlineClasses() {
//    freeCompilerArgs += "-Xinline-classes"
    freeCompilerArgs += "-XXLanguage:+InlineClasses"
}

fun KotlinCommonToolOptions.inlineClasses() {
    freeCompilerArgs += "-Xinline-classes"
}

/**
 * Allows using [JvmDefault].
 */
fun KotlinCommonToolOptions.jvmDefaultEnable() {
    freeCompilerArgs += "-Xjvm-default=enable"
}
/**
 * Allows using [JvmDefault] in compatibility mode.
 */
fun KotlinCommonToolOptions.jvmDefaultCompatibility() {
    freeCompilerArgs += "-Xjvm-default=compatibility"
}

enum class Experimentals(val featureName: String) {
    Contracts("kotlin.contracts.ExperimentalContracts"),
    Experimental("kotlin.Experimental"),
    RequiresOptIn("kotlin.RequiresOptIn")
}

fun KotlinCommonToolOptions.use(feature: Experimentals) {
    useExperimental(feature.featureName)
}

fun KotlinCommonToolOptions.useExperimental(feature: String) {
    freeCompilerArgs += "-Xuse-experimental=$feature"
}

fun KotlinCommonToolOptions.optIn(feature: Experimentals) {
    freeCompilerArgs += "-Xopt-in=${feature.featureName}"
}

// doesn't appear like it can be used, will complain "this class can only be used as..."
inline fun <reified C : Any> KotlinCommonToolOptions.useExperimental() {
    freeCompilerArgs += "-Xuse-experimental=${C::class.qualifiedName}"
}
