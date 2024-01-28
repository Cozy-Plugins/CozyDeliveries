package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the cozy deliveries api interface.
 * Contains the methods that can be accessed
 * by other plugins.
 * <p>
 * To get the instance of the api you can use
 * the method {@link CozyDeliveries#getAPI()}.
 */
public interface CozyDeliveriesAPI {

    /**
     * Used to get the plugin's main configuration file.
     *
     * @return The config file.
     */
    @NotNull Configuration getConfiguration();

    /**
     * Used to get the instance of the database
     * used to store the deliveries.
     * Be careful not to call this method before
     * the plugin has started.
     *
     * @return The instance of the database.
     */
    @NotNull Database getDatabase();

    /**
     * Used to get the instance of a specific delivery
     * from the database.
     *
     * @param uuid The delivery's identifier.
     * @return The optional delivery.
     */
    @NotNull Optional<Delivery> getDelivery(@NotNull UUID uuid);

    /**
     * Used to get a player's list of deliveries.
     *
     * @param playerUuid The player's uuid.
     * @return The list of deliveries that has
     * been sent to the player.
     */
    @NotNull List<Delivery> getDeliveryList(@NotNull UUID playerUuid);

    /**
     * Used to send a delivery to a player.
     *
     * @param delivery The instance of a delivery.
     * @return True if the delivery was sent.
     */
    boolean sendDelivery(@NotNull Delivery delivery);

    /**
     * Used to send a delivery to a player.
     *
     * @param playerUuid The player's uuid to send to.
     * @param fromName   The name of the sender.
     * @param items      The list of items to send.
     * @return True if the delivery was sent.
     */
    boolean sendDelivery(@NotNull UUID playerUuid, @Nullable String fromName, @NotNull CozyItem... items);
}
