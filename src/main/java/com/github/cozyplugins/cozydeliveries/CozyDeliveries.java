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

package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozydeliveries.command.DeliveryCommand;
import com.github.cozyplugins.cozydeliveries.configuration.ContentConfigurationDirectory;
import com.github.cozyplugins.cozydeliveries.configuration.EventConfigurationDirectory;
import com.github.cozyplugins.cozydeliveries.database.*;
import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import com.github.cozyplugins.cozydeliveries.delivery.DeliveryContent;
import com.github.cozyplugins.cozydeliveries.event.DeliverySendEvent;
import com.github.cozyplugins.cozydeliveries.inventory.AddItemsInventory;
import com.github.cozyplugins.cozydeliveries.inventory.PickPlayerInventory;
import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.ConfigurationFactory;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import com.github.smuddgge.squishydatabase.DatabaseCredentials;
import com.github.smuddgge.squishydatabase.DatabaseFactory;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents the main plugin class.
 * Using the cozy library to handle commands etc.
 */
public final class CozyDeliveries extends CozyPlugin implements CozyDeliveriesAPI, Listener {

    private static CozyDeliveries instance;
    private @Nullable Configuration config;
    private @Nullable Database database;
    private @Nullable ContentConfigurationDirectory contentDirectory;
    private @Nullable EventConfigurationDirectory eventDirectory;

    @Override
    public void onLoad() {
        CozyDeliveries.instance = this;
        super.onLoad();
    }

    @Override
    public boolean enableCommandDirectory() {
        return true;
    }

    @Override
    public void onCozyEnable() {

        // Initialize the configuration file.
        this.config = ConfigurationFactory.YAML.create(
                this.getDataFolder(), "config"
        );
        this.config.setDefaultPath("config.yml");
        this.config.load();

        // Initialize the database.
        this.setupDatabase();

        // Initialize the configuration directory's.
        this.contentDirectory = new ContentConfigurationDirectory();
        this.contentDirectory.getDirectory().reload();
        this.eventDirectory = new EventConfigurationDirectory();
        this.eventDirectory.getDirectory().reload();

        // Register the commands.
        this.addCommandType(new DeliveryCommand());

        // Register this as a listener.
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private void setupDatabase() {

        // Get the instance of the database factory.
        DatabaseFactory factory = DatabaseFactory.valueOf(this.getConfiguration().getString("database.type"));

        if (factory.equals(DatabaseFactory.SQLITE)) {
            this.database = factory.create(DatabaseCredentials.SQLITE(
                    this.getDataFolder().getAbsolutePath() + "/database.sqlite"
            ));
        }

        if (factory.equals(DatabaseFactory.MONGO)) {
            this.database = factory.create(DatabaseCredentials.MONGO(
                    this.getConfiguration().getString("database.connection_string"),
                    this.getConfiguration().getString("database.database_name")
            ));
        }

        if (factory.equals(DatabaseFactory.MYSQL)) {
            this.database = factory.create(DatabaseCredentials.MYSQL(
                    this.getConfiguration().getString("database.connection_string")
            ));
        }

        this.database.createTable(new CooldownTable());
        this.database.createTable(new DeliveryTable());
        this.database.createTable(new PlayerTable());
    }

    @Override
    public @NotNull Configuration getConfiguration() {

        // Check if the config is null.
        if (this.config == null) throw new RuntimeException(
                "Tried to get the config but the config has not been initialized yet."
        );

        return this.config;
    }

    @Override
    public @NotNull Database getDatabase() {

        // Check if the database is null.
        if (this.database == null) throw new RuntimeException(
                "Tried to get the database but the database has not been initialized yet."
        );

        return this.database;
    }

    @Override
    public @NotNull ContentConfigurationDirectory getContentConfiguration() {

        // Check if the configuration directory is null.
        if (this.contentDirectory == null) throw new RuntimeException(
                "Tried to get the content configuration but it has not been initialized yet."
        );

        return this.contentDirectory;
    }

    @Override
    public @NotNull EventConfigurationDirectory getEventConfiguration() {

        // Check if the configuration directory is null.
        if (this.eventDirectory == null) throw new RuntimeException(
                "Tried to get the event configuration but it has not been initialized yet."
        );

        return this.eventDirectory;
    }

    @Override
    public @NotNull Optional<Delivery> getDelivery(@NotNull UUID uuid) {

        // Check if the database is disabled.
        if (this.getDatabase().isDisabled()) return Optional.empty();

        DeliveryRecord record = this.getDatabase()
                .getTable(DeliveryTable.class)
                .getFirstRecord(new Query().match("uuid", uuid.toString()));

        // Check if the record doesn't exist.
        if (record == null) return Optional.empty();

        // Check if the delivery is expired.
        Delivery delivery = this.removeIfExpired(record.getDelivery()).orElse(null);
        return Optional.ofNullable(delivery);
    }

    @Override
    public @NotNull List<Delivery> getDeliveryList() {

        // Check if the database is disabled.
        if (this.getDatabase().isDisabled()) return new ArrayList<>();

        // Get the list of deliveries.
        List<Delivery> deliveryList = this.getDatabase()
                .getTable(DeliveryTable.class)
                .getRecordList()
                .stream().map(DeliveryRecord::getDelivery)
                .toList();

        return this.removeExpiredDeliveries(deliveryList);
    }

    @Override
    public @NotNull List<Delivery> getDeliveryList(@NotNull UUID playerUuid) {

        // Check if the database is disabled.
        if (this.getDatabase().isDisabled()) return new ArrayList<>();

        // Get the list of deliveries.
        List<Delivery> deliveryList = this.getDatabase()
                .getTable(DeliveryTable.class)
                .getRecordList(new Query().match("toPlayerUuid", playerUuid.toString()))
                .stream().map(DeliveryRecord::getDelivery)
                .toList();

        return this.removeExpiredDeliveries(deliveryList);
    }

    @Override
    public boolean sendDelivery(@NotNull Delivery delivery) {

        // Check if the database is disabled.
        if (this.getDatabase().isDisabled()) return false;

        // Call a delivery send event.
        DeliverySendEvent event = new DeliverySendEvent(delivery);
        Bukkit.getPluginManager().callEvent(event);

        // Check if the event was cancelled.
        if (event.isCancelled()) return false;

        // Handle in an external method to ensure
        // the delivery instance is not used.
        this.sendDelivery0(event);
        return true;
    }

    @Override
    public boolean sendDelivery(@NotNull UUID playerUuid, @Nullable String fromName, @NotNull CozyItem... items) {
        return this.sendDelivery(playerUuid, fromName, Arrays.stream(items).toList());
    }

    @Override
    public boolean sendDelivery(@NotNull UUID playerUuid, @Nullable String fromName, @NotNull List<CozyItem> itemList) {
        Delivery delivery = new Delivery(playerUuid, System.currentTimeMillis());
        delivery.setFromName(fromName);
        delivery.setDeliveryContent(new DeliveryContent().addItems(itemList));

        return this.sendDelivery(delivery);
    }

    @Override
    public void createDelivery(@NotNull Player fromPlayer) {
        new PickPlayerInventory().open(fromPlayer);
    }

    @Override
    public void createDelivery(@NotNull Player fromPlayer, @NotNull UUID toPlayerUuid) {
        new AddItemsInventory(toPlayerUuid).open(fromPlayer);
    }

    @Override
    public @NotNull Optional<Delivery> removeIfExpired(@NotNull Delivery delivery) {
        List<Delivery> list = this.removeExpiredDeliveries(List.of(delivery));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public @NotNull List<Delivery> removeExpiredDeliveries(@NotNull List<Delivery> deliveryList) {
        List<Delivery> list = new ArrayList<>(deliveryList);

        // The list of expired deliveries.
        List<Delivery> toRemove = new ArrayList<>();

        // Remove expired deliveries.
        for (Delivery delivery : list) {
            if (!delivery.hasExpireDate()) continue;
            if (!delivery.hasExpired()) continue;
            toRemove.add(delivery);

            // Remove the delivery from the database.
            if (this.getDatabase().isDisabled()) continue;

            // Remove the record from the database.
            this.getDatabase()
                    .getTable(DeliveryTable.class)
                    .removeRecord(new DeliveryRecord(delivery));
        }

        list.removeAll(toRemove);
        return list;
    }

    private void sendDelivery0(@NotNull DeliverySendEvent event) {

        // Save the delivery to the database.
        this.getDatabase().getTable(DeliveryTable.class)
                .insertRecord(new DeliveryRecord(event.getDelivery()));

        // Update the player's statistics.
        PlayerTable playerTable = this.getDatabase().getTable(PlayerTable.class);
        playerTable.insertRecord(
                playerTable.getPlayerRecord(event.getDelivery().getToPlayerUuid())
                        .orElse(new PlayerRecord(event.getDelivery().getToPlayerUuid()))
                        .incrementReceived(1)
        );

        // Attempt to notify the player it was sent to.
        Player player = Bukkit.getPlayer(event.getDelivery().getToPlayerUuid());
        if (player == null) return;

        new PlayerUser(player).sendMessage(this.getConfiguration()
                .getAdaptedString("delivery.receive_message", "\n", "&7You have received a delivery from &f{sender}")
                .replace("{sender}", event.getDelivery().getFromName("null"))
        );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getEventConfiguration().onPlayerJoinEvent(event);
    }

    @EventHandler
    public void onPlayerLeave(PlayerKickEvent event) {
        this.getEventConfiguration().onPlayerLeaveEvent(event);
    }

    /**
     * Used to get the instance of the plugin api methods
     * from bukkit.
     *
     * @return The optional instance of the api.
     */
    public static @NotNull Optional<CozyDeliveriesAPI> getAPI() {
        return Optional.of(CozyDeliveries.instance);
    }
}
