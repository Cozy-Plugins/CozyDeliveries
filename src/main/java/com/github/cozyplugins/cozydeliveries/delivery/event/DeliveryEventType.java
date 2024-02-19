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

import com.github.cozyplugins.cozydeliveries.delivery.event.type.CooldownDeliveryEventType;
import com.github.cozyplugins.cozydeliveries.delivery.event.type.FirstJoinDeliveryEventType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the delivery event type.
 * Contains different types that can be specified in
 * the event configuration directory.
 */
public interface DeliveryEventType {

    /**
     * Used to get the delivery event handler
     * for this event type.
     *
     * @return Teh delivery event handler.
     */
    @NotNull DeliveryEventHandler getDeliveryEventHandler();

    /**
     * Called when a player joins the server.
     *
     * @param event         The instance of the event.
     * @param deliveryEvent The instance of the delivery event.
     */
    void onPlayerJoin(@NotNull PlayerJoinEvent event, @NotNull DeliveryEvent deliveryEvent);

    /**
     * Called when a player leaves the server.
     *
     * @param event         The instance of the event.
     * @param deliveryEvent The instance of the delivery event.
     */
    void onPlayerLeave(@NotNull PlayerKickEvent event, @NotNull DeliveryEvent deliveryEvent);

    /**
     * Used to attempt to get the event type
     * of specific identifier.
     *
     * @param identifier The identifier to look for.
     * @return The instance of the delivery event type
     * that matches the identifier.
     */
    static @NotNull Optional<DeliveryEventType> getEventType(@NotNull String identifier) {
        if (identifier.equalsIgnoreCase("cooldown")) {
            return Optional.of(new CooldownDeliveryEventType());
        }
        if (identifier.equalsIgnoreCase("first_join")) {
            return Optional.of(new FirstJoinDeliveryEventType());
        }
        return Optional.empty();
    }
}
