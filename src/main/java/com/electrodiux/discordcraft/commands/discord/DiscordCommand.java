package com.electrodiux.discordcraft.commands.discord;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.configuration.ConfigurationSection;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DiscordCommand {

    // Command status

    private boolean enabled = true;
    private boolean global = false;

    // Basic command information

    private String name = "";
    private String description = "";
    private String help = "";

    // Command restrictions

    private boolean isAdministratorOnly = false;

    // Command data

    private Collection<OptionData> options = new ArrayList<>();
    private Collection<SubcommandData> subcommands = new ArrayList<>();

    // Command Configuration

    private ConfigurationSection config = null;

    // Constructors

    public DiscordCommand(String configName) {

        // Initialize the configuration

        config = DiscordCraft.instance().getDiscordCommandsConfigManager().getConfig().getConfigurationSection("commands." + configName);

        if (config == null) {
            throw new IllegalArgumentException("Configuration section not found for command: " + configName);
        }

        // Load the configuration

        enabled = getConfig().getBoolean("enabled", true);

        name = getConfig().getString("command", configName.replaceAll("[^A-Za-z0-9 -]", "").toLowerCase());
        description = getConfig().getString("description", null);
        help = getConfig().getString("help", null);

        isAdministratorOnly = getConfig().getBoolean("admin-only", false);

    }

    public DiscordCommand(String name, String description, String help, boolean enabled, boolean isAdministratorOnly) {

        this.config = null;

        this.enabled = enabled;

        this.name = name;
        this.description = description;
        this.help = help;

        this.isAdministratorOnly = isAdministratorOnly;

    }

    // Configuration methods

    protected final ConfigurationSection getConfig() {
        return config;
    }

    // Command options

    protected final OptionData addOption(OptionData option) {
        options.add(option);
        return option;
    }

    protected final OptionData addOption(OptionType type, String name, String description, boolean required) {
        OptionData option = new OptionData(type, name, description, required);
        options.add(option);
        return option;
    }

    // Command subcommands

    protected final SubcommandData addSubcommand(SubcommandData subcommand) {
        subcommands.add(subcommand);
        return subcommand;
    }

    protected final SubcommandData addSubcommand(String name, String description) {
        SubcommandData subcommand = new SubcommandData(name, description);
        subcommands.add(subcommand);
        return subcommand;
    }

    // Getters

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getHelp() {
        return help;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final boolean isAdministratorOnly() {
        return isAdministratorOnly;
    }

    public final Collection<OptionData> getOptions() {
        return options;
    }

    public final boolean hasOptions() {
        return !options.isEmpty();
    }

    public final Collection<SubcommandData> getSubcommands() {
        return subcommands;
    }

    public final boolean hasSubcommands() {
        return !subcommands.isEmpty();
    }

    public final boolean isGlobal() {
        return global;
    }

    // Setters

    protected final void setName(String name) {
        this.name = name;
    }

    protected final void setDescription(String description) {
        this.description = description;
    }

    protected final void setHelp(String help) {
        this.help = help;
    }

    protected final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected final void setAdministratorOnly(boolean isAdministratorOnly) {
        this.isAdministratorOnly = isAdministratorOnly;
    }

    protected final void setGlobal(boolean global) {
        this.global = global;
    }

    // Abstract methods

    public abstract void onCommandInteraction(SlashCommandInteractionEvent event);

}
