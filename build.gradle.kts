import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm")
    id("fabric-loom")
    id("com.gradleup.shadow") version "9.3.1"
}

group = property("maven_group")!!
version = property("mod_version")!!

java.toolchain.languageVersion = JavaLanguageVersion.of(21)
kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_21)

repositories {
    maven("https://maven.notenoughupdates.org/releases/") {
        name = "not-enough-updates"
    }

    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
        name = "DevAuth"
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/pridge.accesswidener"))
}

val shadowModImpl by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")

    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Extra fabric api modules
    val apiModules = setOf(
        "fabric-resource-loader-v0",
        "fabric-lifecycle-events-v1",
        "fabric-events-interaction-v0",
        "fabric-command-api-v2",
        "fabric-registry-sync-v0",
        "fabric-rendering-v1",
        "fabric-message-api-v1"
    )

    apiModules.forEach {
        modImplementation(fabricApi.module(it, property("fapi_version").toString()))
    }

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    shadowModImpl("org.notenoughupdates.moulconfig:${property("moulconfig_version")}")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
}

tasks.shadowJar {
    // Make sure to relocate MoulConfig to avoid version clashes with other mods
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "io.github.ricciow.moulconfig")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }
}