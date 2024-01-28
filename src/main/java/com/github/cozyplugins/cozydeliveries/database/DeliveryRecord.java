package com.github.cozyplugins.cozydeliveries.database;

import com.github.cozyplugins.cozydeliveries.Delivery;
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
 */
public class DeliveryRecord extends Record {

    @Field(type = RecordFieldType.PRIMARY)
    private @NotNull String uuid;

    private @NotNull String delivery;

    /**
     * Used to create a new delivery record.
     *
     * @param delivery The instance of the delivery.
     */
    public DeliveryRecord(@NotNull Delivery delivery) {
        this.uuid = UUID.randomUUID().toString();
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
        return new Delivery().convert(
                new MemoryConfigurationSection(
                        new Gson().fromJson(this.delivery, LinkedHashMap.class)
                )
        ).setUuid(UUID.fromString(this.uuid));
    }

    /**
     * Used to change the instance of the delivery.
     *
     * @param delivery The instance of the delivery.
     * @return This instance.
     */
    public @NotNull DeliveryRecord setDelivery(@NotNull Delivery delivery) {
        this.delivery = new Gson().toJson(delivery.convert().getMap());
        this.uuid = delivery.getUuid().toString();
        return this;
    }
}
