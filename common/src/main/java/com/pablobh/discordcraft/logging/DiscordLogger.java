package com.pablobh.discordcraft.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordLogger {

    @Nullable
    private TextChannel channel;

    public DiscordLogger(@Nullable TextChannel channel) {
        this.channel = channel;
    }

    public void setChannel(@Nullable TextChannel channel) {
        this.channel = channel;
    }

    private void log(@Nonnull String message) {
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    private void log(@Nonnull String prefix, @Nonnull String message) {
        log("[" + prefix + "]: " + message);
    }
   
    public void info(@Nonnull String message) {
        log("INFO", message);
    }

    public void warning(@Nonnull String message) {
        log("WARNING", message);
    }

    public void severe(@Nonnull String message) {
        log("ERROR", message);
    }

    public void exception(@Nonnull Exception e, @Nullable String message) {
        if (message == null || message.isBlank()) {
            message = "An internal error occurred!";
        }
        log("ERROR", message + " (Check Console for more Details).");
    }

}
