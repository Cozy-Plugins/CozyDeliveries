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

package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozydeliveries.configuration.ContentConfigurationDirectory;
import com.github.cozyplugins.cozydeliveries.configuration.EventConfigurationDirectory;
import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the cozy deliveries api interface.
 * Contains the methods that can be accessed
 * by other plugins.
 * <p>
 * To get the instance of the api you can use
 * the method {@link CozyDeliveries#getAPI()}.
 */
public interface CozyDeliveriesAPI {

    /**
     * Used to get the plugin's main configuration file.
     *
     * @return The config file.
     */
    @NotNull Configuration getConfiguration();

    /**
     * Used to get the instance of the database
     * used to store the deliveries.
     * Be careful not to call this method before
     * the plugin has started.
     *
     * @return The instance of the database.
     */
    @NotNull Database getDatabase();

    /**
     * Used to get the instance of the content
     * configuration directory.
     * This can be used to get the instance of
     * defined delivery content in the plugin.
     *
     * @return The content configuration directory.
     */
    @NotNull ContentConfigurationDirectory getContentConfiguration();

    /**
     * Used to get the instance of the event
     * configuration directory.
     * This contains all the events that will occur on
     * the server regarding deliveries.
     * For example, daily deliveries.
     *
     * @return The event configuration directory.
     */
    @NotNull EventConfigurationDirectory getEventConfiguration();

    /**
     * Used to get the instance of a specific delivery
     * from the database.
     * This will also check if the database is enabled.
     * If the delivery is expired it will return an empty
     * optional and remove it from the database.
     *
     * @param uuid The delivery's identifier.
     * @return The optional delivery.
     */
    @NotNull Optional<Delivery> getDelivery(@NotNull UUID uuid);

    /**
     * Used to get the list of all the
     * deliveries.
     * Be careful using this method as it interacts
     * with the database.
     * This will also check if the database is enabled.
     * If the delivery is expired it removes it from the
     * database and this list.
     *
     * @return The list of deliveries.
     * Empty list if the database is disabled.
     */
    @NotNull List<Delivery> getDeliveryList();

    /**
     * Used to get a player's list of deliveries.
     * This will also check if the database is enabled.
     * If the delivery is expired it removes it from the
     * database and this list.
     *
     * @param playerUuid The player's uuid.
     * @return The list of deliveries that has
     * been sent to the player.
     * Empty list if the database is disabled.
     */
    @NotNull List<Delivery> getDeliveryList(@NotNull UUID playerUuid);

    /**
     * Used to send a delivery to a player.
     *
     * @param delivery The instance of a delivery.
     * @return True if the delivery was sent.
     */
    boolean sendDelivery(@NotNull Delivery delivery);

    /**
     * Used to send a delivery to a player.
     *
     * @param playerUuid The player's uuid to send to.
     * @param fromName   The name of the sender.
     * @param items      The list of items to send.
     * @return True if the delivery was sent.
     */
    boolean sendDelivery(@NotNull UUID playerUuid, @Nullable String fromName, @NotNull CozyItem... items);

    /**
     * Used to send a delivery to a player.
     *
     * @param playerUuid The player's uuid to send to.
     * @param fromName   The name of the sender.
     * @param itemList   The list of items to send.
     * @return True if the delivery was sent.
     */
    boolean sendDelivery(@NotNull UUID playerUuid, @Nullable String fromName, @NotNull List<CozyItem> itemList);

    /**
     * Used to remove a delivery from the database
     * if it is expired.
     *
     * @param delivery The instance of the delivery.
     * @return The optional delivery if it hasn't expired.
     */
    @NotNull Optional<Delivery> removeIfExpired(@NotNull Delivery delivery);

    /**
     * Removes the expired deliveries from the database.
     *
     * @param deliveryList The list of deliveries.
     * @return The deliveries that have not expired.
     */
    @NotNull List<Delivery> removeExpiredDeliveries(@NotNull List<Delivery> deliveryList);
}
