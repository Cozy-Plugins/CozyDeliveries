package com.github.cozyplugins.cozydeliveries.configuration;

import com.github.cozyplugins.cozydeliveries.delivery.DeliveryContent;
import org.jetbrains.annotations.NotNull;

public class ContentConfigurationDirectory extends CozyDeliveriesConfigurationDirectory<DeliveryContent> {

    /**
     * Used to create a new event configuration directory instance.
     */
    public ContentConfigurationDirectory() {
        super("contents", "contents.yml");
    }

    @Override
    public @NotNull DeliveryContent createEmpty(@NotNull String identifier) {
        return new DeliveryContent();
    }

    @Override
    public void onReload() {

    }
}
