package com.github.cozyplugins.cozydeliveries.configuration;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the event configuration directory.
 * Contains events when deliveries should be given.
 */
public class EventConfigurationDirectory extends CozyDeliveriesConfigurationDirectory<DeliveryEvent> {

    /**
     * Used to create a new event configuration directory instance.
     */
    public EventConfigurationDirectory() {
        super("events", "events.yml");
    }

    @Override
    public @NotNull DeliveryEvent createEmpty(@NotNull String identifier) {
        return new DeliveryEvent(identifier);
    }

    @Override
    public void onReload() {

    }
}
