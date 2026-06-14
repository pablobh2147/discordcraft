package com.electrodiux.discordcraft;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.ChatColor;

public class Messages {

    // Static loader

    private static ConfigManager messagesConfig;

    static void setup() {
        messagesConfig = DiscordCraft.instance().getMessagesConfigManager();
    }

    public static ConfigManager getConfiguration() {
        return messagesConfig;
    }

    // Messages

    @Nullable
    public static String getMessage(@Nonnull ConfigurationSection config, @Nonnull String key, @Nullable String defaultMessage, Object... args) {
        Objects.requireNonNull(key, "The message key cannot be null");
        Objects.requireNonNull(config, "The messages config is null");

        String message = config.getString(key);

        if (message == null) {
            if (defaultMessage == null) {
                return null;
            }
            message = defaultMessage;
        }

        if (args.length > 0) {
            message = replaceVariables(message, args);
        }

        return message;
    }

    @Nullable
    public static String getMessageWithDefault(@Nonnull String key, @Nullable String defaultMessage, Object... args) {
        Objects.requireNonNull(messagesConfig, "The messages config is null, Messages has been initialized?");
        return getMessage(messagesConfig.getConfig(), key, defaultMessage, args);
    }

    @Nonnull
    public static String getMessage(@Nonnull String key, Object... args) {
        Objects.requireNonNull(messagesConfig, "The messages config is null, Messages has been initialized?");
        return getMessage(messagesConfig.getConfig(), key, key, args);
    }

    // Variables

    @Nonnull
    public static String replaceVariables(@Nonnull String message, Object... args) {
        for (int i = 0; i < args.length; i += 2) {
            Object varName = args[i];
            Object varValue = args[i + 1];

            if (varName == null || varValue == null) continue;

            message = variable(message, String.valueOf(varName), varValue);
        }
        return message;
    }

    @Nonnull
    public static String variablePlayer(@Nonnull String message, @Nullable String varName, @Nullable Player player) {
        if (player == null) return message;

        String name = player.getName();
        String displayName = player.getDisplayName();
        String uuid = player.getUniqueId().toString();
        String ip = player.getAddress().getAddress().getHostAddress();

        message = message.replace('%' + varName + "_name" + '%', name);
        message = message.replace('%' + varName + "_displayname" + '%', displayName);
        message = message.replace('%' + varName + "_uuid" + '%', uuid);
        message = message.replace('%' + varName + "_ip" + '%', ip);
        return message;
    }

    @Nonnull
    public static String variableOfflinePlayer(@Nonnull String message, @Nullable String varName, @Nonnull OfflinePlayer player) {
        if (player == null) return message;

        String name = player.getName();
        String uuid = player.getUniqueId().toString();

        message = message.replace('%' + varName + "_name" + '%', name);
        message = message.replace('%' + varName + "_uuid" + '%', uuid);
        return message;
    }

    // Discord Member Variable

    @Nonnull
    public static String variableDiscordMember(@Nonnull String message, @Nonnull String varName, @Nonnull Member member) {
        if (member == null) return message;

        String id = member.getId();
        String name = member.getEffectiveName();
        String nickname = member.getNickname();
        if (nickname == null) nickname = name;
        String mention = member.getAsMention();
        
        message = message.replace('%' + varName + "_id" + '%', id);
        message = message.replace('%' + varName + "_name" + '%', name);
        message = message.replace('%' + varName + "_nickname" + '%', nickname);
        message = message.replace('%' + varName + "_mention" + '%', mention);
        return message;
    }

    @Nonnull
    public static String variable(@Nonnull String message, @Nonnull String varName, @Nonnull Object varValue) {
        if (varValue == null) return message;

        if (varValue instanceof Player) {
            return variablePlayer(message, varName, (Player) varValue);
        }

        if (varValue instanceof OfflinePlayer) {
            return variableOfflinePlayer(message, varName, (OfflinePlayer) varValue);
        }

        if (varValue instanceof Member) {
            return variableDiscordMember(message, varName, (Member) varValue);
        }

        return message.replace('%' + varName + '%', String.valueOf(varValue));
    }

    // Minecraft Colors

    @Nonnull
    public static String applyMinecraftColorFormatting(@Nonnull String message) {
        Objects.requireNonNull(message, "The message cannot be null");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
