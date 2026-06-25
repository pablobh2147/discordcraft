# Installation Guide

## Requirements

- **Java** 21 or higher
- **Minecraft server** running Spigot or any Spigot fork (Paper, Purpur, etc.) — version **1.20.5+**
- **Or** NeoForge — version **1.20.5+**
- A **Discord bot token** ([create one here](https://discord.com/developers/applications))

## Step 1: Create a Discord Bot

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications) and click **New Application**.
2. Give it a name (e.g. `DiscordCraft`) and click **Create**.
3. Go to the **Bot** tab on the left sidebar and click **Reset Token** to generate a new token. **Copy it** — you will need it later.
4. Under **Privileged Gateway Intents**, enable:
   - **Server Members Intent**
   - **Message Content Intent**
5. Go to the **OAuth2** tab, then **URL Generator**:
   - Under **Scopes**, select `bot` and `applications.commands`.
   - Under **Bot Permissions**, select at minimum: `Send Messages`, `Read Message History`, `Use Slash Commands`, and `Manage Messages`.
6. Copy the generated URL and open it in your browser to **invite the bot** to your Discord server.

## Step 2: Download or Build the Plugin

**Option A — Download a release**

Download the latest `DiscordCraft-<platform>-x.x.x.jar` from the [Releases page](https://github.com/pablobh2147/discordcraft/releases).

**Option B — Build from source**

```bash
git clone https://github.com/pablobh2147/discordcraft.git
cd discordcraft
./gradlew build
```

The build produces:
- `common/build/libs/common-<version>.jar` — platform-agnostic shared library
- `<platform>/build/libs/DiscordCraft-<platform>-<version>.jar` — the platform-specific plugin/mod

Use the `<platform>/...` JAR for the `<platform>` server.

## Step 3: Install the Plugin/Mod

### For Spigot/Paper/Purpur:
1. Copy the `DiscordCraft-Spigot-<version>.jar` file into your server's `plugins/` folder.
2. Start (or restart) the server. The plugin will generate its configuration files in `plugins/DiscordCraft/` and then **disable itself** because no bot token is set yet.

### For NeoForge:
1. Copy the `DiscordCraft-NeoForge-<version>.jar` file into your server's `mods/` folder.
2. Start (or restart) the server. The mod will generate its configuration files in `config/DiscordCraft/` and then **disable itself** because no bot token is set yet.

## Step 4: Configure the Bot Token

### For Spigot/Paper/Purpur:
1. Open `plugins/DiscordCraft/bot.yml`.
2. Paste your bot token in the `token` field:
   ```yaml
   token: 'YOUR_BOT_TOKEN_HERE'
   ```
3. Save the file and **restart the server**. The bot should now come online in Discord.

### For NeoForge:
1. Open `config/DiscordCraft/bot.yml`.
2. Paste your bot token in the `token` field:
   ```yaml
   token: 'YOUR_BOT_TOKEN_HERE'
   ```
3. Save the file and **restart the server**. The bot should now come online in Discord.

## Step 5: Run the Setup Command

Once the bot is online, go to your Discord server and run:

```
/setup
```

This slash command will automatically link your Discord server to the plugin and perform the initial configuration. The Minecraft server will restart to apply the changes.

After the restart, the plugin is ready to use. You can link channels, configure messages, and manage your server from Discord.

## Next Steps

For detailed configuration, available commands, channel linking, and message customization, see the **[Configuration Guide](docs/CONFIGURATION.md)**.
