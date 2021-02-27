package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class MinecraftLWJGLVersionFixPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Minecraft 1.12 requires both 2.9.4-nightly-20150209 and 2.9.2-nightly-20140822,
        // and the latter does not exist. We fix that by using the newer version.
        //
        // See https://github.com/MinecraftForge/ForgeGradle/issues/627
        target.configurations.configureEach {
            resolutionStrategy {
                eachDependency {
                    if (requested.group == "org.lwjgl.lwjgl" && requested.name == "lwjgl-platform") {
                        useVersion("2.9.4-nightly-20150209")
                    }
                }
            }
        }
    }
}
