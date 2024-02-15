plugins {
    id("java")
    alias(libs.plugins.intellij)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.compose.desktop)
}

fun environment(key: String) = providers.environmentVariable(key)

group = "by.overpass"
version = properties["version"].toString()

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://androidx.dev/storage/compose-compiler/repository/")
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    maven(url = "https://maven.google.com")
    maven(url = "https://jitpack.io")
    maven(url = "https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    implementation(libs.svg.to.compose) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "xerces", module = "xercesImpl")
        exclude(group = "xml-apis", module = "xml-apis")
    }
    implementation(compose.desktop.macos_x64) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(compose.desktop.macos_arm64) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(compose.desktop.windows_x64) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(compose.desktop.linux_x64) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(compose.desktop.linux_arm64) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(libs.jewel.ide.laf.bridge.get232()) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation(libs.compose.multiplatform.file.picker) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set(properties["since-build"].toString())
    plugins.set(listOf("java", "Kotlin"))
}

kotlin {
    jvmToolchain(properties["jvm-version"].toString().toInt())
}

tasks {
    signPlugin {
        certificateChain.set(environment("CERTIFICATE_CHAIN"))
        privateKey.set(environment("PRIVATE_KEY"))
        password.set(environment("PRIVATE_KEY_PASSWORD"))
    }
    publishPlugin {
        token.set(environment("PUBLISH_TOKEN"))
    }
    patchPluginXml {
        sinceBuild.set(project.properties["since-build"].toString())
        untilBuild.set(project.properties["until-build"].toString())
        changeNotes.set(project.properties["change-notes"].toString())
    }
    test {
        useJUnit()
    }
    runIde {
        systemProperties["org.jetbrains.jewel.debug"] = "true"
    }
}
