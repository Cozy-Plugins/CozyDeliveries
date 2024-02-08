package com.github.cozyplugins.cozydeliveries.delivery.event.type;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventType;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the cool down delivery event type.
 */
public class CooldownDeliveryEventType implements DeliveryEventType {

    @Override
    public void onPlayerJoin(@NotNull Player player, @NotNull ConfigurationSection section) {

    }
}
