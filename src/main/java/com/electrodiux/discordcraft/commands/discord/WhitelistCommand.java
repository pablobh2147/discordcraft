package com.electrodiux.discordcraft.commands.discord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.electrodiux.discordcraft.Messages;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class WhitelistCommand extends DiscordCommand {

    public WhitelistCommand() {
        super("whitelist");

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
                event.reply(Messages.getMessage(CommandManager.COMMAND_INVALID_SUBCOMMAND)).setEphemeral(true).queue(); // Should never happen
                break;
        }
    }

    // Enable and disable whitelist

    private void subcommandEnable(SlashCommandInteractionEvent event, boolean isEphemeral) {
        String enableMessage = getConfig().getString("messages.enable", "The whitelist has been enabled!");
        String alreadyEnableMessage = getConfig().getString("messages.already-enable", "The whitelist is already enabled!");

        if (Bukkit.hasWhitelist()) {
            event.reply(alreadyEnableMessage).setEphemeral(isEphemeral).queue();
            return;
        }

        Bukkit.setWhitelist(true);

        event.reply(enableMessage).setEphemeral(isEphemeral).queue();
    }

    private void subcommandDisable(SlashCommandInteractionEvent event, boolean isEphemeral) {
        String disableMessage = getConfig().getString("messages.disable", "The whitelist has been disabled!");
        String alreadyDisableMessage = getConfig().getString("messages.already-disable", "The whitelist is already disabled!");

        if (!Bukkit.hasWhitelist()) {
            event.reply(alreadyDisableMessage).setEphemeral(isEphemeral).queue();
            return;
        }

        Bukkit.setWhitelist(false);

        event.reply(disableMessage).setEphemeral(isEphemeral).queue();
    }

    // Modify whitelist

    private OfflinePlayer getOfflinePlayer(SlashCommandInteractionEvent event, boolean isEphemeral) {
        String player = event.getOption("player") == null ? null : event.getOption("player").getAsString();
        
        String notFoundMessage = getConfig().getString("messages.not-found", "User %player% not found!");
        String noPlayerOption = getConfig().getString("messages.no-player-option", "You must specify a player!");
        
        if (player == null) {
            event.reply(noPlayerOption).setEphemeral(isEphemeral).queue();
            return null;
        }

        // Get offline player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        
        if (offlinePlayer.getUniqueId() == null) {
            event.reply(notFoundMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
            return null;
        }

        return offlinePlayer;
    }

    private void subcommandAdd(SlashCommandInteractionEvent event, boolean isEphemeral) {
        OfflinePlayer offlinePlayer = getOfflinePlayer(event, isEphemeral);

        if (offlinePlayer == null) {
            return;
        }

        String addSuccessMessage = getConfig().getString("messages.add-success", "The player %player% has been added to the whitelist!");
        String alreadyWhitelistedMessage = getConfig().getString("messages.already-whitelisted", "The player %player% is already whitelisted!");

        if (offlinePlayer.isWhitelisted()) {
            event.reply(alreadyWhitelistedMessage.replace("%player%", offlinePlayer.getName())).setEphemeral(isEphemeral).queue();
            return;
        }

        offlinePlayer.setWhitelisted(true);
        event.reply(addSuccessMessage.replace("%player%", offlinePlayer.getName())).setEphemeral(isEphemeral).queue();
    }

    private void subcommandRemove(SlashCommandInteractionEvent event, boolean isEphemeral) {
        OfflinePlayer offlinePlayer = getOfflinePlayer(event, isEphemeral);

        if (offlinePlayer == null) {
            return;
        }

        String removeSuccessMessage = getConfig().getString("messages.remove-success", "The player %player% has been removed from the whitelist!");
        String notWhitelistedMessage = getConfig().getString("messages.not-whitelisted", "The player %player% is not whitelisted!");

        if (!offlinePlayer.isWhitelisted()) {
            event.reply(notWhitelistedMessage.replace("%player%", offlinePlayer.getName())).setEphemeral(isEphemeral).queue();
            return;
        }

        offlinePlayer.setWhitelisted(false);
        event.reply(removeSuccessMessage.replace("%player%", offlinePlayer.getName())).setEphemeral(isEphemeral).queue();
    }

}