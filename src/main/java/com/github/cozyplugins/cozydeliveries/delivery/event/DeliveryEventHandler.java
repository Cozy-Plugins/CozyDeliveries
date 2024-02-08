package com.github.cozyplugins.cozydeliveries.delivery.event;

import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a delivery event handler.
 * Used to handle giving deliveries when
 * the event is executed.
 */
public interface DeliveryEventHandler {

    /**
     * Called when the delivery should be given.
     *
     * @param section The instance of the configuration section
     *                for that delivery event.
     */
    void onEvent(@NotNull ConfigurationSection section);
}
