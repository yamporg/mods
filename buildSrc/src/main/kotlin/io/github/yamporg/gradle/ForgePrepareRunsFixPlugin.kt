package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class ForgePrepareRunsFixPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // For some reason resources are not loaded in runClient task
        // unless we put them to class dir directly. Newer version of
        // Minecraft work just fine though. That’s weird.
        //
        // See https://github.com/MinecraftForge/ForgeGradle/issues/717
        // See also https://redd.it/e4hfzz
        //
        // The current workaround (with minor changes) comes from Chiseled Me mod.
        // See https://github.com/necauqua/chiseled-me/blob/f8b94ab08ff39c46c58120027de4361597a1a473/build.gradle#L30-L39
        target.plugins.withId("net.minecraftforge.gradle") {
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
