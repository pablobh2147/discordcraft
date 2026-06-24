package com.pablobh.discordcraft.spigot.listeners;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplay;
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

import com.pablobh.discordcraft.listener.PlayerEventHandler;
import com.pablobh.discordcraft.spigot.platform.SpigotAdvancement;
import com.pablobh.discordcraft.spigot.platform.SpigotPlayer;

public class SpigotPlayerEventsAdapter implements Listener {

    private final PlayerEventHandler handler;

    public SpigotPlayerEventsAdapter(PlayerEventHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        handler.onPlayerJoin(
            new SpigotPlayer(event.getPlayer()),
            !event.getPlayer().hasPlayedBefore()
        );
    }

    @EventHandler
    private void onPlayerLeft(PlayerQuitEvent event) {
        handler.onPlayerQuit(new SpigotPlayer(event.getPlayer()));
    }

    @EventHandler
    private void onPlayerDied(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        String damageCause = null;
        if (damageEvent != null) {
            damageCause = damageEvent.getCause().name();
        }

        handler.onPlayerDeath(
            new SpigotPlayer(player),
            event.getDeathMessage(),
            damageCause
        );
    }

    @EventHandler
    public void onPlayerMurder(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageEvent) {
                if (lastDamageEvent.getDamager() instanceof Player killer) {
                    handler.onPlayerKillPlayer(
                        new SpigotPlayer(killer),
                        new SpigotPlayer(player)
                    );
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged) {
            if (event.getDamager() instanceof Player) {
                damaged.setLastDamageCause(event);
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        AdvancementDisplay display = advancement.getDisplay();

        if (display == null) {
            return;
        }

        handler.onPlayerAdvancement(
            new SpigotPlayer(event.getPlayer()),
            new SpigotAdvancement(display)
        );
    }

}
