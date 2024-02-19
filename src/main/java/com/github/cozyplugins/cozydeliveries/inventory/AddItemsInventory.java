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
import com.github.cozyplugins.cozylibrary.inventory.ConfigurationInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.inventory.action.action.PlaceAction;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents the add items inventory.
 *
 * <pre>
 *   add_items_inventory:
 *     size: 54
 *     title: "&8&lSend Items To {player}"
 *     items:
 *       background:
 *         material: BLACK_STAINED_GLASS_PANE
 *         name: "&7"
 *         slots: [ 45, 46, 47, 48, 49, 50, 51, 52, 53 ]
 *       item_slots:
 *         slots: [
 *           0, 1, 2, 3, 4, 5, 6, 7, 8,
 *           9, 10, 11, 12, 13, 14, 15, 16, 17,
 *           18, 19, 20, 21, 22, 23, 24, 25, 26,
 *           27, 28, 29, 30, 31, 32, 33, 34, 35,
 *           36, 37, 38, 39, 40, 41, 42, 43, 44
 *         ]
 *       send_button:
 *         material: LIME_STAINED_GLASS_PANE
 *         name: "&a&lSend Items"
 *         lore:
 *           - "&7Click to send these items to &f{player}&7."
 *         slots: [ 47, 48, 49, 50, 51 ]
 * </pre>
 */
public class AddItemsInventory extends ConfigurationInventory {

    private final @NotNull UUID sendToPlayerUuid;
    private final @NotNull List<Integer> slotList;

    /**
     * Used to create an add items inventory.
     * This will let the player send specific items to a player.
     *
     * @param sendToPlayerUuid The instance of the player uuid
     *                         to send to.
     */
    public AddItemsInventory(@NotNull UUID sendToPlayerUuid) {
        super(CozyDeliveries.getAPI().orElseThrow()
                .getConfiguration()
                .getSection("delivery.add_items_inventory")
        );

        this.sendToPlayerUuid = sendToPlayerUuid;
        this.slotList = new ArrayList<>();
    }

    @Override
    public @Nullable InventoryItem onFunction(
            @NotNull InventoryItem item,
            @NotNull ConfigurationSection section) {

        return switch (section.getString("type", "null")) {
            case "item" -> this.onItem(item);
            case "send" -> this.onSend(item);
            default -> {
                CozyDeliveries.getPlugin().getLogger().log(
                        Level.WARNING,
                        "Could not find function named " + section.getString("type", "null")
                );
                yield item;
            }
        };
    }

    public @NotNull InventoryItem onItem(@NotNull InventoryItem item) {
        this.slotList.addAll(item.getSlots());
        return item.addAction((PlaceAction) (playerUser, cozyItem) -> {
        });
    }

    public @NotNull InventoryItem onSend(@NotNull InventoryItem item) {
        return item.addAction((ClickAction) (playerUser, clickType, inventory) -> {

            // Get the items to send.
            List<ItemStack> itemListToSend = this.getItems(inventory);

            // Send the delivery.
            CozyDeliveries.getAPI().orElseThrow().sendDelivery(
                    this.sendToPlayerUuid,
                    Objects.requireNonNull(this.getOwner()).getName(),
                    itemListToSend.stream().map(CozyItem::new).toList()
            );

            // Close the inventory.
            playerUser.getPlayer().closeInventory();
            this.resetInventory();

            // Update player stats.
            PlayerRecord record = CozyDeliveries.getAPI().orElseThrow().getDatabase()
                    .getTable(PlayerTable.class)
                    .getPlayerRecord(this.getOwner().getUniqueId())
                    .orElse(new PlayerRecord(this.getOwner().getUniqueId()));

            CozyDeliveries.getAPI().orElseThrow().getDatabase()
                    .getTable(PlayerTable.class)
                    .insertRecord(record.incrementSent(1));
        });
    }

    /**
     * Used to get the list of items to send
     * to the player.
     *
     * @param inventory The instance of the inventory.
     * @return The list of items.
     */
    public @NotNull List<ItemStack> getItems(@NotNull Inventory inventory) {
        List<ItemStack> itemList = new ArrayList<>();

        for (int slot : this.slotList) {
            ItemStack item = inventory.getItem(slot);
            if (item == null) continue;
            if (item.getType().equals(Material.AIR)) continue;
            itemList.add(item);
        }

        return itemList;
    }
}
