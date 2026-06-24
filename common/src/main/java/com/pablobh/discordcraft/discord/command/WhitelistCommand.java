package com.pablobh.discordcraft.discord.command;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;
import com.pablobh.discordcraft.platform.MinecraftServer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class WhitelistCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "whitelist";

    private final MinecraftServer minecraftServer;

    public WhitelistCommand(@Nonnull DiscordCommandManager manager, @Nonnull MinecraftServer minecraftServer) {
        super(COMMAND_NAME, manager);
        this.minecraftServer = minecraftServer;

        boolean allowToggleWhitelist = getConfig().getBoolean("allow-toggle-whitelist", true);
        boolean allowModifyWhitelist = getConfig().getBoolean("allow-modify-whitelist", true);

        if (allowToggleWhitelist) {
            addSubcommand("enable", "Enable the whitelist");
            addSubcommand("disable", "Disable the whitelist");
        }

        if (allowModifyWhitelist) {
            addSubcommand("add", "Add a player to the whitelist").addOption(OptionType.STRING, "player", "The player to add to the whitelist", false);
            addSubcommand("remove", "Remove a player from the whitelist").addOption(OptionType.STRING, "player", "The player to remove from the whitelist", false);
        }

        if (!allowToggleWhitelist && !allowModifyWhitelist) {
            setEnabled(false);
        }
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        switch (event.getSubcommandName()) {
            case "enable":
                subcommandEnable(event, isEphemeral);
                break;
            case "disable":
                subcommandDisable(event, isEphemeral);
                break;
            case "add":
                subcommandAdd(event, isEphemeral);
                break;
            case "remove":
                subcommandRemove(event, isEphemeral);
                break;
            default:
                event.reply(getMessageService().getDiscordMessage("commands.invalid-subcommand").toDiscordMessage()).setEphemeral(true).queue(); // Should never happen
                break;
        }
    }

    // Enable and disable whitelist

    private void subcommandEnable(SlashCommandInteractionEvent event, boolean isEphemeral) {
        if (minecraftServer.isWhitelistEnabled()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("already-enabled"), "The whitelist is already enabled!");
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        minecraftServer.setWhitelistEnabled(true);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("enabled"), "The whitelist has been enabled!");
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
    }

    private void subcommandDisable(SlashCommandInteractionEvent event, boolean isEphemeral) {
        if (!minecraftServer.isWhitelistEnabled()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("already-disabled"), "The whitelist is already disabled!");
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        minecraftServer.setWhitelistEnabled(false);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("disabled"), "The whitelist has been disabled!");
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
    }

    // Modify whitelist

    private MinecraftPlayerProfile getOfflinePlayer(SlashCommandInteractionEvent event, boolean isEphemeral) {
        String playerName = event.getOption("player") == null ? null : event.getOption("player").getAsString();
        
        if (playerName == null) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("no-player-option"), "You must specify a player!");
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return null;
        }

        MinecraftPlayerProfile playerProfile = minecraftServer.getPlayerProfile(playerName);
        if (playerProfile == null) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-found"), "The player %player% was not found!");
            msg.replace("player", playerName);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return null;
        }

        return playerProfile;
    }

    private void subcommandAdd(SlashCommandInteractionEvent event, boolean isEphemeral) {
        MinecraftPlayerProfile playerProfile = getOfflinePlayer(event, isEphemeral);

        if (playerProfile == null) {
            return;
        }

        if (playerProfile.isWhitelisted()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("already-whitelisted"), "The player %player_name% is already whitelisted!");
            msg.replace("player", playerProfile);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        playerProfile.setWhitelisted(true);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("add-success"), "The player %player_name% has been added to the whitelist!");
        msg.replace("player", playerProfile);
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
    }

    private void subcommandRemove(SlashCommandInteractionEvent event, boolean isEphemeral) {
        MinecraftPlayerProfile playerProfile = getOfflinePlayer(event, isEphemeral);

        if (playerProfile == null) {
            return;
        }

        if (!playerProfile.isWhitelisted()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-whitelisted"), "The player %player_name% is not whitelisted!");
            msg.replace("player", playerProfile);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        playerProfile.setWhitelisted(false);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("remove-success"), "The player %player_name% has been removed from the whitelist!");
        msg.replace("player", playerProfile);
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
    }

}