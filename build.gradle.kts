import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    alias(libs.plugins.intellij.platform)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.compose.desktop)
    alias(libs.plugins.compose.compiler)
}

fun environment(key: String) = providers.environmentVariable(key)

group = "by.overpass"
version = properties["version"].toString()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven(url = "https://jitpack.io")
    maven(url = "https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    intellijPlatform {
        create(properties["platform-type"].toString(), properties["platform-version"].toString())
        bundledPlugins(
            "com.intellij.java",
            "org.jetbrains.kotlin",
        )
        jetbrainsRuntime()
        pluginVerifier()
        zipSigner()
        instrumentationTools()
        testFramework(TestFrameworkType.Platform)
    }
    implementation(libs.svg.to.compose) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "xerces", module = "xercesImpl")
        exclude(group = "xml-apis", module = "xml-apis")
    }
    listOf(
        compose.desktop.macos_x64,
        compose.desktop.macos_arm64,
        compose.desktop.windows_x64,
        compose.desktop.linux_x64,
        compose.desktop.linux_arm64,
    ).forEach { artifact ->
        implementation(artifact) {
            exclude(group = "org.jetbrains.kotlinx")
            exclude(group = "org.jetbrains.compose.material")
        }
    }
    implementation(libs.jewel.ide.laf.bridge.get241()) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation(libs.compose.multiplatform.file.picker) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    detektPlugins(libs.detekt.compose.rules)
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}

intellijPlatform {
    buildSearchableOptions = true
    pluginConfiguration {
        id = properties["plugin-id"].toString()
        name = properties["plugin-name"].toString()
        description = """
            <p>Generate Jetpack Compose Vector Icons from SVG files in Intellij IDEA and preview them</p>
            <p>This plugin is a wrapper for the <a href="https://github.com/DevSrSouza/svg-to-compose">svg-to-compose tool</a></p>
            <p>Use cases:</p>
            <ul>
                <li>Manipulate dynamic an SVG file in code, you can generate and do source code modifications</li>
                <li>Create an Icon pack similar to how Material Icons works on Compose</li>
                <li>Preview the generated ImageVector icons
            </ul>
        """.trimIndent()
        changeNotes = properties["change-notes"].toString()
        vendor {
            name = properties["vendor-name"].toString()
            email = properties["vendor-email"].toString()
        }
        ideaVersion {
            sinceBuild = properties["since-build"].toString()
            untilBuild = properties["until-build"].toString()
        }
    }
    publishing {
        token = environment("PUBLISH_TOKEN")
    }
    signing {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
}

kotlin {
    jvmToolchain(properties["jvm-version"].toString().toInt())
}

compose.desktop {
    application {
        nativeDistributions {
            linux {
                modules("jdk.security.auth")
            }
        }
    }
}

tasks {
    runIde {
        systemProperties["org.jetbrains.jewel.debug"] = "true"
    }
}
