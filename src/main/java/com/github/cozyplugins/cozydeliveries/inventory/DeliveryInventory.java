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
import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import com.github.cozyplugins.cozylibrary.inventory.InventoryInterface;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Represents the delivery inventory.
 * Contains all the player's deliveries.
 */
public class DeliveryInventory extends InventoryInterface {

    private final @NotNull UUID deliveryPlayerUuid;
    private final @NotNull ConfigurationSection section;

    /**
     * Used to create a new instance of the
     * delivery inventory in terms of a specific
     * player.
     *
     * @param deliveryPlayerUuid The user to show the delivery's of.
     * @param section The configuration section that represents the inventory.
     */
    public DeliveryInventory(@NotNull UUID deliveryPlayerUuid, @NotNull ConfigurationSection section) {
        super(54, section.getString("title", "&8&lDeliveries"));

        this.deliveryPlayerUuid = deliveryPlayerUuid;
        this.section = section;
    }

    @Override
    protected void onGenerate(PlayerUser player) {
        this.resetInventory();

        // Get the player's deliveries.
        List<Delivery> deliveryList = CozyDeliveries.getAPI()
                .orElseThrow().getDeliveryList(this.deliveryPlayerUuid);

        // Loop though all the deliveries.
        Iterator<Integer> slotIterator = section.getListInteger("slots").iterator();
        for (Delivery delivery : deliveryList) {

            // Check if there are any more slots to assign.
            if (!slotIterator.hasNext()) return;
            this.setItem(delivery.getInventoryItem(this::onGenerate).addSlot(slotIterator.next()));
        }
    }
}
