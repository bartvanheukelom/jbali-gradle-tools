package org.jbali.gradle

import org.gradle.api.Project
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor

open class ProjectGroup<T : ProjectWrapper>(
        private val groupProject: Project,
        private val type: KClass<out T>,
        private val wrapper: ProjectGroup<T>.(Project) -> T = { cp ->
            try {
                type.primaryConstructor!!.call(cp)
            } catch (ite: InvocationTargetException) {
                throw ite.cause!!
            }
        }
) {

    val name: String get() = groupProject.name

    val children: Map<String, T> by lazy {
        groupProject.logger.info("init ${this}.children")
        groupProject.childProjects.mapValues { (_, cp) ->
            try {
                wrapper(cp)
            } catch (e: Throwable) {
                throw RuntimeException("Exception while wrapping $cp in ${type.simpleName}: $e", e)
            }
        }
    }

    init {
        @Suppress("LeakingThis")
        groupProject.logger.info("init $this: ${groupProject.childProjects.keys}")
    }

//    fun configure(action: T.() -> Unit): Iterable<T> =
//            configure(children.values, action)

    operator fun div(subProjectName: String): T =
            children.getValue(subProjectName)

    override fun toString() = "ProjectGroup($name)"
    override fun hashCode() = groupProject.hashCode()
    override fun equals(other: Any?) = other is ProjectGroup<*> && other.groupProject == groupProject

    operator fun getValue(projectGroup: ProjectGroup<T>, property: KProperty<*>): T? =
        when (property.returnType.classifier) {
            type -> children[property.name]
            else -> throw NoSuchElementException("Cannot act as delegate for $property")
        }

}