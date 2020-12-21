package org.jbali.gradle

import org.gradle.api.Project
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor

open class ProjectGroup<T : ProjectWrapper>(
        private val groupProject: Project,
        private val type: KClass<out T>,
        val children: Map<String, T> =
                groupProject.childProjects.mapValues { (_, cp) ->
                    try {
                        type.primaryConstructor!!.call(cp)
                    } catch (e: Throwable) {
                        throw RuntimeException("Exception while wrapping $cp in ${type.simpleName}: $e", e)
                    }
                }
) : Set<T> by children.values.toSet() {

    val name get() = groupProject.name

    init {
        @Suppress("LeakingThis")
        groupProject.logger.info("$this: ${children.keys}")
    }

//    fun configure(action: T.() -> Unit): Iterable<T> =
//            configure(children.values, action)

    operator fun div(subProjectName: String): T =
            children.getValue(subProjectName)

    override fun toString() = "ProjectGroup($name)"
    override fun hashCode() = groupProject.hashCode()
    override fun equals(other: Any?) = other is ProjectGroup<*> && other.groupProject == groupProject

    operator fun getValue(projectGroup: ProjectGroup<T>, property: KProperty<*>): T =
        when (property.returnType.classifier) {
            type -> div(property.name)
            else -> throw NoSuchElementException()
        }

}