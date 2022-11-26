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

repositories {
    maven { url = uri("https://cursemaven.com") }
}

dependencies {
    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2860")

    // https://www.curseforge.com/minecraft/mc-mods/shadowfacts-forgelin/files/2785465
    // Filename Forgelin-1.8.4.jar
    implementation("curse.maven:forgelin-248453:2785465")

    // https://www.curseforge.com/minecraft/mc-mods/tesla-core-lib/files/2891841
    // Filename tesla-core-lib-1.12.2-1.0.17.jar
    implementation("curse.maven:teslacorelib-254602:2891841")

    // https://www.curseforge.com/minecraft/mc-mods/industrial-foregoing/files/2745321
    // Filename industrialforegoing-1.12.2-1.12.13-237.jar
    implementation(fg.deobf("curse.maven:industrialforegoing-266515:2745321"))
}

minecraft {
    mappings("stable", "39-1.12")
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

curseforge {
    project {
        id = "446961"
        releaseType = "release"

        changelogType = "markdown"
        changelog = file("CHANGELOG.md")

        mainArtifact(tasks["jar"]) {
            relations {
                requiredDependency("industrial-foregoing")
            }
        }
        addArtifact(tasks["sourcesJar"])
    }
}

github {
    body = file("CHANGELOG.md").readText()
    setAssets(tasks.jar.get().archiveFile.get().asFile.path)
}
