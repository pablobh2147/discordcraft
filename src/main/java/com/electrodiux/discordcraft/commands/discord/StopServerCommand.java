package com.electrodiux.discordcraft.commands.discord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class StopServerCommand extends DiscordCommand {

    public static final int MINIMUM_DELAY = 5;
    public static final int MAXIMUM_DELAY = 60 * 10; // 10 minutes

    public StopServerCommand() {
        super("stop-server");

        addOption(OptionType.INTEGER, "delay", "Delay in seconds", false)
        .setMinValue(MINIMUM_DELAY)
        .setMaxValue(MAXIMUM_DELAY);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        String message = getConfig().getString("message", "Server will stop in %seconds% seconds.");
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

        message = message.replace("%seconds%", String.valueOf(delay));

        event.reply(message).setEphemeral(isEphemeral).queue();

        if (showTitle) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("Stoping Server", message, 10, 60, 10); // Time in ticks
            }
        }

        DiscordCraft.discordLogInfo("Stopping server in " + delay + " seconds, requested by " + event.getUser().getAsMention());

        Bukkit.getScheduler().runTaskLater(DiscordCraft.instance(), this::stopServer, delay * 20); // Time in ticks
    }

    private void stopServer() {
        Bukkit.getServer().shutdown();
    }

}
