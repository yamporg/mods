package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class UploadTaskPlugin : Plugin<Project> {
    companion object {
        const val GROUP_NAME = "upload"
    }

    override fun apply(target: Project) {
        target.tasks.register("upload") {
            group = GROUP_NAME
            dependsOn(
                target.tasks.matching {
                    it.group == group && it != this
                },
            )
        }
    }
}
