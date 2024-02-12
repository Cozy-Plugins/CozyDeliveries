/*
 * CozyDeliveries - An item and money delivery service for a minecraft server.
 * Copyright (C) 2024  Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.cozyplugins.cozydeliveries.delivery.event;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozydeliveries.delivery.event.type.DefaultDeliveryEventType;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.smuddgge.squishyconfiguration.indicator.ConfigurationConvertable;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Represents a delivery event in the configuration.
 */
public class DeliveryEvent implements ConfigurationConvertable<DeliveryEvent>, Replicable<DeliveryEvent>, Savable {

    private final @NotNull String identifier;
    private @NotNull ConfigurationSection section;

    /**
     * Used to create a new delivery event.
     *
     * @param identifier The instance of the event's identifier.
     */
    public DeliveryEvent(@NotNull String identifier) {
        this.identifier = identifier;
        this.section = new MemoryConfigurationSection(new LinkedHashMap<>());
    }

    /**
     * Used to get the delivery event's identifier.
     *
     * @return Teh delivery event identifier.
     */
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    /**
     * Used to get the delivery configuration
     * section.
     *
     * @return The delivery configuration section.
     */
    public @NotNull ConfigurationSection getConfigurationSection() {
        return this.section;
    }

    /**
     * Used to get the event type used
     * in this event.
     * This can be used to get the event handler.
     *
     * @return The event type.
     */
    public @NotNull DeliveryEventType getType() {
        Optional<DeliveryEventType> optional = DeliveryEventType.getEventType(this.section.getString("type", "default"));
        return optional.orElseGet(this::getDefaultWithWarning);
    }

    private @NotNull DefaultDeliveryEventType getDefaultWithWarning() {
        CozyDeliveries.getPlugin().getLogger().log(Level.WARNING,
                "Incorrect delivery event type for " + this.identifier + ". It was " + this.section.getString("type", "null")
        );
        return new DefaultDeliveryEventType();
    }

    @Override
    public DeliveryEvent duplicate() {
        return new DeliveryEvent(this.identifier).convert(this.convert());
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        return this.section;
    }

    @Override
    public @NotNull DeliveryEvent convert(@NotNull ConfigurationSection section) {
        this.section = section;
        return this;
    }

    @Override
    public void save() {
        CozyDeliveries.getAPI().orElseThrow()
                .getEventConfiguration()
                .insertType(this.identifier, this);
    }
}
