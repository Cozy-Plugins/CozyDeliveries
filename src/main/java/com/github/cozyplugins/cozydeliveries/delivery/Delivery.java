package com.github.cozyplugins.cozydeliveries.delivery;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozydeliveries.database.DeliveryRecord;
import com.github.cozyplugins.cozydeliveries.database.DeliveryTable;
import com.github.cozyplugins.cozylibrary.indicator.ConfigurationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * Represents a delivery to be given to a player.
 */
public class Delivery implements ConfigurationConvertable<Delivery>, Replicable<Delivery>, Savable {

    private @NotNull UUID uuid;
    private @NotNull UUID toPlayerUuid;
    private @Nullable String fromName;
    private @NotNull Long timeStampMillis;
    private @NotNull Long timeStampExpire;
    private @NotNull DeliveryContent deliveryContent;

    /**
     * Used to create a new instance of a delivery.
     *
     * @param toPlayerUuid The player it will be delivered to.
     * @param timeStampMillis The time stamp it was sent to the player.
     */
    public Delivery(@NotNull UUID toPlayerUuid, @NotNull Long timeStampMillis) {
        this.uuid = UUID.randomUUID();
        this.toPlayerUuid = toPlayerUuid;
        this.timeStampMillis = timeStampMillis;
        this.timeStampExpire = -1L;
        this.deliveryContent = new DeliveryContent();
    }

    /**
     * Used to get the delivery's uuid.
     *
     * @return The delivery's uuid.
     */
    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    /**
     * Used to get the player's uuid that the
     * delivery was sent to.
     *
     * @return The player's uuid.
     */
    public @NotNull UUID getToPlayerUuid() {
        return this.toPlayerUuid;
    }

    /**
     * Used to get the name of the sender.
     *
     * @return The name of the sender.
     */
    public @Nullable String getFromName() {
        return this.fromName;
    }

    /**
     * Used to get the name of the sender.
     * If the sender is null it will return the alternative.
     *
     * @param alternative The name of the alternative sender.
     * @return The sender.
     */
    public @NotNull String getFromName(@NotNull String alternative) {
        return this.fromName == null ? alternative : this.fromName;
    }

    /**
     * Used to get the time stamp that
     * the delivery was sent.
     *
     * @return The time stamp it was sent.
     */
    public long getTimeStampMillis() {
        return this.timeStampMillis;
    }

    /**
     * Used to get the time stamp which this
     * delivery will expire.
     * This defaults to -1 for no delivery expire.
     *
     * @return The time stamp the delivery
     * will expire.
     */
    public long getTimeStampExpire() {
        return this.timeStampExpire;
    }

    /**
     * Used to get the expired time formatted
     * as hours, minutes and seconds.
     *
     * @return The formatted expired time.
     */
    public @NotNull String getExpireTimeFormatted() {
        if (!this.hasExpireDate()) return "None";
        Duration duration = Duration.ofMillis(this.getTimeStampExpire() - System.currentTimeMillis());
        return duration.toHours() + "h " + duration.toMinutesPart() + "m " + duration.toSecondsPart() + "s ";
    }

    /**
     * Used to get the reward bundle that will be
     * given to the player.
     *
     * @return The reward bundle.
     */
    public @NotNull DeliveryContent getDeliveryContent() {
        return this.deliveryContent;
    }

    /**
     * Used to set the uuid to a new uuid.
     * Be careful as this could cause unintended
     * side effects.
     * This is mainly used for the database.
     *
     * @param uuid The uuid of this delivery.
     * @return This instance.
     */
    public @NotNull Delivery setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Used to set the name of the sender.
     *
     * @param fromName The name of the sender.
     * @return This instance.
     */
    public @NotNull Delivery setFromName(@Nullable String fromName) {
        this.fromName = fromName;
        return this;
    }

    /**
     * Used to set the time stamp in milliseconds.
     *
     * @param timeStampMillis The time stamp.
     * @return This instance.
     */
    public @NotNull Delivery setTimeStampMillis(@NotNull Long timeStampMillis) {
        this.timeStampMillis = timeStampMillis;
        return this;
    }

    /**
     * Used to set when the delivery should expire.
     *
     * @param timeStampExpire The time stamp for when the delivery should expire.
     * @return This instance.
     */
    public @NotNull Delivery setTimeStampExpire(@NotNull Long timeStampExpire) {
        this.timeStampExpire = timeStampExpire;
        return this;
    }

    /**
     * Used to set the bundle of rewards to a specific
     * instance.
     *
     * @param deliveryContent The instance of a delivery content.
     * @return This instance.
     */
    public @NotNull Delivery setDeliveryContent(@NotNull DeliveryContent deliveryContent) {
        this.deliveryContent = deliveryContent;
        return this;
    }

    /**
     * Used to check if the delivery should
     * expire.
     *
     * @return True if the delivery should expire.
     */
    public boolean hasExpireDate() {
        return this.timeStampExpire > -1L;
    }

    /**
     * Used to check if the delivery has expired.
     * Normally using the get methods in the api,
     * they will already be removed from a database
     * before returning the deliveries if expired.
     *
     * @return True if the delivery has expired.
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() > this.timeStampExpire;
    }

    /**
     * Used to check if a user has enough inventory space to
     * obtain the delivery.
     *
     * @param user The instance of the user.
     * @return True if they have enough room.
     */
    public boolean hasInventorySpace(@NotNull PlayerUser user) {
        return this.deliveryContent.hasInventorySpace(user);
    }

    /**
     * Used to delete the delivery from the
     * database and then give it to a user.
     *
     * @param user The instance of the user.
     * @return True if successful.
     */
    public boolean giveAndDelete(@NotNull PlayerUser user) {

        // Remove the record from the database.
        boolean success = CozyDeliveries.getAPI().orElseThrow().getDatabase()
                .getTable(DeliveryTable.class)
                .removeAllRecords(new Query().match("uuid", this.uuid.toString()));

        if (!success) return false;
        return this.deliveryContent.give(user);
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        section.set("to_player_uuid", this.toPlayerUuid.toString());
        section.set("from_name", this.fromName);
        section.set("time_stamp_millis", this.timeStampMillis);
        section.set("content", this.deliveryContent.convert().getMap());

        return section;
    }

    @Override
    public @NotNull Delivery convert(ConfigurationSection section) {

        this.toPlayerUuid = UUID.fromString(section.getString("to_player_uuid"));
        this.fromName = section.getString("from_name");
        this.timeStampMillis = section.getLong("time_stamp_millis");
        this.deliveryContent = new DeliveryContent().convert(section.getSection("content"));

        return this;
    }

    @Override
    public Delivery duplicate() {
        return new Delivery(this.toPlayerUuid, this.timeStampMillis).convert(this.convert());
    }

    /**
     * Used to save this class to the database.
     * Be carefully not to call this method too many
     * times as it will execute database operations.
     */
    @Override
    public void save() {
        CozyDeliveries.getAPI().orElseThrow()
                .getDatabase()
                .getTable(DeliveryTable.class)
                .insertRecord(new DeliveryRecord(this));
    }
}
