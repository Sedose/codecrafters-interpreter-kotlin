plugins {
  kotlin("jvm") version "2.0.0"
  application
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.codecrafters"
version = "1.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("io.insert-koin:koin-core:3.5.6")
  implementation("io.insert-koin:koin-core-jvm:3.5.6")

  testImplementation("io.kotest:kotest-runner-junit5-jvm:6.0.0.M2")
  testImplementation("io.kotest:kotest-assertions-core-jvm:6.0.0.M2")
  testImplementation("io.kotest:kotest-property:6.0.0.M2")
  testImplementation("io.mockk:mockk:1.13.17")
}

tasks.named("distZip") {
  dependsOn(tasks.named("shadowJar"))
}

tasks.named("distTar") {
  dependsOn(tasks.named("shadowJar"))
}

tasks.named("startScripts") {
  dependsOn(tasks.named("shadowJar"))
}

tasks.named("startShadowScripts") {
  dependsOn(tasks.named("jar"))
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
  archiveClassifier.set("")
}

tasks {
  shadowJar {
    archiveClassifier.set("")
  }
  test {
    useJUnitPlatform()
  }
}

application {
  mainClass.set("io.codecrafters.MainKt")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}
