plugins {
    base
}

group = "com.pablobh"
version = "2.0.0"
description = "Discord integration plugin and mod for Minecraft"

allprojects {
    group = "com.pablobh"
    version = "2.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

// Convenience tasks for the most common workflow
tasks.register("dev") {
    dependsOn(":spigot:dev")
    group = "development"
    description = "Build and deploy the Spigot plugin to the test server"
}

tasks.register("devNeoForge") {
    dependsOn(":neoforge:dev")
    group = "development"
    description = "Build and deploy the NeoForge mod to the test server"
}
