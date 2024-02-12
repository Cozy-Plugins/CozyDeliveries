/*
 * CozyDeliveries - An item and money delivery service for a minecraft server.
 * Copyright (C) 2024  Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.cozyplugins.cozydeliveries.command;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozylibrary.CozyLibrary;
import com.github.cozyplugins.cozylibrary.command.command.CommandType;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandArguments;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandStatus;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandSuggestions;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandTypePool;
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeliveryReloadCommand implements CommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "reload";
    }

    @Override
    public @Nullable String getSyntax() {
        return "/[name]";
    }

    @Override
    public @Nullable String getDescription() {
        return "Used to reload the plugin.";
    }

    @Override
    public @Nullable CommandTypePool getSubCommandTypes() {
        return null;
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull ConfigurationSection configurationSection, @NotNull CommandArguments commandArguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments commandArguments) {

        CozyDeliveries.getAPI().orElseThrow().getConfiguration().load();
        CozyDeliveries.getAPI().orElseThrow().getEventConfiguration().getDirectory().reload();
        CozyDeliveries.getAPI().orElseThrow().getContentConfiguration().getDirectory().reload();
        CozyLibrary.getCommandDirectory().reload();
        CozyLibrary.getCommandHandler().unregisterCommands().registerCommands();

        user.sendMessage(section.getString("message", "&7&l> &7Reloaded configuration and commands."));

        return new CommandStatus();
    }

    @Override
    public @Nullable CommandStatus onPlayer(@NotNull PlayerUser playerUser, @NotNull ConfigurationSection configurationSection, @NotNull CommandArguments commandArguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onFakeUser(@NotNull FakeUser fakeUser, @NotNull ConfigurationSection configurationSection, @NotNull CommandArguments commandArguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onConsole(@NotNull ConsoleUser consoleUser, @NotNull ConfigurationSection configurationSection, @NotNull CommandArguments commandArguments) {
        return null;
    }
}
