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

package com.pablobh.discordcraft.neoforge.listeners;

import com.pablobh.discordcraft.listener.PlayerEventHandler;
import com.pablobh.discordcraft.neoforge.platform.NeoForgeAdvancement;
import com.pablobh.discordcraft.neoforge.platform.NeoForgePlayer;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class NeoForgePlayerEventsAdapter {

    private final PlayerEventHandler handler;

    public NeoForgePlayerEventsAdapter(PlayerEventHandler handler) {
        this.handler = handler;
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Check if player is new by checking if they have any play time recorded
            boolean isNewPlayer = player.getStats().getValue(net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.PLAY_TIME)) == 0;
            handler.onPlayerJoin(
                new NeoForgePlayer(player),
                isNewPlayer
            );
        }
    }

    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            handler.onPlayerQuit(new NeoForgePlayer(player));
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DamageSource source = event.getSource();
            String deathMessage = source.getLocalizedDeathMessage(player).getString();
            String damageCause = source.getMsgId();

            handler.onPlayerDeath(
                new NeoForgePlayer(player),
                deathMessage,
                damageCause
            );

            if (source.getEntity() instanceof ServerPlayer killer) {
                handler.onPlayerKillPlayer(
                    new NeoForgePlayer(killer),
                    new NeoForgePlayer(player)
                );
            }
        }
    }

    @SubscribeEvent
    public void onPlayerAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        DisplayInfo display = event.getAdvancement().value().display().orElse(null);
        
        if (display == null) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            handler.onPlayerAdvancement(
                new NeoForgePlayer(player),
                new NeoForgeAdvancement(display)
            );
        }
    }

}
