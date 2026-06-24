package com.pablobh.discordcraft.avatar;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class AvatarProvider {

    private static final String BASE_URL = "https://render.crafty.gg/";

    private String getAvatarPath(AvatarStyle style) {
        switch (style) {
            case BODY:
                return "3d/full";
            case FACE:
                return "2d/head";
            case BUST:
            default:
                return "3d/bust";
        }
    }
   
    public URL getAvatarUrl(UUID uuid, AvatarStyle style, int size) {
        return getAvatarUrl(uuid.toString(), style, size);
    }

    private URL getAvatarUrl(String handle, AvatarStyle style, int size) {
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
