package com.pablobh.discordcraft.discord.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import com.pablobh.discordcraft.StringUtils;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;

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

    public PlayerListCommand(@Nonnull DiscordCommandManager manager) {
        super(COMMAND_NAME, manager);

        PlayerListData online = new PlayerListData(getConfig().getConfigurationSection("lists.online"), "online");
        PlayerListData whitelisted = new PlayerListData(getConfig().getConfigurationSection("lists.whitelisted"), "whitelisted");
        PlayerListData banned = new PlayerListData(getConfig().getConfigurationSection("lists.banned"), "banned");
        PlayerListData operator = new PlayerListData(getConfig().getConfigurationSection("lists.operator"), "operator");

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

        List<String> players = null;

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

    // List of player names

    private List<String> getPlayerList(String listType) {
        switch (listType) {
            case "online":
                return getOnlinePlayers();
            case "whitelisted":
                return getWhitelistedPlayers();
            case "banned":
                return getBannedPlayers();
            case "operator":
                return getOperatorPlayers();
            default:
                return null;
        }
    }

    public static List<String> getWhitelistedPlayers() {
        Set<OfflinePlayer> whitelistedPlayers = Bukkit.getWhitelistedPlayers();
        List<String> whitelistedPlayerNames = new ArrayList<>();
        for (OfflinePlayer player : whitelistedPlayers) {
            whitelistedPlayerNames.add(player.getName());
        }
        return whitelistedPlayerNames;
    }

    public static List<String> getBannedPlayers() {
        Set<OfflinePlayer> bannedPlayers = Bukkit.getBannedPlayers();
        List<String> bannedPlayerNames = new ArrayList<>();
        for (OfflinePlayer player : bannedPlayers) {
            bannedPlayerNames.add(player.getName());
        }
        return bannedPlayerNames;
    }

    public static List<String> getOperatorPlayers() {
        Set<OfflinePlayer> operatorPlayers = Bukkit.getOperators();
        List<String> operatorPlayerNames = new ArrayList<>();
        for (OfflinePlayer player : operatorPlayers) {
            operatorPlayerNames.add(player.getName());
        }
        return operatorPlayerNames;
    }

    public static List<String> getOnlinePlayers() {
        List<String> onlinePlayerNames = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getOnlinePlayers()) {
            onlinePlayerNames.add(player.getName());
        }
        return onlinePlayerNames;
    }

}
