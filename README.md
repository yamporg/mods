![CI](https://github.com/yamporg/mods/workflows/CI/badge.svg)
![CD](https://github.com/yamporg/mods/workflows/CD/badge.svg)

# Mods

Monorepo for Minecraft mod projects.

Common build script tweaks are Gradle plugins in [buildSrc](buildSrc). The idea is that each individual project decides which tweaks to apply as well as which plugin third-party versions to use. That is, buildSrc plugins must not depend on any third-party plugin, but may provide integration (e.g. [io.github.yamporg.gradle.reproducible-builds](buildSrc/src/main/kotlin/io/github/yamporg/gradle/ReproducibleBuildsPlugin.kt) plugin fixes non-determinism in [ForgeGradle](https://github.com/MinecraftForge/ForgeGradle) tasks).

Currently, this project contains only [Forge](https://github.com/MinecraftForge/MinecraftForge) mods, but it should be possible to have [Fabric](https://fabricmc.net) mods alongside.

## Development Environment

It’s recommended to use the latest [IntelliJ IDEA](https://jetbrains.com/idea) release with [Minecraft Dev for IntelliJ](https://minecraftdev.org) plugin. [Gradle](https://gradle.org) distribution specified in [gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties) will be [automatically downloaded on project import](https://jetbrains.com/help/idea/gradle.html#604e9f91).

Note that you don’t have to install JDK for each project, [Gradle handles that for you](https://blog.gradle.org/java-toolchains)!

## Getting 503’s from CurseForge (or is it Cloudflare?)

If you are getting 503’s for CurseForge API, set up a proxy. E.g. something like the command below is good enough for local testing.
```
CURSEFORGE_TOKEN=xxx gradle :ifbhfix-1.12.x:curseforge -D'org.gradle.jvmargs=-DsocksProxyHost=45.79.207.110 -DsocksProxyPort=9200'
```
Also, I have no freaking idea why is that happening. I’ve tried connecting from two distinct local ISPs and that didn’t help.
