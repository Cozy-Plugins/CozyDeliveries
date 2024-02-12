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

package com.github.cozyplugins.cozydeliveries.delivery.event;

import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a delivery event handler.
 * Used to handle giving deliveries when
 * the event is executed.
 */
public interface DeliveryEventHandler {

    /**
     * Called when the delivery should be given.
     *
     * @param event The instance of the delivery event.
     * @param playerUuid The instance of the player uuid to give the delivery too.
     */
    void onEvent(@NotNull DeliveryEvent event, @NotNull UUID playerUuid);
}
