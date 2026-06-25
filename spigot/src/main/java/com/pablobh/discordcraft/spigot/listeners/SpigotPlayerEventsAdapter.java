/*
 * This file is part of DiscordCraft.
 *
 * Copyright (c) 2025 Pablo Bermejo Hernández
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.pablobh.discordcraft.spigot.listeners;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
        EntityDamageEvent damageEvent = player.getLastDamageCause();
        String deathMessage = event.getDeathMessage();

        String damageCause = null;
        if (damageEvent != null) {
            damageCause = damageEvent.getCause().name();
        }

        // Check if this is a murder (player killed by another player)
        if (damageEvent instanceof EntityDamageByEntityEvent entityDamageEvent) {
            if (entityDamageEvent.getDamager() instanceof Player killer) {
                handler.onPlayerKillPlayer(
                    new SpigotPlayer(killer),
                    new SpigotPlayer(player),
                    deathMessage
                );
                event.setDeathMessage(null);
                return;
            }
        }

        // Handle normal death
        handler.onPlayerDeath(
            new SpigotPlayer(player),
            deathMessage,
            damageCause
        );
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
