package com.pablobh.discordcraft.spigot.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;

import com.pablobh.discordcraft.StringUtils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public abstract class Message {

    public static final char ALTERNATE_COLOR_CHAR = '&';
    public static final char INITIAL_PLACEHOLDER_CHAR = '%';
    public static final char FINAL_PLACEHOLDER_CHAR = '%';

    @Override
    public abstract String toString();
    
    @Nonnull
    public abstract MessageCreateData toDiscordMessage();

    public abstract Message formatMinecraftColors();
    public abstract Message replace(@Nonnull String key, @Nullable String value);    

    public Message replace(@Nonnull String key, @Nullable Player player) {
        String name =  null;
        String displayName = null;
        String uuid = null;
        String ip = null;

        if (player != null) {
            name = player.getName();
            displayName = player.getDisplayName();
            uuid = player.getUniqueId().toString();
            ip = player.getAddress().getAddress().getHostAddress();
        }

        replace(key + "_name", name);
        replace(key + "_displayname", displayName);
        replace(key + "_uuid", uuid);
        replace(key + "_ip", ip);
        
        return this;
    }

    public Message replace(@Nonnull String key, @Nullable OfflinePlayer player) {
        String name = null;
        String uuid = null;
        
        if (player != null) {
            name = player.getName();
            uuid = player.getUniqueId().toString();
        }
        
        replace(key + "_name", name);
        replace(key + "_uuid", uuid);
        
        return this;
    }

    public Message replace(@Nonnull String key, @Nullable Member member) {
        String id = null;
        String name = null;
        String nickname = null;
        String mention = null;

        if (member != null) {
            id = member.getId();
            name = member.getEffectiveName();
            nickname = member.getNickname();
            if (nickname == null) nickname = name;
            mention = member.getAsMention();
        }

        replace(key + "_id", id);
        replace(key + "_name", name);
        replace(key + "_nickname", nickname);
        replace(key + "_mention", mention);

        return this;
    }

    public Message replace(@Nonnull String key, @Nullable AdvancementDisplay advancement) {
        String title = null;
        String description = null;
        String type = null;
        
        if (advancement != null) {
            title = advancement.getTitle();
            description = advancement.getDescription();
            type = StringUtils.capitalize(advancement.getType().name());
        }
        
        replace(key + "_title", title);
        replace(key + "_description", description);
        replace(key + "_type", type);
        
        return this;
    }

}
