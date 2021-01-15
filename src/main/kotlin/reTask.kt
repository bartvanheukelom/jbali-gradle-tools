package org.jbali.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering

fun Project.setupReTask() {
    tasks {
        val reTask: TaskProvider<Task> by registering {

            val reTask = this
            val regex = Regex(project.property("re").toString())

            // TODO also detect tasks that are _registered_ after reTask is _configured_
            val matches = allprojects
                .flatMap { it.tasks }
                .filter { it != reTask && it.path.matches(regex) }

            if (matches.isEmpty()) {
                throw IllegalArgumentException("No tasks matched $regex")
            } else {
                reTask.dependsOn(*matches.toTypedArray())
            }

            doLast {
                println("Successfully executed tasks:")
                matches.forEach {
                    println("- ${it.path}")
                }
            }
        }
    }
}
