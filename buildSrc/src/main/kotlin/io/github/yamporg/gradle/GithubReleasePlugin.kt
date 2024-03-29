package io.github.yamporg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class GithubReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("com.github.breadmoirai.github-release") {
            // Move githubRelease task to upload group.
            @Suppress("UNCHECKED_CAST")
            val taskClass = Class.forName(
                "com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask",
                false,
                javaClass.classLoader,
            ) as Class<Task>
            target.tasks.named("githubRelease", taskClass) {
                group = UploadTaskPlugin.GROUP_NAME
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

            val githubRepository = System.getenv("GITHUB_REPOSITORY") ?: ""
            val (owner, repo) = githubRepository.split("/", limit = 2)

            val githubRef = System.getenv("GITHUB_REF") ?: ""
            val tagName = githubRef.removePrefix("refs/tags/")

            val githubSha = System.getenv("GITHUB_SHA") ?: ""

            val cls = Class.forName(
                "com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension",
                false,
                javaClass.classLoader,
            )
            val setTokenMethod = cls.getDeclaredMethod("setToken", CharSequence::class.java)
            val setRepoMethod = cls.getDeclaredMethod("setRepo", String::class.java)
            val setOwnerMethod = cls.getDeclaredMethod("setOwner", String::class.java)
            val setTagNameMethod = cls.getDeclaredMethod("setTagName", String::class.java)
            val setTargetCommitishMethod = cls.getDeclaredMethod("setTargetCommitish", String::class.java)
            val setReleaseNameMethod = cls.getDeclaredMethod("setReleaseName", String::class.java)

            target.extensions.configure(cls) {
                setTokenMethod(this, githubToken)
                setRepoMethod(this, repo)
                setOwnerMethod(this, owner)
                setTagNameMethod(this, tagName)
                setTargetCommitishMethod(this, githubSha)
                setReleaseNameMethod(this, tagName)
            }
        }
    }
}
