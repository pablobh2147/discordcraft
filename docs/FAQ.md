# Frequently Asked Questions (FAQ)

Common questions about DiscordCraft.

## General Questions

### What is DiscordCraft?

DiscordCraft is a plugin/mod that bridges your Minecraft server with Discord, allowing bidirectional chat, player event notifications, and server management through Discord slash commands.

### Which platforms are supported?

- **Spigot** and forks (Paper, Purpur, etc.) — Minecraft 1.20.5+
- **NeoForge** — Minecraft 1.20.5+

### Is it free?

Yes! DiscordCraft is completely free and open-source under the MIT License.

### Can I use it on older Minecraft versions?

No, DiscordCraft requires Minecraft 1.20.5 or higher due to modern API requirements and dependencies.

### Does it work on Fabric?

Not yet. Fabric support is planned for future releases. Currently only Spigot/Paper/Purpur and NeoForge are supported.

---

## Setup Questions

### Do I need a Discord bot?

Yes, you need to create your own Discord bot through the [Discord Developer Portal](https://discord.com/developers/applications). See the [Installation Guide](../INSTALL.md) for step-by-step instructions.

### Can I use an existing bot?

Technically yes, but it's **not recommended**. DiscordCraft takes full control of the bot and may conflict with other bot functionality.

### How do I get a bot token?

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application
3. Go to the Bot tab
4. Click "Reset Token" and copy it
5. Paste it in your `bot.yml` file

See the [Installation Guide](../INSTALL.md) for detailed instructions.

### Can I run multiple servers with one bot?

No, each Minecraft server needs its own Discord bot. The bot can only be linked to one Minecraft server at a time.

---

## Feature Questions

### Can I link multiple Discord channels?

Yes! You can link as many Discord channels as you want using `/link add #channel`. Each channel can have different message filters.

### Can I customize the messages?

Yes! All messages are fully customizable in `messages.yml`. You can change text, colors, and use placeholders. See the [Configuration Guide](CONFIGURATION.md#message-customization).

### Can I disable certain commands?

Yes! Each command can be individually enabled/disabled in `commands.yml`. You can also restrict commands to administrators only.

### Can I see player skins in Discord?

Yes! Player avatars are automatically displayed in Discord messages. You can choose between `body`, `bust`, or `face` styles in `config.yml`.

---

## Permission Questions

### What Discord permissions does the bot need?

Minimum required permissions:
- Send Messages
- Read Message History
- Use Slash Commands
- Manage Messages (for editing/deleting)
- Embed Links (for rich formatting)
- Manage Webhooks (for sending messages with player avatars)

### Who can use admin commands?

By default, only Discord users with the **Administrator** permission can use admin commands like `/ban`, `/whitelist`, `/stop`, etc. This can be configured per-command in `commands.yml`.

### Can I restrict commands to specific roles?

Not directly through DiscordCraft. However, you can use Discord's built-in slash command permissions to restrict commands to specific roles or users.

---

## Technical Questions

### What Java version do I need?

Java 21 or higher is required for both Spigot and NeoForge versions.

### Does it affect server performance?

DiscordCraft has minimal performance impact. It uses asynchronous processing for Discord communication to avoid blocking the main server thread.

### Is my bot token secure?

Your bot token can be stored in `bot.yml` or as an environment variable. Make sure to:
- Never share your `bot.yml` file or expose the environment variable
- Never commit tokens to public repositories
- Keep your server files secure

**For better security**, use the `DISCORDCRAFT_BOT_TOKEN` environment variable instead of storing the token in `bot.yml`. This is especially recommended for production servers and containerized deployments.

### Can I use it with BungeeCord/Velocity?

DiscordCraft is designed for individual servers. For proxy networks, you'll need to install it on each backend server. Cross-server chat is not currently supported.

### Does it work with other plugins/mods?

Yes! DiscordCraft is designed to be compatible with most plugins and mods. If you encounter conflicts, please report them on [GitHub](https://github.com/pablobh2147/discordcraft/issues).

---

## Configuration Questions

### Where are the config files?

- **Spigot/Paper/Purpur:** `plugins/DiscordCraft/`
- **NeoForge:** `config/DiscordCraft/`

### What config files are there?

- `bot.yml` — Bot token and Discord server settings
- `config.yml` — Default channel settings and avatar style
- `messages.yml` — All message templates
- `commands.yml` — Command configuration

### How do I reset my configuration?

1. Stop the server
2. Delete the config files you want to reset
3. Start the server (new defaults will be generated)
4. For `bot.yml`, you'll need to run `/setup` again

### Can I reload config without restarting?

Yes! You can reload the configuration by running `/config reload` in Discord. This will reload all configuration files without restarting the server.

**Note:** You need Administrator permission in Discord to use this command.

---

## Troubleshooting

For help with common issues, see the **[Troubleshooting Guide](TROUBLESHOOTING.md)** which covers:
- Bot connection problems
- Message delivery issues
- Command problems
- Platform-specific issues

---

## Development Questions

### Is it open source?

Yes! The source code is available on [GitHub](https://github.com/pablobh2147/discordcraft) under the MIT License.

### Can I contribute?

Absolutely! Contributions are welcome. Please:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

See the repository for contribution guidelines.

### How do I build from source?

```bash
git clone https://github.com/pablobh2147/discordcraft.git
cd discordcraft
./gradlew build
```

Built JARs will be in `spigot/build/libs/` and `neoforge/build/libs/`.

### Where do I report bugs?

Open an issue on [GitHub](https://github.com/pablobh2147/discordcraft/issues). Please include:
- Platform and version
- Minecraft version
- Java version
- Error messages from logs
- Steps to reproduce

---

## Still have questions?

- Check the [Installation Guide](../INSTALL.md)
- Check the [Configuration Guide](CONFIGURATION.md)
- Check the [Troubleshooting Guide](TROUBLESHOOTING.md)
- Search [existing issues](https://github.com/pablobh2147/discordcraft/issues)
- Open a [new issue](https://github.com/pablobh2147/discordcraft/issues/new)
