package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.ListProperty
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
        //
        // See https://github.com/MinecraftForge/ForgeGradle/issues/358
        // See https://github.com/md-5/SpecialSource/issues/40
        target.plugins.withId("net.minecraftforge.gradle") {
            val cls = Class.forName(
                "net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace",
                false,
                javaClass.classLoader,
            )
            @Suppress("UNCHECKED_CAST")
            target.tasks.withType(cls as Class<Task>).configureEach {
                val args = property("args") as ListProperty<String>
                args.add("--stable")
            }
        }
    }
}
