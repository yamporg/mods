package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin

class UploadTaskPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("upload") {
            group = BasePlugin.UPLOAD_GROUP
            dependsOn(
                target.tasks.matching {
                    it.group == group && it != this
                }
            )
        }
    }
}
