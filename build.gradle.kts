import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    jacoco
    id("com.github.spotbugs") version "6.0.19"
    id("net.ltgt.errorprone") version "4.2.0"
    id("com.diffplug.spotless") version "6.25.0"
    id("com.vanniktech.maven.publish") version "0.32.0"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.13")
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.6")
    compileOnly("com.google.errorprone:error_prone_annotations:2.25.0")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.commons:commons-lang3:3.16.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.modelmapper:modelmapper:3.2.1")
    compileOnly("com.google.code.findbugs:annotations:3.0.1")

    testImplementation("ch.qos.logback:logback-classic:1.3.14")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.hamcrest:hamcrest-integration:1.3")
    testImplementation("com.nitorcreations:matchers:1.3")
    testImplementation("org.glassfish.grizzly:grizzly-http-server:3.0.1")

    errorprone("com.google.errorprone:error_prone_core:2.29.2")
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
