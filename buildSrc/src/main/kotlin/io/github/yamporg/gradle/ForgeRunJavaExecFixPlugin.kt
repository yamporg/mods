package io.github.yamporg.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType

class ForgeRunJavaExecFixPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Fix ForgeGradle creating new JavaExec task instead of using
        // project.javaexec. That should be have been fixed in 4.0.15.
        // But for some reason it wasn’t.
        //
        // Basically, we want these run tasks to use java.toolchain launcher.
        //
        // Note that JavaExec is registered only after all projects are evaluated.
        // See https://github.com/MinecraftForge/ForgeGradle/blob/a74f4b155d3c41f495e4e03398ead9a95ef831f8/src/common/java/net/minecraftforge/gradle/common/util/Utils.java#L631-L648
        target.plugins.withId("net.minecraftforge.gradle") {
            val userDevExtension = Class.forName(
                "net.minecraftforge.gradle.userdev.UserDevExtension",
                false, javaClass.classLoader
            )
            val minecraftExtension = userDevExtension.superclass
            val getRunsMethod = minecraftExtension.getDeclaredMethod("getRuns")
            val runConfig = Class.forName(
                "net.minecraftforge.gradle.common.util.RunConfig",
                false, javaClass.classLoader
            )
            val getTaskNameMethod = runConfig.getDeclaredMethod("getTaskName")

            @Suppress("UNCHECKED_CAST")
            val minecraftRuns = getRunsMethod.invoke(target.extensions["minecraft"]) as NamedDomainObjectContainer<Any> // <RunConfig>

            val java = target.extensions["java"] as JavaPluginExtension
            val javaToolchains = target.extensions["javaToolchains"] as JavaToolchainService
            val toolchain = javaToolchains.launcherFor(java.toolchain)
            minecraftRuns.configureEach {
                val runTaskName = getTaskNameMethod.invoke(this) as String
                target.tasks.withType(JavaExec::class).matching {
                    it.name == runTaskName
                }.configureEach {
                    javaLauncher.set(toolchain)
                }
            }
        }
    }
}
