plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.pablobh"
version = "1.0"
description = "Discord integration plugin for Minecraft"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Spigot API - 1.20.1 (stable, supports 1.13+ via api-version)
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    
    // JDA - Discord library (bundled in final JAR)
    implementation("net.dv8tion:JDA:5.0.0") {
        exclude(module = "opus-java")
    }
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("${project.name}-${project.version}.jar")
        
        // Relocate JDA to avoid conflicts with other plugins
        relocate("net.dv8tion.jda", "com.pablobh.discordcraft.libs.jda")
        relocate("org.slf4j", "com.pablobh.discordcraft.libs.slf4j")
        
        minimize {
            exclude(dependency("net.dv8tion:JDA:.*"))
        }
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    
    // Deploy to local test server
    register<Copy>("deployPlugin") {
        dependsOn(shadowJar)
        from(shadowJar.get().archiveFile)
        into(file("server/plugins"))
        doLast {
            println("Plugin deployed to server/plugins/")
        }
    }
    
    // Build and deploy in one command
    register("dev") {
        dependsOn("deployPlugin")
        group = "development"
        description = "Build and deploy plugin to test server"
    }
}
