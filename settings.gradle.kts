pluginManagement {
    repositories {
        mavenCentral()

        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent {
                snapshotsOnly()
            }
        }

        maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
            mavenContent {
                snapshotsOnly()
            }
        }

        gradlePluginPortal()
    }
}

rootProject.name = "ffmpeg-cli-wrapper"

plugins {
    id("com.gradle.develocity") version "4.1.1"
}

develocity {
    val isCI = System.getenv("CI") == "true"

    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        publishing.onlyIf { isCI }
        if(isCI) {
            termsOfUseAgree = "yes"
        }
    }
}
