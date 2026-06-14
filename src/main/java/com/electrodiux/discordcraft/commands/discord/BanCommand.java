package com.electrodiux.discordcraft.commands.discord;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class BanCommand extends DiscordCommand {

    public BanCommand() {
        super("ban");

        addOption(OptionType.STRING, "player", "The player to ban", true);
        addOption(OptionType.STRING, "reason", "The reason for the ban", false);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        String notFoundMessage = getConfig().getString("messages.not-found", "User %player% not found!");
        String successMessage = getConfig().getString("messages.success", "The player %player% has been banned because %reason%!");
        String alreadyBannedMessage = getConfig().getString("messages.already-banned", "The player %player% is already banned!");

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String player = event.getOption("player").getAsString();
        String reason = event.getOption("reason") == null ? null : event.getOption("reason").getAsString();

        if (reason == null) {
            reason = getConfig().getString("default-reason", "You have been banned from the server!");
        }

        // Get offline player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer.getUniqueId() == null) {
            event.reply(notFoundMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
            return;
        }

        if (offlinePlayer.isBanned()) {
            event.reply(alreadyBannedMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
            return;
        }

        Bukkit.getBanList(BanList.Type.NAME).addBan(offlinePlayer.getName(), reason, null, reason);

        event.reply(successMessage.replace("%player%", player).replace("%reason%", reason)).setEphemeral(isEphemeral).queue();

        DiscordCraft.logInfo("Player " + player + " has been banned from the server by " + event.getUser().getEffectiveName() + " because " + reason + "!");
    }
    
}
