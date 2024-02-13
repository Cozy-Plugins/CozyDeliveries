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
 * Represents the player record class.
 * Contains player statistics.
 */
public class PlayerRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public @NotNull String playerUuid;
    public int deliveriesSent = 0;
    public int deliveriesReceived = 0;

    public PlayerRecord() {
        this.playerUuid = "null";
    }

    /**
     * Used to create a player record.
     *
     * @param playerUuid The player's uuid.
     */
    public PlayerRecord(@NotNull UUID playerUuid) {
        this.playerUuid = playerUuid.toString();
    }

    public @NotNull PlayerRecord incrementSent(int amount) {
        this.deliveriesSent += amount;
        return this;
    }

    public @NotNull PlayerRecord incrementReceived(int amount) {
        this.deliveriesReceived += amount;
        return this;
    }
}
