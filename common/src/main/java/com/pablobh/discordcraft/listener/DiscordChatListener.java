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

package com.pablobh.discordcraft.listener;

import java.util.List;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.platform.component.ClickAction;
import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordChatListener extends ListenerAdapter {

    private final DiscordCraft discordCraft;

    public DiscordChatListener(DiscordCraft discordCraft) {
        this.discordCraft = discordCraft;
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        onMessage(event, event.getAuthor(), event.getMessage(), true);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        onMessage(event, event.getAuthor(), event.getMessage(), false);
    }

    // Message in minecraft chat from discord

    private void onMessage(GenericMessageEvent event, User author, Message message, boolean edited) {

        // Ignore private messages
        if (event.getChannelType() == ChannelType.PRIVATE) { 
            return;
        }

        // Ignore bot messages (to prevent webhooks from being displayed as messages)
        if (author.isBot()) { 
            return;
        }
        
        LinkedChannel linkedChannel = discordCraft.getDiscordService().getLinkedChannel(message.getChannel().asTextChannel());

        // Ignore messages from channels that are not linked
        if (linkedChannel == null || !linkedChannel.canSendDiscordMessages()) { 
            return;
        }

        // Ignore system messages if not allowed
        if (!linkedChannel.canSendDiscordSystemMessages() && author.isSystem()) { 
            return;
        }

        // Ignore messages from the bot itself
        if (author.getIdLong() == discordCraft.getDiscordService().getSelfUser().getIdLong()) { 
            return;
        }
        
        com.pablobh.discordcraft.message.Message rawMessage = discordCraft.getMessageService().getMessage(edited ? "chat.minecraft-edited-format" : "chat.minecraft-format");

        rawMessage.replace("username", author.getEffectiveName());
        rawMessage.replace("guild", message.getGuild().getName());
        rawMessage.replace("channel", message.getChannel().getName());
        rawMessage.replace("message", message.getContentDisplay());

        String[] parts = rawMessage.getContent().split("%attachments%", 2);

        MinecraftComponentBuilder builder = discordCraft.getServer().createComponentBuilder();

        if (parts.length > 0) {
            builder.append(parts[0]);
        }

        if (parts.length >= 2) {
            builder.append(getAttachmentsComponent(message));
            builder.append(parts[1]);
        }

        discordCraft.getServer().broadcastComponent(builder.build());
    }

    private MinecraftComponent getAttachmentsComponent(Message message) {
        MinecraftComponentBuilder builder = discordCraft.getServer().createComponentBuilder();

        List<Attachment> attachments = message.getAttachments();
        if (!attachments.isEmpty()) {
            builder.append("[");

            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = attachments.get(i);

                if (i != 0) {
                    builder.append(discordCraft.getMessageService().getPlainMessageOrDefault("chat.attachment-separator", ", "));
                }

                builder.append(attachment.getFileName())
                       .setClickEvent(ClickAction.OPEN_URL, attachment.getUrl());

                if (attachment.isSpoiler()) {
                    builder.append(discordCraft.getMessageService().getPlainMessageOrDefault("chat.attachment-spoiler", " (spoiler)"));
                }
            }

            builder.append("]");
        }

        return builder.build();
    }

}
