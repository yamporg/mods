plugins {
    id("net.minecraftforge.gradle") version "5.1.58"
    id("io.github.CDAGaming.cursegradle") version "1.6.0"
    id("co.riiid.gradle") version "0.4.2"
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
        "FMLAT" to "accesstransformer.cfg",
    )
}

curseforge {
    project {
        id = "829142"
        releaseType = "release"

        changelogType = "markdown"
        changelog = file("CHANGELOG.md")

        mainArtifact(tasks["jar"])
        addArtifact(tasks["sourcesJar"])
    }
}

github {
    body = file("CHANGELOG.md").readText()
    setAssets(tasks.jar.get().archiveFile.get().asFile.path)
}
