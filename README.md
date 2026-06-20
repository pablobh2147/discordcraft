# DiscordCraft

[![License: MIT](https://img.shields.io/github/license/pablobh2147/discordcraft)](LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/pablobh2147/discordcraft)](https://github.com/pablobh2147/discordcraft/releases)

A Discord integration plugin and mod for Minecraft servers. Bridge your Minecraft server chat with Discord channels, manage your server from Discord with slash commands, and keep your community connected.

Currently supports Spigot and Spigot forks, with a platform-agnostic `common` core designed to support mod loaders in the future.

## Features

- **Bidirectional chat** — Minecraft chat messages appear in Discord and vice versa
- **Player events** — Join, leave, death, and kill notifications forwarded to Discord
- **Server status** — Automatic start/stop announcements in Discord
- **Discord slash commands** — Manage your server directly from Discord (`/ban`, `/pardon`, `/whitelist`, `/stop`, `/playerlist`, etc.)
- **Channel linking** — Link multiple Discord channels with individual message-type filters
- **Customizable messages** — Every message the bot sends is fully configurable via `messages.yml`
- **Customizable commands** — Enable/disable and configure each Discord command in `commands.yml`
- **Bot activity** — Show a custom status on the bot (Playing, Watching, etc.)

## Examples

### Discord setup

![Discord setup](docs/img/screenshot-discord-setup.png)

### Discord chat

![Discord chat](docs/img/screenshot-discord.png)

### Minecraft chat

![Minecraft chat](docs/img/screenshot-minecraft.png)

## Requirements

- **Java** 17 or higher
- **Minecraft server** running Spigot or any Spigot fork (Paper, Purpur, etc.) — version **1.13+** (mod loader support is planned)
- A **Discord bot token** ([create one here](https://discord.com/developers/applications))

## Installation

### 1. Create a Discord Bot

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

### 2. Download or Build the Plugin

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

### 3. Install the Plugin

1. Copy the `DiscordCraft-<platform>-<version>.jar` JAR file into your `<platform>` server's `plugins/` (or `mods/`) folder (or `<platform>/server/plugins/` for the built-in test server).
2. Start (or restart) the server. The plugin/mod will generate its configuration files and then **disable itself** because no bot token is set yet.

### 4. Configure the Bot Token

1. Open `plugins/DiscordCraft/bot.yml`.
2. Paste your bot token in the `token` field:
   ```yaml
   token: 'YOUR_BOT_TOKEN_HERE'
   ```
3. Save the file and **restart the server**. The bot should now come online in Discord.

### 5. Run the Setup Command

Once the bot is online, go to your Discord server and run:

```
/setup
```

This slash command will automatically link your Discord server to the plugin and perform the initial configuration. The Minecraft server will restart to apply the changes.

After the restart, the plugin is ready to use. You can link channels, configure messages, and manage your server from Discord.

## Documentation

For detailed configuration, available commands, channel linking, and message customization, see the **[Configuration Guide](docs/CONFIGURATION.md)**.

## Building for Development

Useful Gradle commands:

```bash
./gradlew build                # build everything
./gradlew :common:build        # build only the common library
./gradlew :<platform>:build   # build only the platform-specific plugin/mod
./gradlew :<platform>:dev     # build + deploy to <platform>/server/plugins/
./gradlew dev                  # root convenience task delegating to the active platform
```

Replace `<platform>` with `spigot`, `neoforge`, `fabric`, etc. To build and deploy to the local test server in one step, run `./gradlew dev`.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
