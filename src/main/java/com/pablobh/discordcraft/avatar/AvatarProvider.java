package com.pablobh.discordcraft.avatar;

import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class AvatarProvider {

    private static final String BASE_URL = "https://render.crafty.gg/";
    private static final String DEFAULT_AVATAR_HANDLE = "MHF_Stevee";

    public static final int MIN_AVATAR_SIZE = 16;
    public static final int MAX_AVATAR_SIZE = 256;

    private String getPlayerAvatarHandle(Player player) {
        return player.getUniqueId().toString();
    }

    private String getAvatarPath(AvatarStyle style) {
        switch (style) {
            case FRONT:
                return "2d/front";
            case FRONT_FULL:
                return "2d/frontfull";
            case BODY:
                return "3d/full";
            case BUST:
                return "3d/bust";
            case FACE:
            default:
                return "2d/head";
        }
    }
   
    public URL getAvatarUrl(UUID uuid, AvatarStyle style, int size) {
        return getAvatarUrl(uuid.toString(), style, size);
    }

    public URL getAvatarUrl(Player player, AvatarStyle style, int size) {
        String handle = getPlayerAvatarHandle(player);
        return getAvatarUrl(handle, style, size);
    }

    private URL getAvatarUrl(String handle, AvatarStyle style, int size) {
        if (size < MIN_AVATAR_SIZE || size > MAX_AVATAR_SIZE) {
            throw new IllegalArgumentException("Avatar size must be between " + MIN_AVATAR_SIZE + " and " + MAX_AVATAR_SIZE);
        }

        String avatarPath = getAvatarPath(style);

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(BASE_URL);
        urlBuilder.append(avatarPath).append("/");
        urlBuilder.append(handle);
        urlBuilder.append("?size=").append(size);
        urlBuilder.append("&width=").append(size);
        urlBuilder.append("&height=").append(size);

        try {
            return URI.create(urlBuilder.toString()).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to construct avatar URL", e);
        }
    }
}
