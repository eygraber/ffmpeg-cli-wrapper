import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  jacoco
  alias(libs.plugins.abiCompat)
  alias(libs.plugins.detekt)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.maven.publish)
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

  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)

  compileOnly(libs.errorprone.annotations)
  compileOnly(libs.findbugs.annotations)
  compileOnly(libs.spotbugs.annotations)
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions.jvmTarget = JvmTarget.JVM_1_8
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

detekt {
  source.from("build.gradle.kts")

  autoCorrect = true
  parallel = true

  buildUponDefaultConfig = true

  config.from(rootProject.file("detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
  jvmTarget = JvmTarget.JVM_1_8.target

  val projectDir = projectDir
  val buildDir = project.layout.buildDirectory.asFile.get()

  exclude {
    it.file.relativeTo(projectDir).startsWith(buildDir.relativeTo(projectDir))
  }
}

jacoco {
  toolVersion = "0.8.14"
}

tasks.jacocoTestReport {
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
  dependsOn(tasks.test)
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()
}
