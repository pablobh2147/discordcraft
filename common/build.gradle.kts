plugins {
    `java-library`
}

dependencies {
    // Discord API and webhooks (shared across all platforms)
    api("net.dv8tion:JDA:5.0.0") {
        exclude(module = "opus-java")
    }
    api("club.minnced:discord-webhooks:0.8.4")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}
