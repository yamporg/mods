package io.github.yamporg.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class CurseReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // CurseReleasePlugin helps with setting up token for CurseForge release uploads.
        // It’ll also enable debug mode if token is empty or we are not running in CI environment.
        target.plugins.withId("io.github.CDAGaming.cursegradle") {
            val githubActions = System.getenv("GITHUB_ACTIONS") == "true"
            val curseForgeToken = System.getenv("CURSEFORGE_TOKEN") ?: ""

            val curseExtensionClass =
                Class.forName(
                    "com.matthewprenger.cursegradle.CurseExtension",
                    false,
                    javaClass.classLoader,
                )
            val optionsMethod = curseExtensionClass.getDeclaredMethod("options", Action::class.java)
            val setApiKeyMethod = curseExtensionClass.getDeclaredMethod("setApiKey", Any::class.java)

            val optionsClass =
                Class.forName(
                    "com.matthewprenger.cursegradle.Options",
                    false,
                    javaClass.classLoader,
                )
            val setDebugMethod = optionsClass.getDeclaredMethod("setDebug", Boolean::class.java)

            target.extensions.configure(curseExtensionClass) {
                val hasToken = curseForgeToken != ""
                if (hasToken) {
                    setApiKeyMethod.invoke(this, curseForgeToken)
                }

                if (!githubActions || !hasToken) {
                    optionsMethod.invoke(
                        this,
                        Action<Any>({
                            setDebugMethod.invoke(this, true)
                        },),
                    )
                }
            }
        }
    }
}
