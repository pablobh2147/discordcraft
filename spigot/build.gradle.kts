plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    implementation(project(":common"))

    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("DiscordCraft-Spigot-${project.version}.jar")

        // Relocate bundled libraries to avoid conflicts with other plugins/mods
        relocate("net.dv8tion.jda", "com.pablobh.discordcraft.libs.jda")
        relocate("org.slf4j", "com.pablobh.discordcraft.libs.slf4j")
        relocate("club.minnced.discord.webhook", "com.pablobh.discordcraft.libs.webhook")

        minimize {
            exclude(dependency("net.dv8tion:JDA:.*"))
            exclude(dependency("club.minnced:discord-webhooks:.*"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to project.version)
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
        description = "Build and deploy Spigot plugin to test server"
    }
}
