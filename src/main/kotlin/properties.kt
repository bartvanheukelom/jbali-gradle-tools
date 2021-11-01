package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension


fun ExtraPropertiesExtension.opt(name: String): Any? =
    if (has(name)) get(name) else null

val Gradle.props: ExtraPropertiesExtension
    get() = (this as ExtensionAware).extensions.extraProperties

fun Project.projectOrBuildProp(name: String): Any? =
    findProperty(name) ?: gradle.props.opt(name)

val Any?.isTrueProp: Boolean
    get() = this == true || this?.toString() == "true"
