package com.github.cozyplugins.cozydeliveries;

import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the cozy deliveries api interface.
 * Contains the methods that can be accessed
 * by other plugins.
 */
public interface CozyDeliveriesAPI {

    /**
     * Used to get the plugin's main configuration file.
     *
     * @return The config file.
     */
    @NotNull Configuration getConfiguration();

    /**
     * Used to get the instance of the database
     * used to store the deliveries.
     * Be careful not to call this method before
     * the plugin has started.
     *
     * @return The instance of the database.
     */
    @NotNull Database getDatabase();
}
