package org.jbali.gradle

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.api           (groupAndModule: String, version: String) = api           ("$groupAndModule:$version")
fun KotlinDependencyHandler.implementation(groupAndModule: String, version: String) = implementation("$groupAndModule:$version")
fun KotlinDependencyHandler.compileOnly   (groupAndModule: String, version: String) = compileOnly   ("$groupAndModule:$version")
fun KotlinDependencyHandler.runtimeOnly   (groupAndModule: String, version: String) = runtimeOnly   ("$groupAndModule:$version")
