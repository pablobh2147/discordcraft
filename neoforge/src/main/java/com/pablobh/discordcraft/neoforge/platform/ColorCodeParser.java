package com.pablobh.discordcraft.neoforge.platform;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import javax.annotation.Nonnull;

public final class ColorCodeParser {

    private static final char COLOR_CHAR = '&';
    private static final String VALID_CODES = "0123456789abcdefklmnorABCDEFKLMNOR";

    private ColorCodeParser() {}

    @Nonnull
    public static MutableComponent parse(@Nonnull String message) {
        MutableComponent result = Component.empty();
        StringBuilder current = new StringBuilder();
        Style style = Style.EMPTY;

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            if (c == COLOR_CHAR && i + 1 < message.length()) {
                char code = message.charAt(i + 1);
                if (VALID_CODES.indexOf(code) != -1) {
                    if (current.length() > 0) {
                        result = result.append(Component.literal(current.toString()).withStyle(style));
                        current.setLength(0);
                    }

                    ChatFormatting formatting = ChatFormatting.getByCode(Character.toLowerCase(code));
                    if (formatting != null) {
                        if (formatting == ChatFormatting.RESET) {
                            style = Style.EMPTY;
                        } else if (formatting.isColor()) {
                            style = Style.EMPTY.applyFormat(formatting);
                        } else {
                            style = style.applyFormat(formatting);
                        }
                    }

                    i++;
                    continue;
                }
            }

            current.append(c);
        }

        if (current.length() > 0) {
            result = result.append(Component.literal(current.toString()).withStyle(style));
        }

        return result;
    }
}
