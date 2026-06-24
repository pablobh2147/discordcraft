package com.pablobh.discordcraft.discord.command;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;
import com.pablobh.discordcraft.platform.MinecraftServer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PardonCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "pardon";

    private final MinecraftServer minecraftServer;

    public PardonCommand(@Nonnull DiscordCommandManager manager, @Nonnull MinecraftServer minecraftServer) {
        super(COMMAND_NAME, manager);
        this.minecraftServer = minecraftServer;

        addOption(OptionType.STRING, "player", "The player to pardon", true);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String playerName = event.getOption("player").getAsString();

        MinecraftPlayerProfile playerProfile = minecraftServer.getPlayerProfile(playerName);
        if (playerProfile == null) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-found"), "The player %player% was not found!");
            msg.replace("player", playerName);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        if (!playerProfile.isBanned()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-banned"), "The player %player_name% is not banned!");
            msg.replace("player", playerProfile);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        playerProfile.setBanned(false, null);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("success"), "The player %player_name% has been unbanned!");
        msg.replace("player", playerProfile);
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
    }
    
}
