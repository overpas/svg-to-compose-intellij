import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
}

val jvmVersion = providers.gradleProperty("jvm-version").get()

java {
    sourceCompatibility = JavaVersion.toVersion(jvmVersion)
    targetCompatibility = JavaVersion.toVersion(jvmVersion)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jvmVersion)
    }
}
