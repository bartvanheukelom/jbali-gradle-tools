package org.jbali.gradle.settings

import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logging
import java.io.File
import java.util.*

fun Settings.compositeBuild(configure: CompositeBuildConfiguration.() -> Unit) {
    CompositeBuildConfiguration(this).configure()
}

class CompositeBuildConfiguration(
    private val settings: Settings
) {
    init { GradleSettingsToolsLib.validate() }

    private val logger = Logging.getLogger(javaClass)

    val props = Properties()

    // If this build is part of another one as a submodule (possibly nested), properties, including dependency paths,
    // can be provided by the main build in a properties file in some parent directory.
    // In the absence of such a file, it's assumed that this working copy is a root build.
    init {
        var dir: File = settings.rootDir
        while (true) {
            File(dir, "compositeBuild.properties")
                .takeIf(File::exists)
                ?.let { f ->
                    val fp = Properties()
                    f.reader().use(fp::load)
                    fp.forEach { (k, v) ->
                        var vv = v.toString()
                        // TODO do this at resolve time instead
                        if (vv.startsWith("./")) {
                            vv = File(dir, vv).absolutePath
                        }
                        props[k.toString()] = vv
                    }
                }
            if (dir.parentFile == null) {
                break
            } else {
                dir = dir.parentFile
            }
        }
    }


    fun includeNamedBuild(buildName: String, defaultPath: String? = null) {
        GradleSettingsToolsLib.validate()

        // resolve path
        val path = File(
            props.getProperty("build.$buildName.path")
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
