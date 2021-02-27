pluginManagement {
    repositories {
        maven { url = uri("https://files.minecraftforge.net/maven") }
        maven { url = uri("https://repo.spongepowered.org/maven") }
        gradlePluginPortal()
    }
    // The following resolution strategy allows using plugin DSL
    // even though MixinGradle and ForgeGradle do not follow
    // artifact naming convention for Gradle plugins.
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "net.minecraftforge.gradle" -> useModule("net.minecraftforge.gradle:ForgeGradle:${requested.version}")
                "org.spongepowered.mixin" -> useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

rootProject.name = "mods"

include(
    "darkness-forge-1.12.x",
    "ifbhfix-forge-1.12.x"
)
