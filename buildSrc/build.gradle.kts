plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

gradlePlugin {
    plugins {
        register("env-version") {
            id = "io.github.yamporg.gradle.env-version"
            implementationClass = "io.github.yamporg.gradle.EnvVersionPlugin"
        }
        register("upload-task") {
            id = "io.github.yamporg.gradle.upload-task"
            implementationClass = "io.github.yamporg.gradle.UploadTaskPlugin"
        }
        register("curse-release") {
            id = "io.github.yamporg.gradle.curse-release"
            implementationClass = "io.github.yamporg.gradle.CurseReleasePlugin"
        }
        register("github-release") {
            id = "io.github.yamporg.gradle.github-release"
            implementationClass = "io.github.yamporg.gradle.GithubReleasePlugin"
        }
        register("reproducible-builds") {
            id = "io.github.yamporg.gradle.reproducible-builds"
            implementationClass = "io.github.yamporg.gradle.ReproducibleBuildsPlugin"
        }
        register("forge-modinfo-version") {
            id = "io.github.yamporg.gradle.forge-modinfo-version"
            implementationClass = "io.github.yamporg.gradle.ForgeModInfoVersionPlugin"
        }
        register("forge-prepare-runs-fix") {
            id = "io.github.yamporg.gradle.forge-prepare-runs-fix"
            implementationClass = "io.github.yamporg.gradle.ForgePrepareRunsFixPlugin"
        }
        register("minecraft-lwjgl-version-fix") {
            id = "io.github.yamporg.gradle.minecraft-lwjgl-version-fix"
            implementationClass = "io.github.yamporg.gradle.MinecraftLWJGLVersionFixPlugin"
        }
    }
}
