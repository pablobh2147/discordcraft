package com.electrodiux.discordcraft.links;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.entities.Member;

public class AccountLink {

    public static final String ACCOUNT_LINKS = "account-links";

    public static final String DISCORD_ID = "discord-id";
    public static final String MINECRAFT_UUID = "minecraft-uuid";
    public static final String VERIFIED = "verified";


    private long discordId;
    private transient Member member = null;

    private UUID minecraftUuid;
    private transient OfflinePlayer offlinePlayer = null;

    private boolean verified = false;

    private AccountLink(long discordId, UUID minecraftUuid, boolean verified) {
        this.discordId = discordId;
        this.minecraftUuid = minecraftUuid;

        Discord.getMainGuild().retrieveMemberById(discordId).queue(m -> {
            member = m;
        });

        offlinePlayer = Bukkit.getOfflinePlayer(minecraftUuid);

        this.verified = verified;
    }

    private AccountLink(Member member, OfflinePlayer offlinePlayer) {
        this.discordId = member.getIdLong();
        this.member = member;
        
        this.minecraftUuid = offlinePlayer.getUniqueId();
        this.offlinePlayer = offlinePlayer;

        this.verified = false;
    }

    // Discord

    public long getDiscordId() {
        return discordId;
    }

    public Member getMember() {
        return member;
    }

    // Minecraft

    public UUID getMinecraftUuid() {
        return minecraftUuid;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    // Verification

    public boolean isVerified() {
        return verified;
    }

    public void verify() {
        if (!verified) {
            verified = true;
            AccountLink.save();
        }
    }

    // Static loader

    private static List<AccountLink> links = new ArrayList<>();

    public static AccountLink getLinkByDiscord(Member member) {
        for (AccountLink link : links) {
            if (link.getDiscordId() == member.getIdLong()) {
                return link;
            }
        }
        return null;
    }

    public static AccountLink getLinkByMinecraft(OfflinePlayer offlinePlayer) {
        for (AccountLink link : links) {
            if (link.getMinecraftUuid().equals(offlinePlayer.getUniqueId())) {
                return link;
            }
        }
        return null;
    }

    public static void addLink(Member member, OfflinePlayer offlinePlayer) {
        AccountLink link = new AccountLink(member, offlinePlayer);
        links.add(link);
        AccountLink.save();
    }

    public static void removeLink(Member member) {
        for (Iterator<AccountLink> iterator = links.iterator(); iterator.hasNext();) {
            AccountLink link = iterator.next();
            if (link.getDiscordId() == member.getIdLong()) {
                iterator.remove();
                AccountLink.save();
            }
        }
    }

    public static void removeLink(OfflinePlayer offlinePlayer) {
        for (Iterator<AccountLink> iterator = links.iterator(); iterator.hasNext();) {
            AccountLink link = iterator.next();
            if (link.getMinecraftUuid().equals(offlinePlayer.getUniqueId())) {
                iterator.remove();
                AccountLink.save();
            }
        }
    }

    public static void verifyLink(Member member, String playerName) {
        AccountLink link = getLinkByDiscord(member);

        if (link == null) {
            return;
        }
        
        if (link.getOfflinePlayer().getName().equals(playerName)) {
            link.verify();
        }
    }

    public static void initialize() {
        List<Map<?,?>> listmap = DiscordCraft.instance().getBotConfigManager().getConfig().getMapList(ACCOUNT_LINKS);

        if (listmap == null) {
            return;
        }

        for (Map<?,?> map : listmap) {
            links.add(new AccountLink(
                (long) map.get(DISCORD_ID),
                UUID.fromString((String) map.get(MINECRAFT_UUID)),
                (boolean) map.get(VERIFIED)
            ));
        }
    }

    public static void save() {
        List<Map<?,?>> listmap = DiscordCraft.instance().getBotConfigManager().getConfig().getMapList(ACCOUNT_LINKS);

        if (listmap == null) {
            listmap = new ArrayList<>();
        }

        for (AccountLink link : links) {
            listmap.add(Map.of(
                DISCORD_ID, link.getDiscordId(),
                MINECRAFT_UUID, link.getMinecraftUuid().toString(),
                VERIFIED, link.isVerified()
            ));
        }

        DiscordCraft.instance().getBotConfigManager().getConfig().set(ACCOUNT_LINKS, listmap);

        DiscordCraft.instance().getBotConfigManager().saveConfig();
    }

}