package com.pablobh.discordcraft.spigot.message;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class DiscordMessage extends Message {

    private String content;
    private EmbedData embedData;

    public DiscordMessage(@Nonnull ConfigurationSection config) {
        if (config.isString("")) {
            this.content = config.getString("");
        } else {
            this.content = config.getString("content");
            
            if (config.isConfigurationSection("embed")) {
                ConfigurationSection embedSection = config.getConfigurationSection("embed");
                this.embedData = new EmbedData(embedSection);
            }
        }
    }

    @Override
    public Message formatMinecraftColors() {
        return this;
    }

    @Override
    public Message replace(@Nonnull String key, @Nullable String value) {
        if (value == null) {
            return this;
        }
        
        String placeholder = INITIAL_PLACEHOLDER_CHAR + key + FINAL_PLACEHOLDER_CHAR;
        
        if (content != null) {
            content = content.replace(placeholder, value);
        }
        
        if (embedData != null) {
            embedData.replace(placeholder, value);
        }
        
        return this;
    }

    @Override
    public String toString() {
        if (content != null) {
            return content;
        }
        if (embedData != null && embedData.title != null) {
            return embedData.title;
        }
        return "";
    }

    @Nonnull
    @Override
    public MessageCreateData toDiscordMessage() {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        
        if (content != null && !content.isEmpty()) {
            builder.setContent(content);
        }
        
        if (embedData != null) {
            builder.setEmbeds(embedData.toEmbed());
        }
        
        return builder.build();
    }

    private static class EmbedData {
        String title;
        String description;
        String color;
        AuthorData author;
        FooterData footer;
        String thumbnail;
        String image;
        List<FieldData> fields;

        EmbedData(@Nonnull ConfigurationSection config) {
            this.title = config.getString("title");
            this.description = config.getString("description");
            this.color = config.getString("color");
            this.thumbnail = config.getString("thumbnail");
            this.image = config.getString("image");

            if (config.isConfigurationSection("author")) {
                this.author = new AuthorData(config.getConfigurationSection("author"));
            }

            if (config.isConfigurationSection("footer")) {
                this.footer = new FooterData(config.getConfigurationSection("footer"));
            }

            if (config.isList("fields")) {
                this.fields = new ArrayList<>();
                List<Map<?, ?>> fieldsList = config.getMapList("fields");
                for (Map<?, ?> fieldMap : fieldsList) {
                    fields.add(new FieldData(fieldMap));
                }
            }
        }

        void replace(String placeholder, String value) {
            if (title != null) {
                title = title.replace(placeholder, value);
            }
            if (description != null) {
                description = description.replace(placeholder, value);
            }
            if (thumbnail != null) {
                thumbnail = thumbnail.replace(placeholder, value);
            }
            if (image != null) {
                image = image.replace(placeholder, value);
            }
            if (author != null) {
                author.replace(placeholder, value);
            }
            if (footer != null) {
                footer.replace(placeholder, value);
            }
            if (fields != null) {
                for (FieldData field : fields) {
                    field.replace(placeholder, value);
                }
            }
        }

        MessageEmbed toEmbed() {
            EmbedBuilder builder = new EmbedBuilder();

            if (title != null) {
                builder.setTitle(title);
            }
            if (description != null) {
                builder.setDescription(description);
            }
            if (color != null) {
                builder.setColor(parseColor(color));
            }
            if (thumbnail != null) {
                builder.setThumbnail(thumbnail);
            }
            if (image != null) {
                builder.setImage(image);
            }
            if (author != null) {
                builder.setAuthor(author.name, author.url, author.iconUrl);
            }
            if (footer != null) {
                builder.setFooter(footer.text, footer.iconUrl);
            }
            if (fields != null) {
                for (FieldData field : fields) {
                    builder.addField(field.name, field.value, field.inline);
                }
            }

            return builder.build();
        }

        private Color parseColor(String colorStr) {
            try {
                if (colorStr.startsWith("#")) {
                    return Color.decode(colorStr);
                }
                return Color.decode("#" + colorStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static class AuthorData {
        String name;
        String url;
        String iconUrl;

        AuthorData(@Nonnull ConfigurationSection config) {
            this.name = config.getString("name");
            this.url = config.getString("url");
            this.iconUrl = config.getString("icon_url");
        }

        void replace(String placeholder, String value) {
            if (name != null) {
                name = name.replace(placeholder, value);
            }
            if (url != null) {
                url = url.replace(placeholder, value);
            }
            if (iconUrl != null) {
                iconUrl = iconUrl.replace(placeholder, value);
            }
        }
    }

    private static class FooterData {
        String text;
        String iconUrl;

        FooterData(@Nonnull ConfigurationSection config) {
            this.text = config.getString("text");
            this.iconUrl = config.getString("icon_url");
        }

        void replace(String placeholder, String value) {
            if (text != null) {
                text = text.replace(placeholder, value);
            }
            if (iconUrl != null) {
                iconUrl = iconUrl.replace(placeholder, value);
            }
        }
    }

    private static class FieldData {
        String name;
        String value;
        boolean inline;

        FieldData(@Nonnull Map<?, ?> map) {
            Object nameObj = map.get("name");
            Object valueObj = map.get("value");
            Object inlineObj = map.get("inline");

            this.name = nameObj != null ? String.valueOf(nameObj) : null;
            this.value = valueObj != null ? String.valueOf(valueObj) : null;
            this.inline = inlineObj instanceof Boolean ? (Boolean) inlineObj : false;
        }

        void replace(String placeholder, String replaceValue) {
            if (name != null) {
                name = name.replace(placeholder, replaceValue);
            }
            if (value != null) {
                value = value.replace(placeholder, replaceValue);
            }
        }
    }
}
