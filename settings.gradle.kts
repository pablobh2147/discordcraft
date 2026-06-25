rootProject.name = "DiscordCraft"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
    }
}

include("common")
include("spigot")
include("neoforge")
