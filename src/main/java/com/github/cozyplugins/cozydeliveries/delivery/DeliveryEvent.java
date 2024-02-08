package com.github.cozyplugins.cozydeliveries.delivery;

import com.github.cozyplugins.cozylibrary.indicator.ConfigurationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class DeliveryEvent implements ConfigurationConvertable<DeliveryEvent>, Replicable<DeliveryEvent> {
    @Override
    public @NotNull ConfigurationSection convert() {
        return null;
    }

    @Override
    public @NotNull DeliveryEvent convert(ConfigurationSection section) {
        return null;
    }

    @Override
    public DeliveryEvent duplicate() {
        return null;
    }
}
