rootProject.name = "pridge"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }

    val loom_version: String by settings
    val kotlin_version: String by settings

    plugins {
        id("fabric-loom") version loom_version
        id("org.jetbrains.kotlin.jvm") version kotlin_version
    }

}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.11"
}

stonecutter {
    create(rootProject) {
        versions("1.21.10", "1.21.11")
        vcsVersion = "1.21.10"
    }
}
