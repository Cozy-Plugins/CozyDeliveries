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
import com.github.cozyplugins.cozylibrary.inventory.ConfigurationInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Represents the pick player inventory.
 * This is where a player can pick a player to send a delivery to.
 * <pre>
 *   pick_player_inventory:
 *     size: 54
 *     title: "&8&lSend To..."
 *     items:
 *       background:
 *         material: BLACK_STAINED_GLASS_PANE
 *         name: "&7"
 *         slots: [ 45, 46, 47, 48, 49, 50, 51, 52, 53 ]
 *       player:
 *         function:
 *           type: "player"
 *         material: PLAYER_HEAD
 *         name: "&f&l{name}"
 *         lore:
 *           - "&7Click to send a delivery to &f{name}"
 *         slots: [ 47, 48, 49, 50, 51 ]
 *       last_page:
 *         function:
 *           type: "last_page"
 *         material: LIME_STAINED_GLASS_PANE
 *         name: "&a&lLast Page"
 *         lore:
 *           - "&7Click to go back a page."
 *         slots: [ 48 ]
 *       next_page:
 *         function:
 *           type: "next_page"
 *         material: LIME_STAINED_GLASS_PANE
 *         name: "&a&lNext Page"
 *         lore:
 *           - "&7Click to go to the next page."
 *         slots: [ 50 ]
 * </pre>
 */
public class PickPlayerInventory extends ConfigurationInventory {

    private int page;

    /**
     * Used to create a pick player inventory.
     */
    public PickPlayerInventory() {
        super(CozyDeliveries.getAPI().orElseThrow().getConfiguration().getSection("delivery.pick_player_inventory"));

        this.page = 0;
    }

    @Override
    public @Nullable InventoryItem onFunction(
            @NotNull InventoryItem item,
            @NotNull ConfigurationSection section) {

        return switch (section.getString("type", "null")) {
            case "player" -> this.onPlayer(item);
            case "last_page" -> this.onLastPage(item);
            case "next_page" -> this.onNextPage(item);
            default -> {
                CozyDeliveries.getPlugin().getLogger().log(
                        Level.WARNING,
                        "Could not find function named " + section.getString("type", "null")
                );
                yield item;
            }
        };
    }

    private @Nullable InventoryItem onPlayer(@NotNull InventoryItem item) {

        // Get the all the players you can send to.
        List<OfflinePlayer> offlinePlayerList = Arrays.stream(Bukkit.getOfflinePlayers()).toList();

        final int playersPerPage = item.getSlots().size();
        final int playersToSkip = this.page * playersPerPage;

        // Loop though the deliveries.
        Iterator<Integer> slotIterator = item.getSlots().iterator();
        int playerPosition = 0;
        for (OfflinePlayer player : offlinePlayerList) {
            playerPosition++;

            // Check if this player should be skipped as
            // they are on a page before.
            if (playersToSkip > playerPosition) continue;

            // Check if there are any more slots to assign.
            if (!slotIterator.hasNext()) return null;
            this.setItem(item
                    .removeSlots()
                    .addSlot(slotIterator.next())
                    .setSkull(player.getUniqueId())
                    .addAction((ClickAction) (playerUser, clickType, inventory) -> {
                        CozyDeliveries.getAPI().orElseThrow().createDelivery(
                                Objects.requireNonNull(this.getOwner()),
                                playerUser.getUuid()
                        );
                    })
            );
        }

        return null;
    }

    private @NotNull InventoryItem onLastPage(@NotNull InventoryItem item) {
        return item.addAction((ClickAction) (playerUser, clickType, inventory) -> {
            if (this.page == 0) return;
            this.page--;
            this.onGenerate(new PlayerUser(Objects.requireNonNull(this.getOwner().getPlayer())));
        });
    }

    private @NotNull InventoryItem onNextPage(@NotNull InventoryItem item) {
        return item.addAction((ClickAction) (playerUser, clickType, inventory) -> {
            this.page++;
            this.onGenerate(new PlayerUser(Objects.requireNonNull(this.getOwner().getPlayer())));
        });
    }
}
