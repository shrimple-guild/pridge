import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    kotlin("jvm")
    id("fabric-loom") apply false
    id("net.fabricmc.fabric-loom") apply false
    id("com.gradleup.shadow") version "9.3.1"
}

if (stonecutter.eval(stonecutter.current.version, ">=26.1")) {
    apply(plugin = "net.fabricmc.fabric-loom")
} else {
    apply(plugin = "fabric-loom")
}

stonecutter {
    properties.tags(current.version)
}

group = property("maven_group")!!
version = "${property("mod_version")}+${stonecutter.current.version}"

base.archivesName = property("mod_id") as String

val requiredJava = when {
    stonecutter.eval(stonecutter.current.version, ">=26.1") -> JavaVersion.VERSION_25
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

configure<net.fabricmc.loom.api.LoomGradleExtensionAPI> {
    fabricModJsonPath.set(rootProject.file("src/main/resources/fabric.mod.json"))
    
    val aw = project.file("src/main/resources/pridge.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    } else {
        val rootAw = rootProject.file("src/main/resources/pridge.accesswidener")
        if (rootAw.exists()) {
            accessWidenerPath.set(rootAw)
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

val isModern = stonecutter.eval(stonecutter.current.version, ">=26.1")
val modImpl = if (isModern) "implementation" else "modImplementation"

val shadowModImpl by configurations.creating {
    configurations.named(modImpl).get().extendsFrom(this)
}

dependencies {
    add("minecraft", "com.mojang:minecraft:${stonecutter.current.version}")
    
    if (stonecutter.eval(stonecutter.current.version, "<26.1")) {
        val loomExt = project.extensions.getByType<net.fabricmc.loom.api.LoomGradleExtensionAPI>()
        add("mappings", loomExt.officialMojangMappings())
    }

    val isModern = stonecutter.eval(stonecutter.current.version, ">=26.1")
    val modImpl = if (isModern) "implementation" else "modImplementation"
    val modRuntime = if (isModern) "runtimeOnly" else "modRuntimeOnly"

    add(modImpl, "net.fabricmc:fabric-loader:${property("loader_version")}")

    add(modImpl, "net.fabricmc.fabric-api:fabric-api:${property("fapi_version")}")

    add(modImpl, "net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    shadowModImpl("org.notenoughupdates.moulconfig:${property("moulconfig_version")}")

    add(modRuntime, "me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
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

if (stonecutter.eval(stonecutter.current.version, "<26.1")) {
    val remapJar by tasks.named<RemapJarTask>("remapJar") {
        archiveClassifier.set("")
        dependsOn(tasks.shadowJar)
        inputFile.set(tasks.shadowJar.get().archiveFile)
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
    tasks.assemble.get().dependsOn(remapJar)
} else {
    tasks.shadowJar {
        archiveClassifier.set("")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
    tasks.assemble.get().dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    if (stonecutter.eval(stonecutter.current.version, "<26.1")) {
        destinationDirectory.set(layout.buildDirectory.dir("libs/badjars"))
        archiveClassifier.set("all-dev")
    }
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "io.github.ricciow.moulconfig")
    mergeServiceFiles()
}

tasks.jar {
    archiveClassifier.set("nodeps")
    destinationDirectory.set(layout.buildDirectory.dir("libs/badjars"))
}

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