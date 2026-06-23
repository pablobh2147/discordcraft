package com.pablobh.discordcraft.spigot.discord.commands;

import javax.annotation.Nonnull;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.profile.PlayerProfile;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.spigot.message.SpigotPlaceholder;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PardonCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "pardon";

    private final DiscordCraft discordCraft;

    public PardonCommand(@Nonnull DiscordCommandManager manager, @Nonnull DiscordCraft discordCraft) {
        super(COMMAND_NAME, manager);
        this.discordCraft = discordCraft;

        addOption(OptionType.STRING, "player", "The player to pardon", true);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String player = event.getOption("player").getAsString();

        // Get offline player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer == null || !offlinePlayer.isBanned()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-banned"), "The player %player% is not banned!");
            msg.replace("player", player);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        BanList<PlayerProfile> profileBanList = Bukkit.getBanList(BanList.Type.PROFILE);
        profileBanList.pardon(offlinePlayer.getPlayerProfile());

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("success"), "The player %player_name% has been unbanned!");
        msg.replace("player", SpigotPlaceholder.player(offlinePlayer));
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();

        discordCraft.getLogger().info("Player " + player + " has been pardoned from the server by " + event.getUser().getEffectiveName() + "!");
    }
    
}
