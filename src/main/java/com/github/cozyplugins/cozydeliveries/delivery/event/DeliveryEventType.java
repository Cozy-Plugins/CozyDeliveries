package com.github.cozyplugins.cozydeliveries.delivery.event;

import com.github.cozyplugins.cozydeliveries.delivery.event.type.CooldownDeliveryEventType;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the delivery event type.
 * Contains different types that can be specified in
 * the event configuration directory.
 */
public interface DeliveryEventType {

    /**
     * Used to get the delivery event handler
     * for this event type.
     *
     * @return Teh delivery event handler.
     */
    @NotNull DeliveryEventHandler getDeliveryEventHandler();

    /**
     * Called when a player joins the server.
     *
     * @param event The instance of the event.
     * @param deliveryEvent The instance of the delivery event.
     */
    void onPlayerJoin(@NotNull PlayerJoinEvent event, @NotNull DeliveryEvent deliveryEvent);

    /**
     * Called when a player leaves the server.
     *
     * @param event The instance of the event.
     * @param deliveryEvent The instance of the delivery event.
     */
    void onPlayerLeave(@NotNull PlayerKickEvent event, @NotNull DeliveryEvent deliveryEvent);

    /**
     * Used to attempt to get the event type
     * of specific identifier.
     *
     * @param identifier The identifier to look for.
     * @return The instance of the delivery event type
     * that matches the identifier.
     */
     static @NotNull Optional<DeliveryEventType> getEventType(@NotNull String identifier) {
        return switch (identifier.toLowerCase()) {
            case "cooldown" -> Optional.of(new CooldownDeliveryEventType());
            default -> Optional.empty();
        };
    }
}
