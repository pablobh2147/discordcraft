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

package com.pablobh.discordcraft.discord.command;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.StringUtils;
import com.pablobh.discordcraft.configuration.ConfigurationSection;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.platform.MinecraftServer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PlayerListCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "playerlist";

    private static class PlayerListData {

        private boolean enabled;

        private String name;
        private String enumValue;

        public PlayerListData(ConfigurationSection config, String enumValue) {
            this.enabled = config.getBoolean("enabled", true);
            this.name = config.getString("name", "Player List");
            this.enumValue = enumValue;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getName() {
            return name;
        }

        public String getEnumValue() {
            return enumValue;
        }

    }

    private final MinecraftServer minecraftServer;

    public PlayerListCommand(@Nonnull DiscordCommandManager manager, @Nonnull MinecraftServer minecraftServer) {
        super(COMMAND_NAME, manager);
        this.minecraftServer = minecraftServer;

        PlayerListData online = new PlayerListData(getConfig().getSection("lists.online"), "online");
        PlayerListData whitelisted = new PlayerListData(getConfig().getSection("lists.whitelisted"), "whitelisted");
        PlayerListData banned = new PlayerListData(getConfig().getSection("lists.banned"), "banned");
        PlayerListData operator = new PlayerListData(getConfig().getSection("lists.operator"), "operator");

        if (!online.isEnabled() && !whitelisted.isEnabled() && !banned.isEnabled() && !operator.isEnabled()) {
            this.setEnabled(false); // Disable the command if all lists are disabled
        }

        OptionData listOption = super.addOption(
            OptionType.STRING,
            "list",
            "Selects the type of list this command shows, like 'online', 'whitelisted', 'banned'",
            true
        );

        // Add choices for the list option

        if (online.isEnabled()) listOption.addChoice(online.getName(), online.getEnumValue());
        if (whitelisted.isEnabled()) listOption.addChoice(whitelisted.getName(), whitelisted.getEnumValue());
        if (banned.isEnabled()) listOption.addChoice(banned.getName(), banned.getEnumValue());
        if (operator.isEnabled()) listOption.addChoice(operator.getName(), operator.getEnumValue());

    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        String listType = event.getOption("list").getAsString();
        String listName = getConfig().getString("lists." + listType + ".name", StringUtils.capitalize(listType));

        Collection<String> players = null;

        switch (listType) {
            case "online":
            case "whitelisted":
            case "banned":
            case "operator":

                if (getConfig().getBoolean("lists." + listType + ".enabled", true)) {
                    players = getPlayerList(listType);
                } else { // This should never happen but for safety
                    Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("list-disabled"), "%list_name% players list is disabled!");
                    msg.replace("list_name", listName);
                    event.reply(msg.toDiscordMessage()).setEphemeral(true).queue();
                    return;
                }
                break;

            default:
                event.reply("Invalid list type!").setEphemeral(true).queue(); // This should never happen but for safety
                return;
        }
        
        boolean ephemeral = getConfig().getBoolean("is-ephemeral", false);

        if (players == null || players.isEmpty()) { // In case there is no players in the list
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("list-empty"), "There are no %list_name% players online!");
            msg.replace("list_name", listName);
            event.reply(msg.toDiscordMessage()).setEphemeral(ephemeral).queue();
        } else { // If there are players in the list

            String listHeaderMessage = getMessageService().getPlainMessageOrDefault(getMessageKey("list-header"), "List of %list_name% players (%player_count%):");
            String rowFormat = getMessageService().getPlainMessageOrDefault(getMessageKey("list-row"), "- ***%player%***");

            StringBuilder replyMessage = new StringBuilder();

            replyMessage.append(listHeaderMessage
                .replace("%list_name%", listName)
                .replace("%player_count%", String.valueOf(players.size()))
            );

            for (String playerName : players) {
                replyMessage.append("\n").append(rowFormat.replace("%player%", playerName));
            }

            event.reply(replyMessage.toString()).setEphemeral(ephemeral).queue();
            
        }

    }

    private Collection<String> getPlayerList(String listType) {
        switch (listType) {
            case "online":
                return minecraftServer.getOnlinePlayerNames();
            case "whitelisted":
                return minecraftServer.getWhitelistedPlayerNames();
            case "banned":
                return minecraftServer.getBannedPlayerNames();
            case "operator":
                return minecraftServer.getOperatorPlayerNames();
            default:
                return null;
        }
    }

}
