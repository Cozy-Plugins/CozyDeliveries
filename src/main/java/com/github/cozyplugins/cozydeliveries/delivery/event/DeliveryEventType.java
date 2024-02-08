package com.github.cozyplugins.cozydeliveries.delivery.event;

import com.github.cozyplugins.cozydeliveries.delivery.event.type.CooldownDeliveryEventType;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the delivery event type.
 * Contains different types that can be specified in
 * the event configuration directory.
 */
public interface DeliveryEventType {

    /**
     * Called when a player joins the server.
     *
     * @param player The instance of the player.
     * @param section The delivery event's configuration section.
     */
    void onPlayerJoin(@NotNull Player player, @NotNull ConfigurationSection section);

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
