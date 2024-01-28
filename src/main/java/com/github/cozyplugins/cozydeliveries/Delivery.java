package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozylibrary.indicator.ConfigurationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.reward.RewardBundle;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Represents a delivery of items sent to a player.
 */
public class Delivery implements ConfigurationConvertable<Delivery>, Replicable<Delivery>, Savable {

    private @NotNull UUID uuid;
    private @NotNull RewardBundle bundle;

    /**
     * Used to create a new instance of a delivery.
     */
    public Delivery() {
        this.uuid = UUID.randomUUID();
        this.bundle = new RewardBundle();
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

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        section.set("package", this.bundle.convert());

        return section;
    }

    @Override
    public @NotNull Delivery convert(ConfigurationSection section) {

        this.bundle = new RewardBundle().convert(section.getSection("package"));

        return this;
    }

    @Override
    public Delivery duplicate() {
        return new Delivery().convert(this.convert());
    }

    @Override
    public void save() {

    }
}
