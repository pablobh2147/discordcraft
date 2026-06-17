package com.pablobh.discordcraft.discord.commands;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.profile.PlayerProfile;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class BanCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "ban";

    public BanCommand(@Nonnull DiscordCommandManager manager) {
        super(COMMAND_NAME, manager);

        addOption(OptionType.STRING, "player", "The player to ban", true);
        addOption(OptionType.STRING, "reason", "The reason for the ban", false);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String playerName = event.getOption("player").getAsString();
        String reason = event.getOption("reason") == null ? null : event.getOption("reason").getAsString();

        if (reason == null) {
            reason = getConfig().getString("default-reason", "You have been banned from the server!");
        }

        // Get offline player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if (offlinePlayer.getUniqueId() == null) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-found"), "The player %player% was not found!");
            msg.replace("player", playerName);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        if (offlinePlayer.isBanned()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("already-banned"), "The player %player_name% is already banned!");
            msg.replace("player", offlinePlayer);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        BanList<PlayerProfile> profileBanList = Bukkit.getBanList(BanList.Type.PROFILE);
        profileBanList.addBan(offlinePlayer.getPlayerProfile(), reason, (Instant) null, reason);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("success"), "The player %player_name% has been banned because %reason%!");
        msg.replace("player", offlinePlayer).replace("reason", reason);
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();

        DiscordCraft.logInfo("Player " + playerName + " has been banned from the server by " + event.getUser().getEffectiveName() + " because " + reason + "!");
    }
    
}
