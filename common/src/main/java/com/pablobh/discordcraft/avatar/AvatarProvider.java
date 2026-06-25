/*
 * This file is part of DiscordCraft.
 *
 * Copyright (c) 2025 Pablo Bermejo Hernández
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
