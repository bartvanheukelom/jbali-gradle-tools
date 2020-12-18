package org.jbali.gradle.settings

import org.gradle.util.GradleVersion
import java.net.URLClassLoader
import java.util.jar.Manifest

object GradleSettingsToolsLib {

    private val result: String? by lazy {

        val cl = javaClass.classLoader as URLClassLoader
        val jarUrl = javaClass.protectionDomain.codeSource.location.file

        val mf: Manifest =
            cl
                .findResource("META-INF/MANIFEST.MF")
                .openStream()
                .use {
                    Manifest().apply {
                        read(it)
//                            println(mainAttributes.entries)
                    }
                }

        val distVersion = mf.mainAttributes.getValue("Dist-Version")

        val forGradleVersion = mf.mainAttributes.getValue("Gradle-Version")
        val curGradleVersion = GradleVersion.current().version

        if (curGradleVersion != forGradleVersion) {
            "jbali-gradle-settings-tools $distVersion @ $jarUrl was compiled for Gradle ${forGradleVersion}, " +
                        "you're trying to use it with $curGradleVersion"
        } else {
            null
        }

    }

    fun validate() {
        result?.let {
            throw IllegalStateException(it)
        }
    }

}
