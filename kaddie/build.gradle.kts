import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    id("maven-publish")
}

group = "com.bitsycore"
version = "1.0.1"

val javaVersion: JavaVersion by rootProject.extra

android {
    namespace = "com.bitsycore.lib.kaddie"
    compileSdk = rootProject.extra["compileSdk"] as Int

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int
    }
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyHierarchyTemplate(KotlinHierarchyTemplate.default)

    // ===================================
    // Jvm
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
        }
    }

    // ===================================
    // Android
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
        }
        publishLibraryVariants("release")
    }

    // ===================================
    // Apple Native
    val xcf = XCFramework("kaddie")
    listOf(
        macosArm64(),
        macosX64(),
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "kaddie"
            isStatic = true
            xcf.add(this)
        }
    }

    // ===================================
    // Other Native
    listOf(
        mingwX64(),
        linuxX64(),
        linuxArm64()
    ).forEach {
        it.binaries {
            sharedLib()
            staticLib()
        }
    }

    // ===================================
    // Dependencies
    sourceSets {
        val jvmAndAndroidMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain {
            dependsOn(jvmAndAndroidMain)
        }
        jvmMain {
            dependsOn(jvmAndAndroidMain)
        }
        commonMain.dependencies {
            implementation(kotlin("reflect"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bitsycore/Kaddie")
            credentials {
                username = "token"
                password = ""
            }
        }
    }
}