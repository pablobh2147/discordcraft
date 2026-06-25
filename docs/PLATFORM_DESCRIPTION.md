# DiscordCraft

**Bridge your Minecraft server with Discord!** DiscordCraft is a powerful integration plugin/mod that connects your Minecraft server to Discord, enabling seamless communication and server management.

## Features

### Communication
- **Bidirectional Chat** — Messages flow between Minecraft and Discord in real-time
- **Player Events** — Join, leave, death, and kill notifications sent to Discord
- **Server Status** — Automatic start/stop announcements
- **Channel Linking** — Link multiple Discord channels with customizable message filters

### Server Management
- **Discord Slash Commands** — Manage your server from Discord with commands like `/ban`, `/pardon`, `/whitelist`, `/stop`, `/playerlist`, and more!

### Customization
- **Fully Configurable Messages** — Customize every message via `messages.yml`
- **Command Configuration** — Enable/disable and configure each command in `commands.yml`
- **Bot Activity** — Set custom bot status (Playing, Watching, etc.)
- **Easy Setup** — Automated `/setup` command for quick configuration

## Platform Support

- **Spigot/Paper/Purpur** — Version 1.20.5+
- **NeoForge** — Version 1.20.5+

## Installation

1. **Create a Discord Bot**
   - Go to the [Discord Developer Portal](https://discord.com/developers/applications)
   - Create a new application and bot
   - Enable **Server Members Intent** and **Message Content Intent**
   - Copy your bot token

2. **Download the Plugin/Mod**
   - Download the appropriate version for your platform
   - Place it in your `plugins/` (Spigot) or `mods/` (NeoForge) folder

3. **Configure the Bot**
   - Start your server (it will generate config files)
   - Open `plugins/DiscordCraft/bot.yml` (or `config/DiscordCraft/bot.yml` for NeoForge)
   - Add your bot token
   - Restart the server

4. **Run Setup**
   - In Discord, run `/setup` to link your server
   - Follow the prompts to complete configuration

For complete step-by-step installation instructions, see the **[Installation Guide](https://github.com/pablobh2147/discordcraft/blob/main/INSTALL.md)**.

## Links

- **GitHub Repository:** https://github.com/pablobh2147/discordcraft
- **Installation Guide:** https://github.com/pablobh2147/discordcraft/blob/main/INSTALL.md
- **Configuration Guide:** https://github.com/pablobh2147/discordcraft/blob/main/docs/CONFIGURATION.md
- **Issue Tracker:** https://github.com/pablobh2147/discordcraft/issues

## License

This project is licensed under the MIT License.

---

**Need help?** Open an issue on GitHub or check the documentation!
