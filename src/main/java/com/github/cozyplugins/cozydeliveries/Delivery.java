package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozydeliveries.database.DeliveryRecord;
import com.github.cozyplugins.cozydeliveries.database.DeliveryTable;
import com.github.cozyplugins.cozylibrary.indicator.ConfigurationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.cozyplugins.cozylibrary.reward.RewardBundle;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Represents a delivery to be given to a player.
 */
public class Delivery implements ConfigurationConvertable<Delivery>, Replicable<Delivery>, Savable {

    private @NotNull UUID uuid;
    private @NotNull UUID toPlayerUuid;
    private @Nullable String fromName;
    private @NotNull Long timeStampMillis;
    private @NotNull RewardBundle bundle;

    /**
     * Used to create a new instance of a delivery.
     */
    public Delivery(@NotNull UUID toPlayerUuid, @NotNull Long timeStampMillis) {
        this.uuid = UUID.randomUUID();
        this.toPlayerUuid = toPlayerUuid;
        this.bundle = new RewardBundle();
        this.timeStampMillis = timeStampMillis;
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

    public @NotNull Long getTimeStampMillis() {
        return this.timeStampMillis;
    }

    /**
     * Used to get the reward bundle that will be
     * given to the player.
     *
     * @return The reward bundle.
     */
    public @NotNull RewardBundle getBundle() {
        return this.bundle;
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
     * Used to set the bundle of rewards to a specific
     * instance.
     *
     * @param bundle The instance of a reward bundle.
     * @return This instance.
     */
    public @NotNull Delivery setBundle(@NotNull RewardBundle bundle) {
        this.bundle = bundle;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        section.set("to_player_uuid", this.toPlayerUuid.toString());
        section.set("from_name", this.fromName);
        section.set("time_stamp_millis", this.timeStampMillis);
        section.set("package", this.bundle.convert());

        return section;
    }

    @Override
    public @NotNull Delivery convert(ConfigurationSection section) {

        this.toPlayerUuid = UUID.fromString(section.getString("to_player_uuid"));
        this.fromName = section.getString("from_name");
        this.timeStampMillis = section.getLong("time_stamp_millis");
        this.bundle = new RewardBundle().convert(section.getSection("package"));

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
