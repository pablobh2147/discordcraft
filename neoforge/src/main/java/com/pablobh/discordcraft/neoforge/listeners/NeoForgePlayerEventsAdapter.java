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
