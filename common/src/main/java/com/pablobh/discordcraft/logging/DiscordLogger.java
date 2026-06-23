package com.pablobh.discordcraft.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.message.Message;

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

    @Nullable
    public TextChannel getChannel() {
        return channel;
    }

    private void sendMessage(@Nonnull String message) {
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    public void sendMessage(@Nonnull Message message) {
        if (channel != null) {
            channel.sendMessage(message.toDiscordMessage()).queue();
        }
    }

    private void log(@Nonnull String prefix, @Nonnull String message) {
        sendMessage("[" + prefix + "]: " + message);
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
