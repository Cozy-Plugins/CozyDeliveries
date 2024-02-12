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

package com.github.cozyplugins.cozydeliveries.delivery;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozydeliveries.database.DeliveryRecord;
import com.github.cozyplugins.cozydeliveries.database.DeliveryTable;
import com.github.cozyplugins.cozylibrary.indicator.ConfigurationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents a delivery to be given to a player.
 */
public class Delivery implements ConfigurationConvertable<Delivery>, Replicable<Delivery>, Savable {

    private @NotNull UUID uuid;
    private @NotNull UUID toPlayerUuid;
    private @Nullable String fromName;
    private @NotNull Long timeStampMillis;
    private @NotNull Long timeStampExpire;
    private @NotNull DeliveryContent deliveryContent;

    /**
     * Used to create a new instance of a delivery.
     *
     * @param toPlayerUuid The player it will be delivered to.
     * @param timeStampMillis The time stamp it was sent to the player.
     */
    public Delivery(@NotNull UUID toPlayerUuid, @NotNull Long timeStampMillis) {
        this.uuid = UUID.randomUUID();
        this.toPlayerUuid = toPlayerUuid;
        this.timeStampMillis = timeStampMillis;
        this.timeStampExpire = -1L;
        this.deliveryContent = new DeliveryContent();
    }

    /**
     * Represents the interface used when
     * creating the inventory item for this delivery.
     * This class will be used to interface with the inventory
     * and regenerate it when the delivery is given.
     */
    public interface RegenerateInventory {

        /**
         * Called when the inventory should be
         * regenerated.
         *
         * @param user The instance of the player.
         */
        void onRegenerate(@NotNull PlayerUser user);
    }

    /**
     * Used to get the delivery's uuid.
     *
     * @return The delivery's uuid.
     */
    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    /**
     * Used to get the player's uuid that the
     * delivery was sent to.
     *
     * @return The player's uuid.
     */
    public @NotNull UUID getToPlayerUuid() {
        return this.toPlayerUuid;
    }

    /**
     * Used to get the player's name
     * that the delivery is being sent to.
     *
     * @return The player's name.
     */
    public @Nullable String getToPlayerName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.toPlayerUuid);
        return player.getName();
    }

    /**
     * Used to get the name of the sender.
     *
     * @return The name of the sender.
     */
    public @Nullable String getFromName() {
        return this.fromName;
    }

    /**
     * Used to get the name of the sender.
     * If the sender is null it will return the alternative.
     *
     * @param alternative The name of the alternative sender.
     * @return The sender.
     */
    public @NotNull String getFromName(@NotNull String alternative) {
        return this.fromName == null ? alternative : this.fromName;
    }

    /**
     * Used to get the time stamp that
     * the delivery was sent.
     *
     * @return The time stamp it was sent.
     */
    public long getTimeStampMillis() {
        return this.timeStampMillis;
    }

    /**
     * Used to get the time stamp which this
     * delivery will expire.
     * This defaults to -1 for no delivery expire.
     *
     * @return The time stamp the delivery
     * will expire.
     */
    public long getTimeStampExpire() {
        return this.timeStampExpire;
    }

    /**
     * Used to get the expired time formatted
     * as hours, minutes and seconds.
     *
     * @return The formatted expired time.
     */
    public @NotNull String getExpireTimeFormatted() {
        if (!this.hasExpireDate()) return "None";
        Duration duration = Duration.ofMillis(this.getTimeStampExpire() - System.currentTimeMillis());
        return duration.toHours() + "h " + duration.toMinutesPart() + "m " + duration.toSecondsPart() + "s ";
    }

    /**
     * Used to get the reward bundle that will be
     * given to the player.
     *
     * @return The reward bundle.
     */
    public @NotNull DeliveryContent getDeliveryContent() {
        return this.deliveryContent;
    }

    /**
     * Used to create an inventory item
     * that represents this delivery.
     * This will include the click action.
     *
     * @param regenerateInventory The action that will be run when
     *                            the inventory should be regenerated.
     * @return The instance of the inventory item.
     */
    public @NotNull InventoryItem getInventoryItem(@NotNull RegenerateInventory regenerateInventory) {
        return new InventoryItem(this.getInterfaceItem().create())
                .addAction((ClickAction) (user, type, inventory) -> {

                    // Get the configuration section in the config.yml
                    ConfigurationSection section = CozyDeliveries.getAPI().orElseThrow()
                            .getConfiguration().getSection("delivery");

                    // Check if they have inventory space.
                    if (!this.hasInventorySpace(user)) {
                        user.sendMessage(section.getAdaptedString(
                                "inventory_space", "\n", "&7You dont have enough inventory space to collect this delivery."
                        ));
                        return;
                    }

                    // Give the delivery to the player.
                    boolean success = this.giveAndDelete(user);
                    if (success) {
                        user.sendMessage(section.getAdaptedString(
                                "success", "\n", "&7You have received a delivery."
                        ));
                        regenerateInventory.onRegenerate(user);
                        return;
                    }

                    user.sendMessage(section.getAdaptedString("failed", "\n", "&7Failed to receive a delivery."));
                });
    }

    /**
     * Used to get the instance of the item to
     * use in the delivery interfaces.
     *
     * @return The instance of the item.
     */
    public @NotNull CozyItem getInterfaceItem() {

        // Check if the item is null.
        // If so return the default item.
        if (this.getDeliveryContent().getItem() == null) {
            CozyItem defaultItem = new CozyItem().convert(
                    CozyDeliveries.getAPI().orElseThrow()
                            .getConfiguration().getSection("delivery.default_item")
            );

            // Check if the default item is air.
            if (defaultItem.getMaterial().equals(Material.AIR)) {
                CozyDeliveries.getPlugin().getLogger().log(
                        Level.WARNING,
                        "Attempted to get the default item, but it returned as material AIR."
                );
                return new CozyItem(Material.BARREL);
            }

            return this.parsePlaceholders(defaultItem);
        }

        // Check if the item is air.
        if (this.getDeliveryContent().getItem().getMaterial().equals(Material.AIR)) {
            CozyDeliveries.getPlugin().getLogger().log(
                    Level.WARNING,
                    "Attempted to get the item assosicated to {delivery}, but it returned as material AIR."
                            .replace("{delivery}", this.getDeliveryContent().toString())
            );
            this.getDeliveryContent().setCustomItem(null);
            return this.getInterfaceItem();
        }

        // Otherwise use the custom item.
        return this.parsePlaceholders(this.getDeliveryContent().getItem());
    }

    /**
     * Used to set the uuid to a new uuid.
     * Be careful as this could cause unintended
     * side effects.
     * This is mainly used for the database.
     *
     * @param uuid The uuid of this delivery.
     * @return This instance.
     */
    public @NotNull Delivery setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Used to set the name of the sender.
     *
     * @param fromName The name of the sender.
     * @return This instance.
     */
    public @NotNull Delivery setFromName(@Nullable String fromName) {
        this.fromName = fromName;
        return this;
    }

    /**
     * Used to set the time stamp in milliseconds.
     *
     * @param timeStampMillis The time stamp.
     * @return This instance.
     */
    public @NotNull Delivery setTimeStampMillis(@NotNull Long timeStampMillis) {
        this.timeStampMillis = timeStampMillis;
        return this;
    }

    /**
     * Used to set when the delivery should expire.
     *
     * @param timeStampExpire The time stamp for when the delivery should expire.
     * @return This instance.
     */
    public @NotNull Delivery setTimeStampExpire(@NotNull Long timeStampExpire) {
        this.timeStampExpire = timeStampExpire;
        return this;
    }

    /**
     * Used to set the bundle of rewards to a specific
     * instance.
     *
     * @param deliveryContent The instance of a delivery content.
     * @return This instance.
     */
    public @NotNull Delivery setDeliveryContent(@NotNull DeliveryContent deliveryContent) {
        this.deliveryContent = deliveryContent;
        return this;
    }

    /**
     * Used to check if the delivery should
     * expire.
     *
     * @return True if the delivery should expire.
     */
    public boolean hasExpireDate() {
        return this.timeStampExpire > -1L;
    }

    /**
     * Used to check if the delivery has expired.
     * Normally using the get methods in the api,
     * they will already be removed from a database
     * before returning the deliveries if expired.
     *
     * @return True if the delivery has expired.
     */
    public boolean hasExpired() {
        return this.timeStampExpire < System.currentTimeMillis();
    }

    /**
     * Used to check if a user has enough inventory space to
     * obtain the delivery.
     *
     * @param user The instance of the user.
     * @return True if they have enough room.
     */
    public boolean hasInventorySpace(@NotNull PlayerUser user) {
        return this.deliveryContent.hasInventorySpace(user);
    }

    /**
     * Used to delete the delivery from the
     * database and then give it to a user.
     *
     * @param user The instance of the user.
     * @return True if successful.
     */
    public boolean giveAndDelete(@NotNull PlayerUser user) {

        // Remove the record from the database.
        boolean success = CozyDeliveries.getAPI().orElseThrow().getDatabase()
                .getTable(DeliveryTable.class)
                .removeAllRecords(new Query().match("uuid", this.uuid.toString()));

        if (!success) return false;
        return this.deliveryContent.give(user);
    }

    /**
     * Used to parse the placeholders of an item.
     *
     * @param item The item to parse.
     * @return The parsed placeholders.
     */
    public @NotNull CozyItem parsePlaceholders(@NotNull CozyItem item) {

        // Parse name.
        item.setName(this.parsePlaceholders(item.getName()));

        // Parse lore.
        if (!item.getLore().isEmpty()) {
            item.setLore(
                    this.parsePlaceholders(String.join("\n", item.getLore()))
                            .split("\n")
            );
        }

        return item;
    }

    /**
     * The placeholders this will parse include:
     * <li>{lore}</li>
     * <li>{from}</li>
     * <li>{expire}</li>
     * <li>{player}</li>
     *
     * @param string The instance of the string.
     * @return The parsed string.
     */
    public @NotNull String parsePlaceholders(@NotNull String string) {
        final String playerName = this.getToPlayerName();

        return string
                .replace("{lore}", String.join("\n&f", this.getDeliveryContent().getLoreNotEmpty()))
                .replace("{from}", this.getFromName("None"))
                .replace("{expire}", this.getExpireTimeFormatted())
                .replace("{player_name}", playerName == null ? "null" : playerName);
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        section.set("to_player_uuid", this.toPlayerUuid.toString());
        section.set("from_name", this.fromName);
        section.set("time_stamp_expire_millis", this.timeStampExpire);
        section.set("content", this.deliveryContent.convert().getMap());

        return section;
    }

    @Override
    public @NotNull Delivery convert(ConfigurationSection section) {

        this.toPlayerUuid = UUID.fromString(section.getString("to_player_uuid"));
        this.fromName = section.getString("from_name");
        this.timeStampExpire = (long) section.getDouble("time_stamp_expire_millis");
        this.deliveryContent = new DeliveryContent().convert(section.getSection("content"));

        return this;
    }

    @Override
    public Delivery duplicate() {
        return new Delivery(this.toPlayerUuid, this.timeStampMillis).convert(this.convert());
    }

    /**
     * Used to save this class to the database.
     * Be carefully not to call this method too many
     * times as it will execute database operations.
     */
    @Override
    public void save() {
        CozyDeliveries.getAPI().orElseThrow()
                .getDatabase()
                .getTable(DeliveryTable.class)
                .insertRecord(new DeliveryRecord(this));
    }
}
