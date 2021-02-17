package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class EnvVersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // EnvVersionPlugin allows setting the version from Git tag ref of a release.
        // Note that we default to "snapshot" version unless ref is a tag, and we trigger
        // releases by pushing Git tags. Tags are similar to what Go submodules have.
        // Example: refs/tags/darkness-1.12.x/v0.3.1

        val ref = System.getenv("GITHUB_REF") ?: ""
        val tagPrefix = "refs/tags/"
        if (!ref.startsWith(tagPrefix)) {
            target.version = "SNAPSHOT"
            return
        }

        target.version = ref.removePrefix(tagPrefix).split("/v", limit = 2).last()
    }
}
