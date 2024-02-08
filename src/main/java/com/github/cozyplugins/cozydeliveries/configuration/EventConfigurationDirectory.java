package com.github.cozyplugins.cozydeliveries.configuration;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the event configuration directory.
 * Contains events when deliveries should be given.
 */
public class EventConfigurationDirectory extends CozyDeliveriesConfigurationDirectory<DeliveryEvent> {

    private @NotNull List<DeliveryEvent> eventList;

    /**
     * Used to create a new event configuration directory instance.
     */
    public EventConfigurationDirectory() {
        super("events", "events.yml");

        this.eventList = new ArrayList<>();
    }

    @Override
    public @NotNull DeliveryEvent createEmpty(@NotNull String identifier) {
        return new DeliveryEvent(identifier);
    }

    @Override
    public void onReload() {

        // Reset the list.
        this.eventList = new ArrayList<>();

        // Populate the list.
        this.eventList.addAll(this.getAllTypes());
    }

    /**
     * Used to call the player join event for all delivery events.
     *
     * @param event The instance of the event.
     */
    public void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        for (DeliveryEvent deliveryEvent : this.eventList) {
            deliveryEvent.getType().onPlayerJoin(event, deliveryEvent);
        }
    }

    /**
     * Used to call the player leave event for all the delivery events.
     *
     * @param event The isntance of the event.
     */
    public void onPlayerLeaveEvent(@NotNull PlayerKickEvent event) {
        for (DeliveryEvent deliveryEvent : this.eventList) {
            deliveryEvent.getType().onPlayerLeave(event, deliveryEvent);
        }
    }
}
