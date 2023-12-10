plugins {
    id("java")
    alias(libs.plugins.intellij)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.compose.desktop)
}

fun environment(key: String) = providers.environmentVariable(key)

group = "by.overpass"
version = "0.13"

repositories {
    mavenCentral()
    maven(url = "https://maven.google.com")
    maven(url = "https://jitpack.io")
    maven(url = "https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.svg.to.compose) {
        exclude(group = "xerces", module = "xercesImpl")
        exclude(group = "xml-apis", module = "xml-apis")
    }
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation(libs.jewel.int.ui.standalone)
    implementation(libs.jewel.ide.laf.bridge)
    implementation(libs.jewel.ide.laf.bridge.platform.specific)
    implementation(libs.jewel.compose.utils)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
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
