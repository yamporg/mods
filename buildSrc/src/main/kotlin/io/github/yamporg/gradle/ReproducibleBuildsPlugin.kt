package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.withType

class ReproducibleBuildsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Make archive builds reproducible.
        //
        // See https://docs.gradle.org/6.8.1/userguide/working_with_files.html#sec:reproducible_archives
        target.tasks.withType(AbstractArchiveTask::class).configureEach {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }

        // Set zero timestamps in reobfJar tasks too.
        //
        // Note that we don’t depend on ForgeGradle and instead use
        // UserDevPlugin’s classloader to find the class at runtime.
        // Otherwise, it’d be possible to simply do the following:
        //
        //     tasks.withType<net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace> {
        //         args += "--stable"
        //         tool = "net.md-5:SpecialSource:1.8.6:shaded"
        //     }
        //
        // See https://github.com/MinecraftForge/ForgeGradle/issues/358
        // See https://github.com/md-5/SpecialSource/issues/40
        //
        // And --stable option is available since v1.8.5 while ForgeGradle uses v1.8.3.
        // See https://github.com/md-5/SpecialSource/commit/5dbf0ee84ae02085b4bce7ea9101f16ed6ba8f29
        target.plugins.withId("net.minecraftforge.gradle") {
            val cls =
                Class.forName(
                    "net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace",
                    false,
                    javaClass.classLoader
                )
            val jarExec = cls.superclass // net.minecraftforge.gradle.common.task.JarExec
            val getArgs = jarExec.getDeclaredMethod("getArgs")
            val setArgs = jarExec.getDeclaredMethod("setArgs", Array<String>::class.java)
            val setTool = jarExec.getDeclaredMethod("setTool", String::class.java)
            @Suppress("UNCHECKED_CAST")
            target.tasks.withType(cls as Class<Task>).configureEach {
                setArgs.invoke(this, getArgs.invoke(this) as Array<String> + "--stable")
                setTool.invoke(this, "net.md-5:SpecialSource:1.8.6:shaded")
            }
        }
    }
}
