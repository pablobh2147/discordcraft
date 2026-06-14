package com.electrodiux.discordcraft.commands.discord;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PardonCommand extends DiscordCommand {

    public PardonCommand() {
        super("pardon");

        addOption(OptionType.STRING, "player", "The player to pardon", true);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        String successMessage = getConfig().getString("messages.success", "The player %player% has been pardoned!");
        String notBannedMessage = getConfig().getString("messages.not-banned", "The player %player% is not banned!");

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String player = event.getOption("player").getAsString();

        // Get offline player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer == null || !offlinePlayer.isBanned()) {
            event.reply(notBannedMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
            return;
        }

        Bukkit.getBanList(BanList.Type.NAME).pardon(offlinePlayer.getName());

        event.reply(successMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();

        DiscordCraft.logInfo("Player " + player + " has been pardoned from the server by " + event.getUser().getEffectiveName() + "!");
    }
    
}
