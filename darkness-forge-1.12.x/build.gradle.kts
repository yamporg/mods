plugins {
    id("net.minecraftforge.gradle") version "5.1.58"
    id("io.github.CDAGaming.cursegradle") version "1.6.0"
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("io.github.yamporg.gradle.env-version")
    id("io.github.yamporg.gradle.upload-task")
    id("io.github.yamporg.gradle.curse-release")
    id("io.github.yamporg.gradle.github-release")
    id("io.github.yamporg.gradle.reproducible-builds")
}

minecraft {
    mappings("stable", "39-1.12")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    runs {
        create("client") {
            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "debug",
                    "fml.coreMods.load" to "io.github.yamporg.darkness.LoadingPlugin",
                ),
            )
        }
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2860")
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.processResources {
    inputs.property("version", version)
    filesMatching("/mcmod.info") {
        expand("version" to version.toString())
    }
}

tasks.jar {
    manifest.attributes(
        "FMLCorePlugin" to "io.github.yamporg.darkness.LoadingPlugin",
        "FMLAT" to "accesstransformer.cfg",
    )
}

curseforge {
    project {
        id = "363102"
        releaseType = "release"

        changelogType = "markdown"
        changelog = file("CHANGELOG.md")

        mainArtifact(tasks["jar"])
        addArtifact(tasks["sourcesJar"])
    }
}

githubRelease {
    body(file("CHANGELOG.md").readText())
    releaseAssets(tasks.jar.get().archiveFile)
}
