package com.pablobh.discordcraft.discord.commands;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class StopServerCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "stop";

    public static final int MINIMUM_DELAY = 5;
    public static final int MAXIMUM_DELAY = 60 * 10; // 10 minutes

    private final MessageService messageService;

    public StopServerCommand(@Nonnull DiscordCommandManager manager, @Nonnull MessageService messageService) {
        super(COMMAND_NAME, manager.getCommandConfig(COMMAND_NAME));

        this.messageService = messageService;

        addOption(OptionType.INTEGER, "delay", "Delay in seconds", false)
        .setMinValue(MINIMUM_DELAY)
        .setMaxValue(MAXIMUM_DELAY);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);
        boolean showTitle = getConfig().getBoolean("show-title", true);

        // Delay
        
        int delay = getConfig().getInt("delay", MINIMUM_DELAY);

        OptionMapping delayOption = event.getOption("delay");
        
        if (delayOption != null) {
            delay = delayOption.getAsInt();
        }

        if (delay < MINIMUM_DELAY) {
            delay = MINIMUM_DELAY;
            getConfig().set("delay", MINIMUM_DELAY);
        }

        if (delay > MAXIMUM_DELAY) {
            delay = MAXIMUM_DELAY;
            getConfig().set("delay", MAXIMUM_DELAY);
        }

        String seconds = String.valueOf(delay);

        // Discord reply
        Message discordMsg = messageService.getDiscordMessageOrDefault("commands.stop.message", "The server is stopping in %seconds% seconds");
        discordMsg.replace("seconds", seconds);
        event.reply(discordMsg.toDiscordMessage()).setEphemeral(isEphemeral).queue();

        // Minecraft title
        if (showTitle) {
            String title = messageService.getPlainMessageOrDefault("commands.stop.minecraft-title", "Stopping Server");
            String subtitle = messageService.getPlainMessageOrDefault("commands.stop.minecraft-subtitle", "The server is stopping in %seconds% seconds");
            subtitle = subtitle.replace("%seconds%", seconds);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(title, subtitle, 10, 60, 10); // Time in ticks
            }
        }

        DiscordCraft.discordLogInfo("Stopping server in " + delay + " seconds, requested by " + event.getUser().getAsMention());

        Bukkit.getScheduler().runTaskLater(DiscordCraft.getInstance(), this::stopServer, delay * 20); // 20 ticks = 1 second
    }

    private void stopServer() {
        Bukkit.getServer().shutdown();
    }

}
