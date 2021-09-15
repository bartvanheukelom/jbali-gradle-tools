package org.jbali.gradle.settings

import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import java.io.File
import java.util.*

val Settings.properties: ExtraPropertiesExtension
    get() = (gradle as ExtensionAware).extensions.extraProperties

fun ExtraPropertiesExtension.opt(name: String): Any? =
    if (has(name)) get(name) else null

private const val propsLoadedProp = "compositeBuild.propertiesLoaded"

/**
 * Load build properties from the file named "compositeBuild.properties" (if it exists)
 * in this build's root directory, and all its ancestor directories.
 * If any of them define the same properties, higher directories will override lower directories.
 * The properties are stored in this build's [Gradle](org.gradle.api.invocation.Gradle) [ExtraPropertiesExtension].
 */
fun Settings.composableBuild() {
    if (!properties.has(propsLoadedProp)) {
    
        var dir: File = settings.rootDir
        while (true) {
            File(dir, "compositeBuild.properties")
                .takeIf(File::exists)
                ?.let { f ->
                    val fp = Properties()
                    f.reader().use(fp::load)
                    fp.forEach { (k, v) ->
                        var vv = v.toString()
                        
                        val dirVar = "\${dir}"
                        if (vv.startsWith(dirVar)) {
                            vv =
                                when {
                                    vv.length == dirVar.length ->
                                        dir.absolutePath
                                    vv[dirVar.length] == '/' ->
                                        File(dir, vv.substring(dirVar.length + 1)).absolutePath
                                    else ->
                                        throw IllegalArgumentException("Don't know how to handle $dirVar in '$vv'")
                                }
                        }
                        
                        properties[k.toString()] = vv
                    }
                }
            if (dir.parentFile == null) {
                break
            } else {
                dir = dir.parentFile
            }
        }
        
        properties[propsLoadedProp] = true
    }
}

fun Settings.compositeBuild(configure: CompositeBuildConfiguration.() -> Unit) {
    composableBuild()
    CompositeBuildConfiguration(this).configure()
}

class CompositeBuildConfiguration(
    private val settings: Settings
) {
    init { GradleSettingsToolsLib.validate() }

    private val logger = Logging.getLogger(javaClass)
    
    private val props = settings.properties

    fun includeNamedBuild(buildName: String, defaultPath: String? = null) {
        GradleSettingsToolsLib.validate()

        // resolve path
        val path = File(
            props.opt("build.$buildName.path")?.toString()
                ?: defaultPath
                ?: throw IllegalStateException("Path for build $buildName not defined")
        )
        if (!path.exists()) {
            throw IllegalArgumentException("Path for build $buildName does not exist: $path")
        }
        logger.info("${settings.rootProject.name} is including build $buildName from $path")

        // check path
        val skts = File(path, "settings.gradle.kts")
        if (!skts.exists()) {
            throw IllegalArgumentException("${skts.name} not found in $path")
        }

        // go!
        settings.includeBuild(path)

    }

}