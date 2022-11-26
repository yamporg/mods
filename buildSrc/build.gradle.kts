plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
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
    }
}
