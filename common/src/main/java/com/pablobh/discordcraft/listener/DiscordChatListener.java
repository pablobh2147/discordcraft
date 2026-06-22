package com.pablobh.discordcraft.listener;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class DiscordChatListener extends ListenerAdapter {

    private final DiscordService discordService;
    private final MessageService messageService;

    public DiscordChatListener(DiscordService discordService, MessageService messageService) {
        this.discordService = discordService;
        this.messageService = messageService;
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            return;
        }

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
        
        LinkedChannel linkedChannel = discordService.getLinkedChannel(message.getChannel().asTextChannel());

        // Ignore messages from channels that are not linked
        if (linkedChannel == null || !linkedChannel.canSendDiscordMessages()) { 
            return;
        }

        // Ignore system messages if not allowed
        if (!linkedChannel.canSendDiscordSystemMessages() && author.isSystem()) { 
            return;
        }

        // Ignore messages from the bot itself
        if (author.getIdLong() == discordService.getSelfUser().getIdLong()) { 
            return;
        }

        // Normal message broadcast
        com.pablobh.discordcraft.spigot.message.Message rawMessage = messageService.getMessage(edited ? "chat.minecraft-edited-format" : "chat.minecraft-format");

        rawMessage.formatMinecraftColors();
        rawMessage.replace("username", author.getEffectiveName());
        rawMessage.replace("guild", message.getGuild().getName());
        rawMessage.replace("channel", message.getChannel().getName());
        rawMessage.replace("message", message.getContentDisplay());

        // Replace attachments placeholder
        String[] parts = rawMessage.toString().split("%attachments%", 2);

        ComponentBuilder finalMessageBuilder = new ComponentBuilder("");

        if (parts.length > 0) {
            finalMessageBuilder.append(parts[0]);
        }

        if (parts.length >= 2) { // if there is a %attachments% placeholder
            ComponentBuilder attachmentsBuilder = getAttachmentsComponent(message);
            finalMessageBuilder.append(attachmentsBuilder.create());

            finalMessageBuilder.append(parts[1]);
        }

        // Send message to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(finalMessageBuilder.create());
        }
    }

    // Creation of attachments component

    private ComponentBuilder getAttachmentsComponent(Message message) {

        ComponentBuilder attachmentsBuilder = new ComponentBuilder("");

        if (!message.getAttachments().isEmpty()) {

            attachmentsBuilder.append("[");

            List<Attachment> attachmentns = message.getAttachments();
            for (int i = 0; i < attachmentns.size(); i++) {

                Attachment attachment = attachmentns.get(i);

                if (i != 0) {
                    attachmentsBuilder.append(messageService.getPlainMessageOrDefault("chat.attachment-separator", ", "));
                }

                TextComponent attachmentComponent = new TextComponent(attachment.getFileName());
                attachmentComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()));

                attachmentsBuilder.append(attachmentComponent);

                if (attachment.isSpoiler()) {
                    attachmentsBuilder.append(messageService.getPlainMessageOrDefault("chat.attachment-spoiler", " (spoiler)"));
                }

            }

            attachmentsBuilder.append("]");
        }

        return attachmentsBuilder;
    }

}
