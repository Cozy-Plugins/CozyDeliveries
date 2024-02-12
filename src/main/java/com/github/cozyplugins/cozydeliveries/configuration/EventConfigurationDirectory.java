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

package com.github.cozyplugins.cozydeliveries.configuration;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the event configuration directory.
 * Contains events when deliveries should be given.
 */
public class EventConfigurationDirectory extends CozyDeliveriesConfigurationDirectory<DeliveryEvent> {

    private @NotNull List<DeliveryEvent> eventList;

    /**
     * Used to create a new event configuration directory instance.
     */
    public EventConfigurationDirectory() {
        super("events", "events.yml");

        this.eventList = new ArrayList<>();
    }

    @Override
    public @NotNull DeliveryEvent createEmpty(@NotNull String identifier) {
        return new DeliveryEvent(identifier);
    }

    @Override
    public void onReload() {

        // Reset the list.
        this.eventList = new ArrayList<>();

        // Populate the list.
        this.eventList.addAll(this.getAllTypes());
    }

    /**
     * Used to call the player join event for all delivery events.
     *
     * @param event The instance of the event.
     */
    public void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        for (DeliveryEvent deliveryEvent : this.eventList) {
            deliveryEvent.getType().onPlayerJoin(event, deliveryEvent);
        }
    }

    /**
     * Used to call the player leave event for all the delivery events.
     *
     * @param event The isntance of the event.
     */
    public void onPlayerLeaveEvent(@NotNull PlayerKickEvent event) {
        for (DeliveryEvent deliveryEvent : this.eventList) {
            deliveryEvent.getType().onPlayerLeave(event, deliveryEvent);
        }
    }
}
