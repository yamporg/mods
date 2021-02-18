package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin

class GithubReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("co.riiid.gradle") {
            // Move githubRelease task to upload group.
            @Suppress("UNCHECKED_CAST")
            val taskClass = Class.forName(
                "co.riiid.gradle.ReleaseTask",
                false, javaClass.classLoader
            ) as Class<Task>
            target.tasks.named("githubRelease", taskClass) {
                group = BasePlugin.UPLOAD_GROUP
            }

            // Do nothing if we are not in CI environment.
            val githubActions = System.getenv("GITHUB_ACTIONS") == "true"
            if (!githubActions) {
                return@withId
            }

            // Do nothing if we are not running on CD pipeline (otherwise GITHUB_TOKEN is empty).
            val githubToken = System.getenv("GITHUB_TOKEN") ?: ""
            if (githubToken == "") {
                return@withId
            }

            val cls = Class.forName(
                "co.riiid.gradle.GithubExtension",
                false, javaClass.classLoader
            )
            val setTokenMethod = cls.getDeclaredMethod("setToken", String::class.java)
            val setRepoMethod = cls.getDeclaredMethod("setRepo", String::class.java)
            val setTagNameMethod = cls.getDeclaredMethod("setTagName", String::class.java)
            val setTargetCommitishMethod = cls.getDeclaredMethod("targetCommitish", String::class.java)

            target.extensions.configure(cls) {
                setTokenMethod(this, githubToken)
                setRepoMethod(this, System.getenv("GITHUB_REPOSITORY"))
                setTagNameMethod(this, System.getenv("GITHUB_REF"))
                setTargetCommitishMethod(this, System.getenv("GITHUB_SHA"))
            }
        }
    }
}
