plugins {
    id("com.diffplug.spotless") version "5.10.2"
}

spotless {
    isEnforceCheck = false
    java {
        target("*/src/*/java/**/*.java")
        googleJavaFormat("1.9").aosp()
    }
    kotlin {
        target("*/src/*/kotlin/**/*.kt")
        ktlint("0.40.0")
    }
    kotlinGradle {
        target("**/*.kts")
        ktlint("0.40.0")
    }
}
