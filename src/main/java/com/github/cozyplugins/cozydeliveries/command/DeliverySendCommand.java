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
import com.github.cozyplugins.cozylibrary.command.command.CommandType;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandArguments;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandStatus;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandSuggestions;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandTypePool;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ConfirmAction;
import com.github.cozyplugins.cozylibrary.inventory.inventory.ConfirmationInventory;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Represents the delivery send command.
 * Used to send a delivery to another player.
 */
public class DeliverySendCommand implements CommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "send";
    }

    @Override
    public @Nullable String getSyntax() {
        return "/[parent] [name] <player>";
    }

    @Override
    public @Nullable String getDescription() {
        return "Used to send a delivery to a player.";
    }

    @Override
    public @Nullable CommandTypePool getSubCommandTypes() {
        return null;
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return new CommandSuggestions().append(Stream.of(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayer(@NotNull PlayerUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Check if the database is disabled.
        if (CozyDeliveries.getAPI().orElseThrow().getDatabase().isDisabled()) {
            user.sendMessage(section.getString("database_disabled", "&7The database is currently disabled. This could be an error."));
            return new CommandStatus();
        }

        // Check if they have provided any arguments.
        if (arguments.getArguments().isEmpty() || arguments.getArguments().get(0).isEmpty()) {

            // Start the creation of a delivery.
            CozyDeliveries.getAPI().orElseThrow().createDelivery(user.getPlayer());
            return new CommandStatus();
        }

        // Check if they have specified an offline player.
        if (!Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .toList()
                .contains(arguments.getArguments().get(0))) {

            user.sendMessage(section.getString("incorrect_arguments_player", "&7Incorrect arguments. &e" + this.getSyntax()));
            return new CommandStatus();
        }

        // Get the player to send the delivery to.
        final OfflinePlayer player = Bukkit.getOfflinePlayer(arguments.getArguments().get(0));

        // Check if they have selected them self.
        if (player.getUniqueId().equals(user.getUuid())) {
            user.sendMessage(section.getString("chose_self", "&7You cannot choose your self."));
            return new CommandStatus();
        }

        // Create the delivery.
        CozyDeliveries.getAPI().orElseThrow().createDelivery(user.getPlayer(), player.getUniqueId());
        return new CommandStatus();
    }

    @Override
    public @Nullable CommandStatus onFakeUser(@NotNull FakeUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onConsole(@NotNull ConsoleUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }
}
