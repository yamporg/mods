package io.github.yamporg.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class ForgeIdeaModuleFixPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Turns out ForgeGradle is not smart enough to figure out module name
        // for IntelliJ IDEA run configurations in multi-project builds. We fix
        // that by setting ideaModule on each run config.
        target.plugins.withId("net.minecraftforge.gradle") {
            val userDevExtension = Class.forName(
                "net.minecraftforge.gradle.userdev.UserDevExtension",
                false, javaClass.classLoader
            )
            val minecraftExtension = userDevExtension.superclass // net.minecraftforge.gradle.common.util.MinecraftExtension
            val getRunsMethod = minecraftExtension.getDeclaredMethod("getRuns")
            val runConfig = Class.forName(
                "net.minecraftforge.gradle.common.util.RunConfig",
                false, javaClass.classLoader
            )
            val ideaModuleField = runConfig.getDeclaredField("ideaModule").also {
                // Set field to be accessible because getter returns default value.
                // We’ll use that to check whether the field was already set.
                it.isAccessible = true
            }

            @Suppress("UNCHECKED_CAST")
            val minecraftRuns = getRunsMethod.invoke(target.extensions["minecraft"]) as NamedDomainObjectContainer<Any> // <RunConfig>
            minecraftRuns.configureEach {
                if (ideaModuleField.get(this) != null) {
                    return@configureEach
                }
                if (target.rootProject == target) {
                    // At least that case FG should be able to handle.
                    return@configureEach
                }
                val targetPath = target.path.replace(':', '.')
                ideaModuleField.set(
                    this,
                    target.rootProject.name + targetPath + ".main"
                )
            }
        }
    }
}
