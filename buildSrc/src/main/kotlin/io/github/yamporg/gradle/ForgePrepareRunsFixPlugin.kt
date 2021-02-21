package io.github.yamporg.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType

class ForgePrepareRunsFixPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Register afterEvaluate action iff ForgeGradle plugin has been applied.
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
            val getTaskNameMethod = runConfig.getDeclaredMethod("getTaskName")
            val ideaModuleField = runConfig.getDeclaredField("ideaModule").also {
                // Set field to be accessible because getter returns default value.
                // We’ll use that to check whether the field was already set.
                it.isAccessible = true
            }

            // Turns out ForgeGradle is not smart enough to figure out module name
            // for IntelliJ IDEA run configurations in multi-project builds.
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

            // Also, while we are at it, fix ForgeGradle creating new
            // JavaExec task instead of using project.javaexec. That
            // should be fixed in 4.0.15 but we can’t upgrade yet due
            // to the incompatibilities with 4.0.14.
            //
            // Basically, we want these run tasks to use java.toolchain launcher.
            //
            // See also https://github.com/MinecraftForge/ForgeGradle/issues/748
            //
            // Note that JavaExec is registered only after all projects are evaluated.
            // See https://github.com/MinecraftForge/ForgeGradle/blob/a74f4b155d3c41f495e4e03398ead9a95ef831f8/src/common/java/net/minecraftforge/gradle/common/util/Utils.java#L631-L648
            val java = target.extensions["java"] as JavaPluginExtension
            val javaToolchains = target.extensions["javaToolchains"] as JavaToolchainService
            val toolchain = javaToolchains.launcherFor(java.toolchain)
            minecraftRuns.configureEach {
                val runTaskName = getTaskNameMethod.invoke(this) as String
                target.tasks.matching {
                    it.name == runTaskName
                }.withType<JavaExec> {
                    javaLauncher.set(toolchain)
                }
            }

            // For some reason resources are not loaded in runClient task
            // unless we put them to class dir directly. Newer version of
            // Minecraft work just fine though. That’s weird.
            //
            // See https://github.com/MinecraftForge/ForgeGradle/issues/717
            // See also https://redd.it/e4hfzz
            //
            // The current workaround (with minor changes) comes from Chiseled Me mod.
            // See https://github.com/necauqua/chiseled-me/blob/f8b94ab08ff39c46c58120027de4361597a1a473/build.gradle#L30-L39
            target.tasks.matching {
                it.name == "prepareRuns"
            }.configureEach {
                doLast {
                    project.copy {
                        from("build/resources/main")
                        into("build/classes/java/main")
                    }
                }
            }
        }
    }
}
