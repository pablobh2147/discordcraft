package com.pablobh.discordcraft.discord.commands;

import javax.annotation.Nonnull;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.profile.PlayerProfile;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PardonCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "pardon";
    private static final String COMMAND_CONFIG_KEY = "pardon";

    public PardonCommand(@Nonnull DiscordCommandManager manager) {
        super(COMMAND_NAME, manager.getCommandConfig(COMMAND_CONFIG_KEY));

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

        BanList<PlayerProfile> profileBanList = Bukkit.getBanList(BanList.Type.PROFILE);
        profileBanList.pardon(offlinePlayer.getPlayerProfile());

        event.reply(successMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();

        DiscordCraft.logInfo("Player " + player + " has been pardoned from the server by " + event.getUser().getEffectiveName() + "!");
    }
    
}
