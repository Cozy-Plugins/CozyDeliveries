package com.github.cozyplugins.cozydeliveries.delivery.event;

import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a delivery event handler.
 * Used to handle giving deliveries when
 * the event is executed.
 */
public interface DeliveryEventHandler {

    /**
     * Called when the delivery should be given.
     *
     * @param event The instance of the delivery event.
     * @param playerUuid The instance of the player uuid to give the delivery too.
     */
    void onEvent(@NotNull DeliveryEvent event, @NotNull UUID playerUuid);
}
