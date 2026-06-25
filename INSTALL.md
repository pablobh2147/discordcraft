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
   - Under **Bot Permissions**, select at minimum: `Send Messages`, `Read Message History`, `Use Slash Commands`, `Manage Messages`, and `Manage Webhooks`.
6. Copy the generated URL and open it in your browser to **invite the bot** to your Discord server.

## Step 2: Download or Build the Plugin

### Option A: Download a Release (Recommended)

Download the latest version for your platform:

- **[GitHub Releases](https://github.com/pablobh2147/discordcraft/releases)** — All platforms
- **[Modrinth](https://modrinth.com/project/discordcraft)** — Spigot and NeoForge
- **[CurseForge](https://www.curseforge.com/minecraft/bukkit-plugins/discordcraft)** — Spigot and NeoForge

Download the appropriate `DiscordCraft-<platform>-x.x.x.jar` file for your server.

### Option B: Build from Source (Advanced)

```bash
git clone https://github.com/pablobh2147/discordcraft.git
cd discordcraft
./gradlew build
```

The build produces:
- `spigot/build/libs/DiscordCraft-Spigot-<version>.jar` — Spigot/Paper/Purpur plugin
- `neoforge/build/libs/DiscordCraft-NeoForge-<version>.jar` — NeoForge mod
- `common/build/libs/common-<version>.jar` — Shared library (not needed for installation)

Use the platform-specific JAR file for your server.

## Step 3: Install the Plugin/Mod

### For Spigot/Paper/Purpur:
1. Copy the `DiscordCraft-Spigot-<version>.jar` file into your server's `plugins/` folder.
2. Start (or restart) the server. The plugin will generate its configuration files in `plugins/DiscordCraft/` and then **disable itself** because no bot token is set yet.

### For NeoForge:
1. Copy the `DiscordCraft-NeoForge-<version>.jar` file into your server's `mods/` folder.
2. Start (or restart) the server. The mod will generate its configuration files in `config/DiscordCraft/` and then **disable itself** because no bot token is set yet.

## Step 4: Configure the Bot Token

You can configure the bot token in two ways:

### Option A: Configuration File (Recommended)

**For Spigot/Paper/Purpur:**
1. Open `plugins/DiscordCraft/bot.yml`.
2. Paste your bot token in the `token` field:
   ```yaml
   token: 'YOUR_BOT_TOKEN_HERE'
   ```
3. Save the file and **restart the server**. The bot should now come online in Discord.

**For NeoForge:**
1. Open `config/DiscordCraft/bot.yml`.
2. Paste your bot token in the `token` field:
   ```yaml
   token: 'YOUR_BOT_TOKEN_HERE'
   ```
3. Save the file and **restart the server**. The bot should now come online in Discord.

### Option B: Environment Variable

Alternatively, you can set the bot token using an environment variable:

```bash
export DISCORDCRAFT_BOT_TOKEN='YOUR_BOT_TOKEN_HERE'
```

Then start your server. The environment variable takes precedence over the `bot.yml` file.

**Benefits:**
- More secure (token not stored in a file)
- Easier for containerized deployments (Docker, Kubernetes)
- Better for CI/CD pipelines

## Step 5: Run the Setup Command

Once the bot is online, go to your Discord server and run:

```
/setup
```

This slash command will automatically link your Discord server to the plugin and perform the initial configuration. The Minecraft server will restart to apply the changes.

After the restart, the plugin is ready to use. You can link channels, configure messages, and manage your server from Discord.

## Next Steps

For detailed configuration, available commands, channel linking, and message customization, see the **[Configuration Guide](docs/CONFIGURATION.md)**.
