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

package com.github.cozyplugins.cozydeliveries.database;

import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the cooldown record.
 */
public class CooldownRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public @NotNull String identifier;

    public @NotNull String playerUuid;
    public @NotNull String eventIdentifier;
    public @NotNull String lastDeliveryTimeStampMillis;

    public CooldownRecord() {

    }

    /**
     * Used to create a new player cool down record
     * for a specific event identifier.
     *
     * @param playerUuid The player's uuid.
     * @param eventIdentifier The event identifier.
     * @param lastDeliveryTimeStampMillis The last delivery for this
     *                                    event as a time stamp.
     */
    public CooldownRecord(@NotNull UUID playerUuid, @NotNull String eventIdentifier, long lastDeliveryTimeStampMillis) {
        this.identifier = UUID.randomUUID().toString();
        this.playerUuid = playerUuid.toString();
        this.eventIdentifier = eventIdentifier;
        this.lastDeliveryTimeStampMillis = Long.toString(lastDeliveryTimeStampMillis);
    }

    /**
     * Used to get the cooldown identifier.
     *
     * @return The unique identifier
     * for this cooldown.
     */
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    /**
     * Used to get the player's uuid that
     * the cool down is for.
     *
     * @return The player's uuid.
     */
    public @NotNull UUID getPlayerUuid() {
        return UUID.fromString(this.playerUuid);
    }

    /**
     * Used to get the event identifier
     * specified in the configuration directory.
     *
     * @return The event identifier.
     */
    public @NotNull String getEventIdentifier() {
        return this.eventIdentifier;
    }

    /**
     * Used to get the last delivery sent to the player
     * as a time stamp in milliseconds.
     *
     * @return The last delivery time stamp.
     */
    public long getLastDeliveryTimeStampMillis() {
        return Long.parseLong(this.lastDeliveryTimeStampMillis);
    }

    /**
     * Used to set the last delivery time
     * stamp to now.
     *
     * @return This instance.
     */
    public @NotNull CooldownRecord setLastDeliveryTimeStampToNow() {
        this.lastDeliveryTimeStampMillis = Long.toString(System.currentTimeMillis());
        return this;
    }
}
