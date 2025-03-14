package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.internal.deprecation.DeprecatableConfiguration
import java.io.File

/**
 * Setting a breakpoint in a build.gradle.kts file doesn't always work correctly in IntelliJ,
 * (TODO report that bug)
 * but you can insert a call to this function, and put a (permanent) breakpoint in here.
 */
fun debugBreakpoint() {
    println("debugger")
}

/**
 * Add the given dependency to the `compileOnly` and `testImplementation` configurations.
 */
fun DependencyHandler.compileAndTest(dependencyNotation: Any) {
   add("compileOnly", dependencyNotation)
   add("testImplementation", dependencyNotation)
}

//fun <T> Iterable<T>.configure(action: T.() -> Unit) =
//        forEach {
//            it.action()
//        }

operator fun File.div(child: String) =
        File(this, child)

//val Project.childproject get() =
//    object : ReadOnlyProperty<Project, Project?> {
//        override fun getValue(thisRef: Project, property: KProperty<*>) =
//                thisRef.childProjects[property.name]
//    }

val Configuration.deprecatedForDeclaration: Boolean get() =
        when (this) {
            // TODO broken. some deprecated configs have this as empty array, but some non-deprecated ones also do?
            is DeprecatableConfiguration -> this.declarationAlternatives?.isNotEmpty() == true
            else -> false
        }

//    private fun constrain(dependency: String, constraint: String) {
//        project.configurations.forEach { conf ->
//            if (!conf.deprecatedForDeclaration) {
//                project.dependencies.constraints.add(conf.name, "$dependency:$constraint")
//            }
//        }
//    }

// TODO make this a task type as well
fun Project.bash(script: String) {
    this.exec {
        commandLine("bash", "-c", "set -e; $script")
    }
}

/**
 * Stores formal parameter names of constructors and methods in the generated class file so that the method
 * [java.lang.reflect.Executable.getParameters] from the Reflection API can retrieve them.
 */
fun CompileOptions.storeParameterNames() {
    compilerArgs.add("-parameters")
}



/**
 * Configure dependency resolution to throw a hard error if anything
 * in the builds tries to pull in one of the given dependencies.
 * Specify the dependencies as "$group:$name" (no version).
 */
fun Project.forbidDependencies(vararg forbiddenDependencies: String) {
    forbidDependencies(forbiddenDependencies.asSequence().toSet())
}

/**
 * Configure dependency resolution to throw a hard error if anything
 * in the builds tries to pull in one of the given dependencies.
 * Specify the dependencies as "$group:$name" or "$group:$name:$version".
 */
fun Project.forbidDependencies(forbiddenDependencies: Set<String>) {
    val project = this
    configurations.all {
        val config = this
        resolutionStrategy {
            eachDependency {
                fun check(dep: String) {
                    if (dep in forbiddenDependencies) {
                        // log separately because the thrown exception is sometimes hidden by an obscure error
                        val e = RuntimeException("Forbidden dependency in ${project.path} configuration ${config.name}: $dep")
                        logger.error("Error", e)
                        throw e
                    }
                }
                val dep = requested.group + ":" + requested.name
                val vdep = dep + ":" + requested.version
                check(dep)
                check(vdep)
            }
        }
    }
}

val Gradle.isWrapperBuild: Boolean get() =
    startParameter.taskRequests.any {
        it.projectPath == null && it.rootDir == null && it.args.any(Regex(":?wrapper")::matches)
    }
