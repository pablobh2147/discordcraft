# Configuration Guide

This document covers the detailed configuration of the DiscordCraft plugin, including all configuration files, available commands, and channel linking.

## Table of Contents

- [Configuration Files](#configuration-files)
  - [bot.yml](#botyml)
  - [config.yml](#configyml)
  - [messages.yml](#messagesyml)
  - [commands.yml](#commandsyml)
- [Discord Commands](#discord-commands)
  - [/setup](#setup)
  - [/link](#link)
  - [/playerlist](#playerlist)
  - [/ban](#ban)
  - [/pardon](#pardon)
  - [/whitelist](#whitelist)
  - [/stop](#stop)
  - [/help](#help)
- [Channel Linking](#channel-linking)
  - [Adding a Channel](#adding-a-channel)
  - [Removing a Channel](#removing-a-channel)
  - [Configuring a Channel](#configuring-a-channel)
- [Message Customization](#message-customization)
  - [Placeholders](#placeholders)

---

## Configuration Files

Configuration files are located in:
- **Spigot/Paper/Purpur:** `plugins/DiscordCraft/`
- **NeoForge:** `config/DiscordCraft/`

### bot.yml

The main bot configuration file. This file is created on first run and updated automatically by the `/setup` command.

| Key | Type | Description |
|---|---|---|
| `token` | string | Your Discord bot token |
| `activity.show` | boolean | Whether to show the bot activity status |
| `activity.type` | string | Activity type: `PLAYING`, `WATCHING`, `LISTENING`, `COMPETING` |
| `activity.name` | string | The activity text displayed in the bot status |
| `guild` | long | The Discord server (guild) ID — set automatically by `/setup` |
| `log-channel` | long | Channel ID for error/info logging — set via `/setup` |
| `channels` | section | Linked channels configuration — managed via `/link` commands |

> **Note:** The `guild` and `channels` fields are managed automatically. You should only manually edit `token`, `activity`, and `log-channel`.

#### Environment Variable

Instead of storing the token in `bot.yml`, you can use the `DISCORDCRAFT_BOT_TOKEN` environment variable:

```bash
export DISCORDCRAFT_BOT_TOKEN='YOUR_BOT_TOKEN_HERE'
```

The environment variable takes precedence over the `bot.yml` file. This is more secure and recommended for production deployments.

### config.yml

Default settings applied to newly linked channels. Each option controls which event types are forwarded to Discord.

```yaml
avatar-style: bust

channel-defaults:
  minecraft-chat-messages: true    # Minecraft chat → Discord
  player-join-messages: true       # Player join notifications
  player-leave-messages: true      # Player leave notifications
  player-death-messages: true      # Player death notifications
  player-murder-messages: true     # PvP kill notifications
  discord-messages: true           # Discord → Minecraft
  discord-bot-messages: true       # Forward bot messages
  discord-system-messages: true    # Forward system messages
  server-start: true               # Server start announcement
  server-stop: true                # Server stop announcement
```

#### Avatar Style

The `avatar-style` option controls how player avatars are displayed in Discord messages. Three styles are available:

| Style | Description | Preview |
|---|---|---|
| `body` | Full player body | ![Body Style](img/avatar_body.webp) |
| `bust` | Player head and torso (default) | ![Bust Style](img/avatar_bust.webp) |
| `face` | Player head only | ![Face Style](img/avatar_face.webp) |

**Example:**
```yaml
avatar-style: face
```

#### Channel Defaults

These defaults are applied when a new channel is linked. Each linked channel can override these values individually via the `/link config` command.

### messages.yml

All messages sent by the bot are fully customizable. The file is organized into sections:

- **`chat`** — Chat message formats (Minecraft → Discord and Discord → Minecraft)
- **`player`** — Join, leave, death, and murder messages
- **`server`** — Server start/stop announcements
- **`commands`** — Error and feedback messages for commands
- **`errors`** — Internal error messages
- **`setup`** — Setup flow messages
- **`custom-death-messages`** — Death cause-specific messages (lava, fire, fall, void, etc.)

See [Message Customization](#message-customization) for details on available placeholders.

### commands.yml

Controls each Discord slash command's behavior. Every command supports these common fields:

| Field | Type | Description |
|---|---|---|
| `enabled` | boolean | Whether the command is registered and available |
| `command` | string | The slash command name in Discord |
| `description` | string | Command description shown in Discord |
| `help` | string | Help text shown in the `/help` command |
| `admin-only` | boolean | Restrict to users with administrator permissions |
| `is-ephemeral` | boolean | Whether the response is only visible to the user who ran the command |

Some commands have additional fields (e.g., `delay` for `/stop`, `lists` for `/playerlist`, `messages` for `/ban`). See the default file for all available options.

---

## Discord Commands

All commands are slash commands registered automatically when the bot starts.

### /setup

Performs the initial server setup. Links the Discord server to the plugin and optionally configures the bot activity and log channel.

**Options:**
- `log-channel` — A text channel for error/info logging
- `activity-type` — Bot status type (Playing, Watching, etc.)
- `activity-name` — Bot status text
- `show-activity` — Toggle the bot activity on/off

> This command can only be run **once**. To reconfigure, delete `bot.yml` and restart the server.

**Requires:** Administrator

### /link

Manages linked channels. Linked channels are Discord text channels that receive Minecraft events and relay Discord messages back to the game.

**Subcommands:**

- `/link add [channel]` — Link a channel (defaults to the current channel)
- `/link remove [channel]` — Unlink a channel
- `/link config <option> <value> [channel]` — Configure what a channel receives

**Requires:** Administrator

### /playerlist

Displays lists of players.

**Available lists:**
- Online players
- Whitelisted players
- Operators
- Banned players

Each list can be individually enabled/disabled in `commands.yml`.

### /ban

Bans a player from the Minecraft server.

**Options:**
- `player` — The player name to ban
- `reason` — Ban reason (optional, defaults to configured message)

**Requires:** Administrator

### /pardon

Unbans a player from the Minecraft server.

**Options:**
- `player` — The player name to unban

**Requires:** Administrator

### /whitelist

Manages the server whitelist.

**Subcommands:**
- Add/remove players from the whitelist
- Enable/disable the whitelist

**Requires:** Administrator

### /stop

Stops the Minecraft server with a configurable countdown delay.

The delay (in seconds) and the countdown message can be configured in `commands.yml`. A title is optionally shown to online players.

**Requires:** Administrator

### /help

Lists all available commands and their descriptions.

---

## Channel Linking

Channel linking is the core feature that connects Discord text channels to your Minecraft server.

### Adding a Channel

```
/link add #my-channel
```

Or run `/link add` in the channel you want to link (no argument needed).

When a channel is linked, it uses the defaults from `config.yml`. You can then customize it per-channel.

### Removing a Channel

```
/link remove #my-channel
```

### Configuring a Channel

```
/link config <option> <value> [#channel]
```

**Available options:**

| Option | Description |
|---|---|
| `Everything` | Toggle all options at once |
| `Minecraft Chat` | Minecraft chat messages |
| `Player Join` | Player join notifications |
| `Player Leave` | Player leave notifications |
| `Player Death` | Player death notifications |
| `Player Murder` | PvP kill notifications |
| `Discord Chat` | Discord → Minecraft relay |
| `Bot Messages` | Forward bot messages |
| `System Messages` | Forward system messages |
| `Server Start` | Server start announcements |
| `Server Stop` | Server stop announcements |

**Values:** `true`, `false`, or `default` (resets to the value in `config.yml`)

**Example:**

```
/link config "Player Death" false #minecraft-chat
```

This disables death notifications in the `#minecraft-chat` channel.

---

## Message Customization

All messages in `messages.yml` support Minecraft color codes (using `&`) and custom placeholders.

### Placeholders

**Chat messages:**
- `%guild%` — Discord server name
- `%channel%` — Discord channel name
- `%username%` — Discord username
- `%message%` — Message content
- `%attachments%` — Attached files
- `%player_name%` — Minecraft player name

**Player events:**
- `%player_name%` — Player name
- `%death_message%` — Death message
- `%killer_name%` — Killer name (for murder events)
- `%victim_name%` — Victim name (for murder events)

**Commands:**
- `%command_name%` — Command that was executed
- `%member_mention%` — Discord mention of the user
- `%player%` — Target player name
- `%reason%` — Ban reason
- `%list_name%` — Player list name
- `%player_count%` — Number of players in a list
- `%seconds%` — Countdown seconds (for `/stop`)

**Example customization:**

```yaml
player:
  join: '**%player_name%** joined the server!'
  death: '%player_name% died: `%death_message%`'
```
