package com.electrodiux.discordcraft.listeners;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.LinkedChannel;
import com.electrodiux.discordcraft.Messages;

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

        if (event.getChannelType() == ChannelType.PRIVATE) { // ignore private messages
            return;
        }

        LinkedChannel linkedChannel = Discord.getLinkedChannel(message.getChannel().asTextChannel());

        if (linkedChannel == null || !linkedChannel.canSendDiscordMessages()) { // ignore messages from channels that are not linked
            return;
        }

        if (!linkedChannel.canSendBotMessages() && author.isBot()) { // ignore bot messages if not allowed
            return;
        }

        if (!linkedChannel.canSendDiscordSystemMessages() && author.isSystem()) { // ignore system messages if not allowed
            return;
        }

        if (author.getIdLong() == Discord.getSelfUser().getIdLong()) { // ignore messages from the bot itself
            return;
        }

        // normal message broadcast

        String messageWithoutAttachments = Messages.applyMinecraftColorFormatting(Messages.getMessage(
                edited ? "chat.minecraft-edited-format" : "chat.minecraft-format",
                "username", author.getEffectiveName(),
                "guild", message.getGuild().getName(),
                "channel", message.getChannel().getName(),
                "message", message.getContentDisplay()
        ));

        // Replace attachments placeholder

        String[] parts = messageWithoutAttachments.split("%attachments%", 2);

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
                    attachmentsBuilder.append(Messages.getMessageWithDefault("chat.attachment-separator", ", "));
                }

                TextComponent attachmentComponent = new TextComponent(attachment.getFileName());
                attachmentComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()));

                attachmentsBuilder.append(attachmentComponent);

                if (attachment.isSpoiler()) {
                    attachmentsBuilder.append(Messages.getMessageWithDefault("chat.attachment-spoiler", " (spoiler)"));
                }

            }

            attachmentsBuilder.append("]");
        }

        return attachmentsBuilder;
    }

}
