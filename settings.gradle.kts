rootProject.name = "ffmpeg-cli-wrapper"

plugins {
    id("com.gradle.develocity") version "4.0.1"
}

val isCI = System.getenv("CI") == "true"

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        publishing.onlyIf { isCI }
        if(isCI) {
            termsOfUseAgree = "yes"
        }
    }
}
