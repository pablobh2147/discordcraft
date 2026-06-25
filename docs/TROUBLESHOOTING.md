# Troubleshooting Guide

This guide covers common issues and their solutions when using DiscordCraft.

## Table of Contents

- [Bot Issues](#bot-issues)
- [Connection Issues](#connection-issues)
- [Message Issues](#message-issues)
- [Command Issues](#command-issues)
- [Platform-Specific Issues](#platform-specific-issues)

---

## Bot Issues

### Bot doesn't come online

**Symptoms:** The bot appears offline in Discord after starting the server.

**Solutions:**

1. **Check your bot token:**
   - Verify the token in `bot.yml` is correct
   - Make sure there are no extra spaces or quotes
   - Try regenerating the token in the Discord Developer Portal

2. **Check intents:**
   - Go to Discord Developer Portal → Your Application → Bot
   - Enable **Server Members Intent** and **Message Content Intent**

3. **Check server logs:**
   - Look for error messages in the server console
   - Common errors: `Invalid token`, `Connection refused`, `Unauthorized`

### Bot comes online but commands don't appear

**Symptoms:** Bot is online but slash commands aren't showing up.

**Solutions:**

1. **Wait a few minutes:**
   - Discord can take up to 1 hour to register slash commands globally
   - Try in a different Discord server to test

2. **Check bot permissions:**
   - Ensure the bot has `applications.commands` scope
   - Re-invite the bot using the OAuth2 URL generator

3. **Run /setup again:**
   - Delete `bot.yml` and restart the server
   - Run `/setup` to re-register commands

### Bot keeps disconnecting

**Symptoms:** Bot goes offline and reconnects repeatedly.

**Solutions:**

1. **Check network connection:**
   - Ensure your server has a stable internet connection
   - Check firewall settings

2. **Check rate limits:**
   - You may be sending too many messages too quickly
   - Reduce message frequency in linked channels

---

## Connection Issues

### "Guild not found" error

**Symptoms:** Error message about guild/server not being found.

**Solutions:**

1. **Run /setup:**
   - The bot needs to be linked to your Discord server first
   - Run `/setup` in your Discord server

2. **Check bot.yml:**
   - Verify the `guild` field has the correct server ID
   - Delete `bot.yml` and run `/setup` again if needed

### Messages not appearing in Discord

**Symptoms:** Minecraft chat messages don't show up in Discord.

**Solutions:**

1. **Check channel linking:**
   - Run `/link add` in the Discord channel you want to use
   - Verify the channel is linked with `/playerlist` or check `bot.yml`

2. **Check channel configuration:**
   - Run `/link config "Minecraft Chat" true #your-channel`
   - Verify `minecraft-chat-messages` is enabled for that channel

3. **Check bot permissions:**
   - Bot needs `Send Messages` and `Embed Links` permissions
   - Check Discord channel permissions

### Messages not appearing in Minecraft

**Symptoms:** Discord messages don't show up in Minecraft chat.

**Solutions:**

1. **Check channel configuration:**
   - Run `/link config "Discord Chat" true #your-channel`
   - Verify `discord-messages` is enabled

2. **Check message format:**
   - Verify `messages.yml` has valid formatting
   - Check for syntax errors in the YAML file

---

## Message Issues

### Messages have broken formatting

**Symptoms:** Color codes or placeholders appear as raw text.

**Solutions:**

1. **For Minecraft messages:**
   - Use `&` for color codes (e.g., `&a` for green)
   - Check `messages.yml` for proper placeholder syntax

2. **For Discord messages:**
   - Discord uses Markdown, not Minecraft color codes
   - Use `**bold**`, `*italic*`, `` `code` `` formatting

### Placeholders not working

**Symptoms:** Placeholders like `%player_name%` appear as-is instead of being replaced.

**Solutions:**

1. **Check placeholder spelling:**
   - Verify exact placeholder names in the [Configuration Guide](CONFIGURATION.md#placeholders)
   - Placeholders are case-sensitive

2. **Check message type:**
   - Some placeholders only work in specific message types
   - Example: `%killer_name%` only works in murder messages

---

## Command Issues

### Commands return "You don't have permission"

**Symptoms:** Discord users can't run admin commands.

**Solutions:**

1. **Check Discord permissions:**
   - User needs **Administrator** permission in Discord
   - Or assign them a role with Administrator

2. **Check commands.yml:**
   - Verify `admin-only: true` for admin commands
   - Set to `false` if you want everyone to use them (not recommended)

### /setup says "Already configured"

**Symptoms:** Can't run `/setup` again after initial setup.

**Solutions:**

1. **This is intentional:**
   - `/setup` can only be run once for security
   - To reconfigure, follow these steps:

2. **Reset configuration:**
   - Stop the server
   - Delete `bot.yml` (or `config/DiscordCraft/bot.yml` for NeoForge)
   - Start the server
   - Run `/setup` again

### /stop command doesn't work

**Symptoms:** Server doesn't stop when using `/stop` command.

**Solutions:**

1. **Check commands.yml:**
   - Verify `enabled: true` for the stop command
   - Check the `delay` setting

2. **Check permissions:**
   - User needs Administrator permission in Discord

---

## Platform-Specific Issues

### Spigot/Paper/Purpur

#### Plugin fails to load

**Solutions:**

1. **Check Java version:**
   - Requires Java 21 or higher
   - Run `java -version` to check

2. **Check Minecraft version:**
   - Requires Minecraft 1.20.5 or higher
   - Update your server if needed

3. **Check dependencies:**
   - No external dependencies required
   - Check for conflicting plugins

#### Config files not generating

**Solutions:**

1. **Check folder permissions:**
   - Server needs write access to `plugins/` folder
   - Check file system permissions

2. **Check for errors:**
   - Look for errors in server console
   - Check `logs/latest.log`

### NeoForge

#### Mod fails to load

**Solutions:**

1. **Check NeoForge version:**
   - Requires NeoForge 26.1.0 or higher
   - Update NeoForge if needed

2. **Check Minecraft version:**
   - Requires Minecraft 1.20.5 or higher

3. **Check mods folder:**
   - Ensure the JAR is in the `mods/` folder
   - Remove any old versions

#### Config files in wrong location

**Solutions:**

1. **NeoForge uses different path:**
   - Configs are in `config/DiscordCraft/`, not `plugins/`
   - Check the correct folder

---

## Still Having Issues?

If none of these solutions work:

1. **Check the logs:**
   - Spigot: `logs/latest.log`
   - NeoForge: `logs/latest.log` or `logs/debug.log`

2. **Enable debug logging:**
   - Look for detailed error messages
   - Include these in your bug report

3. **Report the issue:**
   - Open an issue on [GitHub](https://github.com/pablobh2147/discordcraft/issues)
   - Include:
     - Platform (Spigot/NeoForge) and version
     - Minecraft version
     - Java version
     - Full error message from logs
     - Steps to reproduce

4. **Check existing issues:**
   - Someone may have already reported the same problem
   - Look for solutions in closed issues
