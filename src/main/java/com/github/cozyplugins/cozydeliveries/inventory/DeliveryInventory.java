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
import com.github.cozyplugins.cozydeliveries.database.PlayerRecord;
import com.github.cozyplugins.cozydeliveries.database.PlayerTable;
import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import com.github.cozyplugins.cozylibrary.inventory.ConfigurationInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents the delivery inventory.
 * Contains all the player's deliveries.
 */
public class DeliveryInventory extends ConfigurationInventory {

    private final @NotNull UUID deliveryPlayerUuid;
    private final @NotNull ConfigurationSection section;

    /**
     * Used to create a new instance of the
     * delivery inventory in terms of a specific
     * player.
     *
     * @param deliveryPlayerUuid The user to show the delivery's of.
     * @param section            The configuration section that represents the inventory.
     */
    public DeliveryInventory(@NotNull UUID deliveryPlayerUuid, @NotNull ConfigurationSection section) {
        super(section);

        this.deliveryPlayerUuid = deliveryPlayerUuid;
        this.section = section;
    }

    @Override
    public @Nullable InventoryItem onFunction(@NotNull InventoryItem item, @NotNull ConfigurationSection section) {
        return switch (section.getString("type", "null")) {
            case "delivery" -> this.onDeliveryItem(item);
            case "send" -> this.onSendItem(item);
            case "stats" -> this.onStatisticsItem(item);
            default -> {
                CozyDeliveries.getPlugin().getLogger().log(
                        Level.WARNING,
                        "Could not find function named " + section.getString("type", "null")
                );
                yield item;
            }
        };
    }

    private @Nullable InventoryItem onDeliveryItem(@NotNull InventoryItem item) {

        // Get the player's deliveries.
        List<Delivery> deliveryList = CozyDeliveries.getAPI()
                .orElseThrow().getDeliveryList(this.deliveryPlayerUuid);

        // Loop though the deliveries.
        Iterator<Integer> slotIterator = item.getSlots().iterator();
        for (Delivery delivery : deliveryList) {

            // Check if there are any more slots to assign.
            if (!slotIterator.hasNext()) return null;
            this.setItem(delivery.getInventoryItem(this::onGenerate).addSlot(slotIterator.next()));
        }

        return null;
    }

    private @NotNull InventoryItem onSendItem(@NotNull InventoryItem item) {
        return item.addAction((ClickAction) (user, type, inventory) -> {
            new PickPlayerInventory().open(user.getPlayer());
        });
    }

    private @NotNull InventoryItem onStatisticsItem(@NotNull InventoryItem item) {

        // Check if the database is disabled.
        if (CozyDeliveries.getAPI().orElseThrow().getDatabase().isDisabled()) {
            return item;
        }

        // Get the player's statistics.
        PlayerRecord record = CozyDeliveries.getAPI().orElseThrow().getDatabase()
                .getTable(PlayerTable.class)
                .getPlayerRecord(this.deliveryPlayerUuid)
                .orElse(new PlayerRecord());

        return item
                .replaceNameAndLore("{sent}", Integer.toString(record.getDeliveriesSent()))
                .replaceNameAndLore("{from}", Integer.toString(record.getDeliveriesReceived()));
    }
}
