import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.detektPlugins
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.abiCompat)
  alias(libs.plugins.detekt)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.kover)
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
  implementation(libs.guava)
  implementation(libs.jsonSugar)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.modelmapper)
  implementation(libs.slf4j.api)

  testImplementation(libs.grizzly.http.server)
  testImplementation(libs.hamcrest)
  testImplementation(libs.hamcrest.integration)
  testImplementation(libs.junit)
  testImplementation(libs.logback.classic)
  testImplementation(libs.mockk)
  testImplementation(libs.nitorcreations.matchers)

  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)
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

kover {
  reports {
    filters {
      excludes {
        classes(
          "*\$Companion", // Companion objects
          "*\$Companion\$*", // Nested companion object classes
          "*\$*serializer", // kotlinx.serialization generated serializers
          "*\$\$serializer", // kotlinx.serialization generated serializers
        )
      }
    }
  }
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()
}
