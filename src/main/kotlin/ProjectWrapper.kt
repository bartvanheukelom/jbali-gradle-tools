package org.jbali.gradle

import org.gradle.api.Action
import org.gradle.api.Project

abstract class ProjectWrapper(val native: Project) : Project by native {

    override fun hashCode() = native.hashCode()
    override fun equals(other: Any?) =
            other is Project && other.path == path

    override fun toString() = "${this.javaClass.simpleName} '$path'"

    override fun allprojects(action: Action<in Project>) {
        val src = this
        native.allprojects {
            val p = this
            try {
                action.execute(p)
            } catch (e: Throwable) {
                throw RuntimeException("Exception while trying to configure $p from ${src}.allprojects: $e", e)
            }
        }
    }

}