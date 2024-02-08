package com.github.cozyplugins.cozydeliveries.delivery.event.type;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventType;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the default delivery event.
 * This is used when the type is not stated.
 */
public class DefaultDeliveryEventType implements DeliveryEventType {

    @Override
    public void onPlayerJoin(@NotNull Player player, @NotNull ConfigurationSection section) {

    }
}
