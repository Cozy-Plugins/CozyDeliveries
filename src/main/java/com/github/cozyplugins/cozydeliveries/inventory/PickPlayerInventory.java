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

package com.github.cozyplugins.cozydeliveries.inventory;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozylibrary.inventory.ConfigurationInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PickPlayerInventory extends ConfigurationInventory {

    public PickPlayerInventory() {
        super(CozyDeliveries.getAPI().orElseThrow().getConfiguration().getSection(""));
    }

    @Override
    public @Nullable InventoryItem onFunction(
            @NotNull InventoryItem inventoryItem,
            @NotNull ConfigurationSection configurationSection) {

        return null;
    }
}
