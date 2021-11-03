package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

abstract class ProjectGroup<T : ProjectWrapper>(
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
    protected annotation class Child
    
    private val logger get() = groupProject.logger
    val name: String get() = groupProject.name
    
    /**
     * Get the names of children as declared with delegating properties in this group, e.g:
     *
     *     class Apps : ProjectGroup(...) {
     *         @Child server by this
     *         @Child worker by this
     *     }
     */
    val childNames =
        javaClass.kotlin.memberProperties
            .filter { it.hasAnnotation<Child>() }
            .map { it.name }
            .also { logger.info("${javaClass.simpleName}.childNames = $it") }
    
    /**
     * Paths of the children as determined in [childNames].
     */
    val childPaths by lazy {
        childNames.map { n -> "${groupProject.path}:$n" }
    }
    
    /**
     * Wrapped children as determined by Gradle's [Project.getChildProjects]
     */
    val children: Map<String, T> by lazy {
        logger.info("init ${this}.children")
        groupProject.childProjects.mapValues { (_, cp) ->
            try {
                wrapper(cp)
            } catch (e: Throwable) {
                throw RuntimeException("Exception while wrapping $cp in ${type.simpleName}: $e", e)
            }
        }
            .also { logger.info("${this}.children.size = ${it.size}") }
    }

    init {
        @Suppress("LeakingThis")
        logger.info("init $this: ${groupProject.childProjects.keys}")
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

    abstract fun configure()
    
}