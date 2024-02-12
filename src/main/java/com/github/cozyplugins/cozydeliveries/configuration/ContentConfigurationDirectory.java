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

import com.github.cozyplugins.cozydeliveries.delivery.DeliveryContent;
import org.jetbrains.annotations.NotNull;

public class ContentConfigurationDirectory extends CozyDeliveriesConfigurationDirectory<DeliveryContent> {

    /**
     * Used to create a new event configuration directory instance.
     */
    public ContentConfigurationDirectory() {
        super("contents", "contents.yml");
    }

    @Override
    public @NotNull DeliveryContent createEmpty(@NotNull String identifier) {
        return new DeliveryContent();
    }

    @Override
    public void onReload() {

    }
}
