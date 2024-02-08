package com.github.cozyplugins.cozydeliveries.delivery.event.type;

import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventHandler;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventType;
import com.github.cozyplugins.cozydeliveries.delivery.event.handler.StandardDeliveryEventHandler;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the default delivery event.
 * This is used when the type is not stated.
 */
public class DefaultDeliveryEventType implements DeliveryEventType {

    @Override
    public @NotNull DeliveryEventHandler getDeliveryEventHandler() {
        return new StandardDeliveryEventHandler();
    }

    @Override
    public void onPlayerJoin(@NotNull PlayerJoinEvent event, @NotNull DeliveryEvent deliveryEvent) {

    }

    @Override
    public void onPlayerLeave(@NotNull PlayerKickEvent event, @NotNull DeliveryEvent deliveryEvent) {

    }
}
