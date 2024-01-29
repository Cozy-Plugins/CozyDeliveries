package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozydeliveries.command.DeliveryCommand;
import com.github.cozyplugins.cozydeliveries.database.DeliveryRecord;
import com.github.cozyplugins.cozydeliveries.database.DeliveryTable;
import com.github.cozyplugins.cozydeliveries.event.DeliverySendEvent;
import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.reward.RewardBundle;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.ConfigurationFactory;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import com.github.smuddgge.squishydatabase.DatabaseCredentials;
import com.github.smuddgge.squishydatabase.DatabaseFactory;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
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

        this.database.createTable(new DeliveryTable());
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
    public @NotNull Optional<Delivery> getDelivery(@NotNull UUID uuid) {
        DeliveryRecord record = this.getDatabase()
                .getTable(DeliveryTable.class)
                .getFirstRecord(new Query().match("uuid", uuid.toString()));

        if (record == null) return Optional.empty();
        return Optional.of(record.getDelivery());
    }

    @Override
    public @NotNull List<Delivery> getDeliveryList(@NotNull UUID playerUuid) {
        List<DeliveryRecord> deliveryRecordList = this.getDatabase()
                .getTable(DeliveryTable.class)
                .getRecordList(new Query().match("toPlayerUuid", playerUuid.toString()));

        return deliveryRecordList == null ? new ArrayList<>()
                : deliveryRecordList.stream().map(DeliveryRecord::getDelivery).toList();
    }

    @Override
    public boolean sendDelivery(@NotNull Delivery delivery) {

        // Check if the database is disabled.
        if (this.getDatabase().isDisabled()) {
            return false;
        }

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
        delivery.setBundle(new RewardBundle().setItemList(itemList));

        return this.sendDelivery(delivery);
    }

    private void sendDelivery0(@NotNull DeliverySendEvent event) {

        // Save the delivery to the database.
        this.getDatabase().getTable(DeliveryTable.class)
                .insertRecord(new DeliveryRecord(event.getDelivery()));

        // Attempt to notify the player it was sent to.
        Player player = Bukkit.getPlayer(event.getDelivery().getToPlayerUuid());
        if (player == null) return;

        new PlayerUser(player).sendMessage(this.getConfiguration()
                .getAdaptedString("delivery.receive_message", "\n", "&7You have received a delivery from &f{sender}")
                .replace("{sender}", event.getDelivery().getFromName("null"))
        );
    }

    @EventHandler
    public void onPlayerFirstJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) return;
        if (!this.getConfiguration().getBoolean("first_join.enabled")) return;

        // Convert to a bundle.
        RewardBundle bundle = new RewardBundle().convert(this.getConfiguration().getSection("first_join"));

        // Create the delivery.
        Delivery delivery = new Delivery(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        delivery.setBundle(bundle);
        delivery.setFromName("Server");

        // Send delivery.
        this.sendDelivery(delivery);
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
