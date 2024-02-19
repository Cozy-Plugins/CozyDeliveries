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

package com.github.cozyplugins.cozydeliveries.delivery.event.type;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventHandler;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventType;
import com.github.cozyplugins.cozydeliveries.delivery.event.handler.StandardDeliveryEventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

public class FirstJoinDeliveryEventType implements DeliveryEventType {

    @Override
    public @NotNull DeliveryEventHandler getDeliveryEventHandler() {
        return new StandardDeliveryEventHandler();
    }

    @Override
    public void onPlayerJoin(@NotNull PlayerJoinEvent event, @NotNull DeliveryEvent deliveryEvent) {
        if (event.getPlayer().hasPlayedBefore()) return;
        this.getDeliveryEventHandler().onEvent(deliveryEvent, event.getPlayer().getUniqueId());
    }

    @Override
    public void onPlayerLeave(@NotNull PlayerKickEvent event, @NotNull DeliveryEvent deliveryEvent) {

    }
}
