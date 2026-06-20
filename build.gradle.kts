plugins {
    base
}

group = "com.pablobh"
version = "1.0.0"
description = "Discord integration plugin for Minecraft"

allprojects {
    group = "com.pablobh"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
