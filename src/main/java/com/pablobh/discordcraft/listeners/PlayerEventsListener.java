package com.pablobh.discordcraft.listeners;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pablobh.discordcraft.Messages;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PlayerEventsListener implements Listener {

    private final DiscordService discordService;

    public PlayerEventsListener(DiscordService discordService) {
        this.discordService = discordService;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        String message = Messages.getMessage("player.join", "player", event.getPlayer());

        if (!event.getPlayer().hasPlayedBefore()) {
            String firstJoinMessage = Messages.getMessageWithDefault("player.first-join", null, "player", event.getPlayer());
            if (firstJoinMessage != null) {
                message = firstJoinMessage;
            }
        }

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerJoinMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    @EventHandler
    private void onPlayerLeft(PlayerQuitEvent event) {
        String message = Messages.getMessage("player.left", "player", event.getPlayer());

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerLeaveMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    @EventHandler
    private void onPlayerDied(PlayerDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
            String deathMessage = Messages.getMessageWithDefault("custom-death-messages." + damageEvent.getCause().name().toLowerCase(), null, "death_message", event.getDeathMessage());

            if (deathMessage == null) {
                deathMessage = event.getDeathMessage();
            }

            String finalMessage = Messages.getMessage("player.death", "player", player, "death_message", deathMessage);

            for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
                if (linkedChannel.canSendPlayerDeathMessages()) {
                    linkedChannel.sendMessage(finalMessage);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMurder(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        // Check if the entity that died is a player
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Check if the player was killed by another player
            if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lastDamageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
                Entity damager = lastDamageEvent.getDamager();

                if (damager instanceof Player) {
                    Player killer = (Player) damager;

                    String killMessage = Messages.getMessage("player.murder", "killer", killer, "victim", player);

                    // Send a message to the linked channels
                    for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
                        if (linkedChannel.canSendPlayerMurderMessages()) {
                            linkedChannel.sendMessage(killMessage);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        // Check if the damaged entity is a player
        if (damaged instanceof Player) {
            Player damagedPlayer = (Player) damaged;

            // Check if the damager is a player
            if (damager instanceof Player) {
                damagedPlayer.setLastDamageCause(event);
            }
        }
    }

    private static final String ADVANCEMENT_API_ENDPOINT = "https://minecraft-api.com/api/achivements/{block}/{title}/{desc}";

    private String getAdvancementDisplayURL(AdvancementDisplay display) {
        String block = display.getIcon().getType().name().toLowerCase();
        String title = "Advancement..Made";
        String name = URLEncoder.encode(display.getTitle().replace(" ", ".."), StandardCharsets.UTF_8);

        String url = ADVANCEMENT_API_ENDPOINT
            .replace("{block}", block)
            .replace("{title}", title)
            .replace("{desc}", name);
        
        return url;
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        AdvancementDisplay display = advancement.getDisplay();

        // Skip hidden advancements (like recipe unlocks)
        if (display == null) {
            return;
        }

        String title = Messages.getMessage("player.achivement-unlock.title", "player", event.getPlayer(), "advancement_title", display.getTitle());
        String description = Messages.getMessage("player.achivement-unlock.description", "player", event.getPlayer(), "advancement_title", display.getTitle(), "advancement_description", display.getDescription());
        String attachmentUrl = getAdvancementDisplayURL(display);

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendPlayerAdvancementMessages()) {
                TextChannel textChannel = channel.getChannel();

                textChannel.sendMessage(title).addEmbeds(new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .setImage(attachmentUrl)
                    .build()).queue();
            }
        }

    }

}
