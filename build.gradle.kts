plugins {
    id("com.diffplug.spotless") version "6.12.0"
}

repositories {
    mavenCentral()
}

spotless {
    java {
        target("*/src/*/java/**/*.java")
        googleJavaFormat("1.15.0").aosp()
    }

    // See https://github.com/diffplug/spotless/issues/142
    val editorConfig = mapOf(
        "ij_kotlin_allow_trailing_comma_on_call_site" to true,
        "ij_kotlin_allow_trailing_comma" to true,
    )
    kotlin {
        target("*/src/*/kotlin/**/*.kt")
        ktlint("0.47.1").editorConfigOverride(editorConfig)
    }

    kotlinGradle {
        target("**/*.kts")
        ktlint("0.47.1").editorConfigOverride(editorConfig)
    }

    // Do not run spotless as part of the check task (that runs before build task).
    isEnforceCheck = false
}
