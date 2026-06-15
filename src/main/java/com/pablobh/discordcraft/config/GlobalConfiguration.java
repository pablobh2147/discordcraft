package com.pablobh.discordcraft.config;

import com.pablobh.discordcraft.ConfigManager;
import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.avatar.AvatarStyle;

public class GlobalConfiguration {

    private static final String KEY_AVATAR_STYLE = "avatar-style";
    private static final AvatarStyle DEFAULT_AVATAR_STYLE = AvatarStyle.BUST;

    private final ConfigManager configManager;

    private AvatarStyle avatarStyle;

    public GlobalConfiguration(ConfigManager configManager) {
        this.configManager = configManager;
        load();
    }

    private void load() {
        avatarStyle = parseAvatarStyle(configManager.getConfig().getString(KEY_AVATAR_STYLE));
    }

    public void reload() {
        configManager.reloadConfig();
        load();
    }

    public void save() {
        configManager.getConfig().set(KEY_AVATAR_STYLE, avatarStyle.name().toLowerCase());
        
        configManager.saveConfig();
    }

    // --------------------- Getters ---------------------

    public AvatarStyle getAvatarStyle() {
        return avatarStyle;
    }

    // --------------------- Setters ---------------------

    public void setAvatarStyle(AvatarStyle avatarStyle) {
        this.avatarStyle = avatarStyle;
    }

    // --------------------- Config manager access ---------------------

    public ConfigManager getConfigManager() {
        return configManager;
    }

    // --------------------- Parsing helpers ---------------------

    private AvatarStyle parseAvatarStyle(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_AVATAR_STYLE;
        }

        try {
            return AvatarStyle.valueOf(value.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            DiscordCraft.logWarning("Invalid avatar-style '" + value + "' in config.yml, defaulting to " + DEFAULT_AVATAR_STYLE.name().toLowerCase());
            return DEFAULT_AVATAR_STYLE;
        }
    }

}
