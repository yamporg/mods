package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

class ForgeModInfoVersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Expand ${version} token in mcmod.info resource file.
        // That gives us a single place to update when bumping versions.
        target.tasks.named<ProcessResources>("processResources") {
            inputs.property("version", target.version)
            filesMatching("/mcmod.info") {
                expand(
                    "version" to target.version.toString()
                )
            }
        }
    }
}
