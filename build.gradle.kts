import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    jacoco
    alias(libs.plugins.errorprone)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(libs.commons.io)
    implementation(libs.commons.lang3)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.modelmapper)
    implementation(libs.slf4j.api)

    testImplementation(libs.grizzly.http.server)
    testImplementation(libs.hamcrest)
    testImplementation(libs.hamcrest.integration)
    testImplementation(libs.junit)
    testImplementation(libs.logback.classic)
    testImplementation(libs.mockito.core)
    testImplementation(libs.nitorcreations.matchers)

    compileOnly(libs.errorprone.annotations)
    errorprone(libs.errorprone.core)
    compileOnly(libs.findbugs.annotations)
    compileOnly(libs.spotbugs.annotations)
}

tasks.withType<Test> {
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-options"))
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    dependsOn(tasks.test)
}

spotless {
    java {
        googleJavaFormat()
    }
}

spotbugs {
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.LOW)
    includeFilter.set(layout.projectDirectory.file("spotbugs-include.xml"))
    excludeFilter.set(layout.projectDirectory.file("spotbugs-exclude.xml"))
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}
