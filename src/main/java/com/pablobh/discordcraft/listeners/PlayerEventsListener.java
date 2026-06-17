package com.pablobh.discordcraft.listeners;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.message.MessageService;

public class PlayerEventsListener implements Listener {

    private final DiscordService discordService;
    private final MessageService messageService;

    public PlayerEventsListener(DiscordService discordService, MessageService messageService) {
        this.discordService = discordService;
        this.messageService = messageService;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Message message = messageService.getDiscordMessage("player.join");

        if (!event.getPlayer().hasPlayedBefore()) {
            Message firstJoinMessage = messageService.getDiscordMessage("player.welcome");
            if (firstJoinMessage != null) {
                message = firstJoinMessage;
            }
        }

        if (message == null) {
            return;
        }

        message.replace("player", event.getPlayer());

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerJoinMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    @EventHandler
    private void onPlayerLeft(PlayerQuitEvent event) {
        Message message = messageService.getDiscordMessage("player.left");

        if (message == null) {
            return;
        }

        message.replace("player", event.getPlayer());

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerLeaveMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    @EventHandler
    private void onPlayerDied(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        Message deathMessage = null;
        if (damageEvent != null) {
            String deathMessageKey = "custom-death-messages." + damageEvent.getCause().name().toLowerCase();
            deathMessage = messageService.getDiscordMessage(deathMessageKey);
        }

        String deathMessageStr = event.getDeathMessage();

        if (deathMessage != null) {
            deathMessage.replace("player", player);
            deathMessage.replace("death_message", deathMessageStr);
            deathMessageStr = deathMessage.toString();
        }

        Message finalDeathMessage = messageService.getDiscordMessage("player.death");
        String finalDeathMessageStr = deathMessageStr;

        if (finalDeathMessage != null) {
            finalDeathMessage.replace("player", player);
            finalDeathMessage.replace("death_message", deathMessageStr);
            finalDeathMessageStr = finalDeathMessage.toString();
        } else {
            finalDeathMessageStr = deathMessageStr;
        }

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendPlayerDeathMessages()) {
                if (finalDeathMessage != null) {
                    channel.sendMessage(finalDeathMessage);
                } else {
                    channel.sendMessage(finalDeathMessageStr);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMurder(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageEvent) {
                if (lastDamageEvent.getDamager() instanceof Player killer) {
                    Message message = messageService.getDiscordMessage("player.murder");

                    if (message == null) {
                        return;
                    }

                    message.replace("killer", killer);
                    message.replace("victim", player);

                    for (LinkedChannel channel : discordService.getLinkedChannels()) {
                        if (channel.canSendPlayerMurderMessages()) {
                            channel.sendMessage(message.toString());
                        }
                    }
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

        Message message = messageService.getDiscordMessage("player.achivement-unlock");

        if (message == null) {
            return;
        }

        message.replace("advancement", display);
        message.replace("player", event.getPlayer());
        message.replace("display_url", getAdvancementDisplayURL(display));

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendPlayerAdvancementMessages()) {
                channel.sendMessage(message);
            }
        }

    }

}
