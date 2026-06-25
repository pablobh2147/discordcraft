/*
 * This file is part of DiscordCraft.
 *
 * Copyright (c) 2025 Pablo Bermejo Hernández
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.pablobh.discordcraft.discord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.configuration.ConfigurationSection;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DiscordCommand {

    // Command status

    private boolean enabled = true;
    private boolean global = false;

    // Basic command information

    private String name;
    private String description = "";
    private String help = "";

    // Command restrictions

    private boolean isAdministratorOnly = false;

    // Command data

    private Collection<OptionData> options = new ArrayList<>();
    private Collection<SubcommandData> subcommands = new ArrayList<>();

    // Command Configuration

    private final ConfigurationSection config;
    private final MessageService messageService;

    // Constructors

    public DiscordCommand(@Nonnull String name, @Nonnull DiscordCommandManager manager) {
        this(name, manager.getCommandConfig(name), manager.getMessageService());
    }

    public DiscordCommand(@Nonnull String name, @Nullable ConfigurationSection config, @Nonnull MessageService messageService) {
        Objects.requireNonNull(name, "Command name cannot be null");

        this.name = name;
        this.config = config;
        this.messageService = messageService;

        enabled = getConfig().getBoolean("enabled", true);
        description = getConfig().getString("description", null);
        help = getConfig().getString("help", null);

        isAdministratorOnly = getConfig().getBoolean("admin-only", false);
    }

    // Configuration methods

    protected final ConfigurationSection getConfig() {
        return config;
    }

    protected final MessageService getMessageService() {
        return messageService;
    }

    protected final String getMessageKey(@Nonnull String key) {
        return "commands." + name + "." + key;
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
