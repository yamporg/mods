<p align=center>
    <a title="CI workflow" href="https://github.com/yamporg/mods/actions/workflows/main.yaml">
        <img src="https://img.shields.io/github/actions/workflow/status/yamporg/mods/main.yaml?style=for-the-badge&logo=github" alt="CI workflow build status badge">
    </a>
    <a title="CD workflow" href="https://github.com/yamporg/mods/actions/workflows/release.yaml">
        <img src="https://img.shields.io/github/actions/workflow/status/yamporg/mods/release.yaml?style=for-the-badge&label=release&logo=github" alt="CD workflow release status badge">
    </a>
</p>

# Mods

Monorepo for Minecraft mod projects.

<p>
    <a title="Dynamic Darkness mod" href="https://www.curseforge.com/minecraft/mc-mods/dynamic-darkness">
        <img src="https://cf.way2muchnoise.eu/full_363102_Darkness%20%7C_downloads.svg?badge_style=for_the_badge" alt="Dynamic Darkness mod downloads counter badge">
    </a>
    <br>
    <a title="IFBHFix mod" href="https://www.curseforge.com/minecraft/mc-mods/ifbhfix">
        <img src="https://cf.way2muchnoise.eu/full_446961_IFBHFix%20%7C_downloads.svg?badge_style=for_the_badge" alt="IFBHFix mod downloads counter badge">
    </a>
    <br>
    <a title="NopRecipeBook mod" href="https://www.curseforge.com/minecraft/mc-mods/nop-recipe-book">
        <img src="https://cf.way2muchnoise.eu/full_829142_NopRecipeBook%20%7C_downloads.svg?badge_style=for_the_badge" alt="NopRecipeBook mod downloads counter badge">
    </a>
</p>

## The Idea

Common build script tweaks are Gradle plugins in [buildSrc](buildSrc). Each individual project decides which tweaks to apply as well as which third-party plugins versions to use. Plugins in buildSrc must not depend on any third-party plugin, but may provide optional integration (e.g. [reproducible-builds](buildSrc/src/main/kotlin/io/github/yamporg/gradle/ReproducibleBuildsPlugin.kt) plugin fixes non-determinism in [ForgeGradle](https://github.com/MinecraftForge/ForgeGradle) tasks).

Currently, this project contains only [Forge](https://github.com/MinecraftForge/MinecraftForge) mods, but it should be possible to have [Fabric](https://fabricmc.net) mods alongside.

## Getting Started

### Prerequisites

It’s recommended to use the latest [IntelliJ IDEA](https://jetbrains.com/idea) release with [Minecraft Dev for IntelliJ](https://minecraftdev.org) plugin. [Gradle](https://gradle.org) distribution specified in [gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties) will be [automatically downloaded on project import](https://jetbrains.com/help/idea/gradle.html#604e9f91). Note that you don’t have to install a specific JDK version, [Gradle handles that for you](https://blog.gradle.org/java-toolchains)!

### Building

To start with the project, you can either import it with IntelliJ IDEA or simply build from the command line.
```
gradle build
```
That would output jar archives to `<project-name>/build/libs` directory.

### Running

Each subproject defines either runClient or runServer tasks, or both, if it makes sense. To generate run/debug configurations, execute genIntellijRuns task. You may need to reopen the project and/or change SDK version in the generated configuration afterwards.
```
gradle genIntellijRuns
```

Alternatively, just run the Gradle task without generating IDE configuration.
```
gradle :darkness-forge-1.12.x:runClient
```

## Releasing

The CI runs upload task on tag push. The tag must follow the `<project>/v<semver>` format where `<project>` is the name of the project (e.g. `darkness-forge-1.12.x`) and `<semver>` is the [semantic version](https://semver.org).  
For example, to publish release v0.4.0-beta of darkness-forge-1.12.x, run the following:
```
git tag darkness-forge-1.12.x/v0.4.0-beta
git push --tags
```

## Known Issues

This section discusses known issues that may occur during development process.

### runClient crashes with NPE at newChannel

There seems to be an issue with interactions between ForgeGradle and Minecraft 1.12.x where the required libraries are missing when runClient task is called outside of IDE with clean cache. The workaround is to bootstrap the cache using an IDE.

See https://github.com/MinecraftForge/ForgeGradle/issues/750

### Resources are missing in runClient

For some reason resources are not loaded with Minecraft 1.12.x in runClient task unless we put them to classes directory directly. Newer version of Minecraft work just fine though. We used to have a workaround based on [build.gradle from Chiseled Me mod](https://github.com/necauqua/chiseled-me/blob/f8b94ab08ff39c46c58120027de4361597a1a473/build.gradle#L30-L39), but removed it to avoid maintenance burden.

See https://github.com/MinecraftForge/ForgeGradle/issues/717 and https://redd.it/e4hfzz

### 503’s from CurseForge (or is it Cloudflare?)

If you are getting HTTP 503’s errors for CurseForge API, set up a proxy. E.g. something like the command below is good enough for local testing.
```
CURSEFORGE_TOKEN=xxx gradle :darkness-forge-1.12.x:curseforge -D'org.gradle.jvmargs=-DsocksProxyHost=45.79.207.110 -DsocksProxyPort=9200'
```
Also, I have no freaking idea why is that happening. I’ve tried connecting from two distinct local ISPs and that didn’t help.
