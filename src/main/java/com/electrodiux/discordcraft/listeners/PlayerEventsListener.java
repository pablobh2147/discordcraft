package com.electrodiux.discordcraft.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.LinkedChannel;
import com.electrodiux.discordcraft.Messages;

public class PlayerEventsListener implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        String message = Messages.getMessage("player.join", "player", event.getPlayer());

        if (!event.getPlayer().hasPlayedBefore()) {
            String firstJoinMessage = Messages.getMessageWithDefault("player.first-join", null, "player", event.getPlayer());
            if (firstJoinMessage != null) {
                message = firstJoinMessage;
            }
        }

        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerJoinMessages()) {
                linkedChannel.getChannel().sendMessage(message).queue();
            }
        }
    }

    @EventHandler
    private void onPlayerLeft(PlayerQuitEvent event) {
        String message = Messages.getMessage("player.left", "player", event.getPlayer());

        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerLeaveMessages()) {
                linkedChannel.getChannel().sendMessage(message).queue();
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

            for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
                if (linkedChannel.canSendPlayerDeathMessages()) {
                    linkedChannel.getChannel().sendMessage(finalMessage).queue();
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
                    for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
                        if (linkedChannel.canSendPlayerMurderMessages()) {
                            linkedChannel.getChannel().sendMessage(killMessage).queue();
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

}
