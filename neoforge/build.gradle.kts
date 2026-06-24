plugins {
    java
    id("net.neoforged.gradle.userdev") version "7.1.25"
    id("com.gradleup.shadow") version "9.4.1"
}

// Custom configuration for libraries that should be bundled into the shadow jar.
// This prevents Shadow from pulling in the entire runtime classpath (Minecraft, NeoForge, MixinExtras, etc.)
val shade: Configuration by configurations.creating

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.neoforged:neoforge:26.1.2.76")
    implementation(project(":common"))

    // YAML configuration
    implementation("org.yaml:snakeyaml:2.3")

    // Libraries to bundle into the mod jar
    shade(project(":common"))
    shade("org.yaml:snakeyaml:2.3")

    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("DiscordCraft-NeoForge-${project.version}.jar")

        // Only bundle the 'shade' configuration, not the full runtime classpath
        configurations = listOf(shade)

        // Enable zip64 for large archives
        isZip64 = true

        // Relocate bundled libraries to avoid conflicts with other mods
        relocate("net.dv8tion.jda", "com.pablobh.discordcraft.libs.jda")
        relocate("org.slf4j", "com.pablobh.discordcraft.libs.slf4j")
        relocate("club.minnced.discord.webhook", "com.pablobh.discordcraft.libs.webhook")
        relocate("org.yaml.snakeyaml", "com.pablobh.discordcraft.libs.snakeyaml")

        // Preserve service files for bundled libraries
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }
    }

    // Deploy to local test server
    register<Copy>("deployMod") {
        dependsOn(shadowJar)
        from(shadowJar.get().archiveFile)
        into(file("server/mods"))
        doLast {
            println("Mod deployed to server/mods/")
        }
    }

    // Build and deploy in one command
    register("dev") {
        dependsOn("deployMod")
        group = "development"
        description = "Build and deploy NeoForge mod to test server"
    }
}
