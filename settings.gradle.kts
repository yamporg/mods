pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://files.minecraftforge.net/maven") }
    }
}

rootProject.name = "mods"

include(
    "darkness-forge-1.12.x",
    "ifbhfix-forge-1.12.x",
    "noprecipebook-forge-1.12.x",
)
