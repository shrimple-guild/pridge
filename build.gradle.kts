import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    kotlin("jvm")
    id("fabric-loom")
    id("com.gradleup.shadow") version "9.3.1"
}

group = property("maven_group")!!
version = "${property("mod_version")}+${stonecutter.current.version}"

base.archivesName = property("mod_id") as String

val requiredJava = when {
    stonecutter.eval(stonecutter.current.version, ">=1.20.6") -> JavaVersion.VERSION_21
    stonecutter.eval(stonecutter.current.version, ">=1.18") -> JavaVersion.VERSION_17
    stonecutter.eval(stonecutter.current.version, ">=1.17") -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}


repositories {
    maven("https://maven.notenoughupdates.org/releases/") {
        name = "not-enough-updates"
    }

    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
        name = "DevAuth"
    }
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    
    val aw = file("src/main/resources/pridge.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

val shadowModImpl by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:${stonecutter.current.version}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fapi_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    shadowModImpl("org.notenoughupdates.moulconfig:${property("moulconfig_version")}")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

kotlin {
    jvmToolchain(requiredJava.majorVersion.toInt())

    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

val remapJar by tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("libs/badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "io.github.ricciow.moulconfig")
    mergeServiceFiles()
}

tasks.jar {
    archiveClassifier.set("nodeps")
    destinationDirectory.set(layout.buildDirectory.dir("libs/badjars"))
}

tasks.assemble.get().dependsOn(tasks.remapJar)

tasks.processResources {
    val mcVersion = stonecutter.current.version

    inputs.property("minecraft", mcVersion)
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        val expansionProps = project.properties + mapOf(
            "minecraft" to mcVersion,
            "version" to project.version
        )

        expand(expansionProps)
    }
}