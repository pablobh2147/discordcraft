package com.pablobh.discordcraft.spigot.config;

import javax.annotation.Nonnull;

import org.bukkit.plugin.java.JavaPlugin;

import com.pablobh.discordcraft.avatar.AvatarStyle;

public class GlobalConfiguration extends SpigotConfiguration {

    private static final String KEY_AVATAR_STYLE = "avatar-style";
    private static final AvatarStyle DEFAULT_AVATAR_STYLE = AvatarStyle.BUST;

    private AvatarStyle avatarStyle;

    public GlobalConfiguration(@Nonnull JavaPlugin plugin, @Nonnull String filename) {
        super(plugin, filename);
        loadConfiguration();
    }

    @Override
    public boolean reload() {
        boolean success = super.reload();
        loadConfiguration();
        return success;
    }
    
    private void loadConfiguration() {
        avatarStyle = getEnum(KEY_AVATAR_STYLE, AvatarStyle.class, DEFAULT_AVATAR_STYLE);
    }
    
    // --------------------- Getters ---------------------
    
    public AvatarStyle getAvatarStyle() {
        return avatarStyle;
    }
    
    // --------------------- Setters ---------------------
    
    public void setAvatarStyle(AvatarStyle avatarStyle) {
        this.avatarStyle = avatarStyle;
        set(KEY_AVATAR_STYLE, avatarStyle.name().toLowerCase());
    }

}
