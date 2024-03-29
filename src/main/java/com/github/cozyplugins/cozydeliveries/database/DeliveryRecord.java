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

import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import com.github.smuddgge.squishydatabase.record.Field;
import com.github.smuddgge.squishydatabase.record.Record;
import com.github.smuddgge.squishydatabase.record.RecordFieldType;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Represents the delivery record.
 * Contains records of deliveries that have
 * not been opened.
 */
public class DeliveryRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    public @NotNull String uuid;

    public @NotNull String toPlayerUuid;
    public @NotNull String timeStampMillis;
    public @NotNull String delivery;

    public DeliveryRecord() {
    }

    /**
     * Used to create a new delivery record.
     *
     * @param delivery The instance of the delivery.
     */
    public DeliveryRecord(@NotNull Delivery delivery) {
        this.uuid = UUID.randomUUID().toString();
        this.toPlayerUuid = delivery.getToPlayerUuid().toString();
        this.timeStampMillis = Long.toString(delivery.getTimeStampMillis());
        this.delivery = new Gson().toJson(delivery.convert().getMap());
    }

    /**
     * Used to convert and get the instance of the delivery.
     * Try not to call this method too many times as it
     * uses gson to convert the json string into the class instance.
     *
     * @return The instance.
     */
    public @NotNull Delivery getDelivery() {
        return new Delivery(UUID.fromString(this.toPlayerUuid), Long.parseLong(this.timeStampMillis)).convert(
                new MemoryConfigurationSection(
                        new Gson().fromJson(this.delivery, LinkedHashMap.class)
                )
        ).setUuid(UUID.fromString(this.uuid));
    }

    /**
     * Used to change the instance of the delivery.
     * This will also update all other fields.
     * If the uuid has been changed the database
     * may interpret this as a new entry.
     *
     * @param delivery The instance of the delivery.
     * @return This instance.
     */
    public @NotNull DeliveryRecord setDelivery(@NotNull Delivery delivery) {
        this.uuid = delivery.getUuid().toString();
        this.toPlayerUuid = delivery.getToPlayerUuid().toString();
        this.timeStampMillis = Long.toString(delivery.getTimeStampMillis());
        this.delivery = new Gson().toJson(delivery.convert().getMap());
        return this;
    }
}
