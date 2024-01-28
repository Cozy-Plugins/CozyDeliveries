package com.github.cozyplugins.cozydeliveries;

import com.github.cozyplugins.cozydeliveries.command.DeliveryCommand;
import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.smuddgge.squishyconfiguration.ConfigurationFactory;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import com.github.smuddgge.squishydatabase.DatabaseCredentials;
import com.github.smuddgge.squishydatabase.DatabaseFactory;
import com.github.smuddgge.squishydatabase.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents the main plugin class.
 * Using the cozy library to handle commands etc.
 */
public final class CozyDeliveries extends CozyPlugin implements CozyDeliveriesAPI {

    private @Nullable Configuration config;
    private @Nullable Database database;

    @Override
    public boolean enableCommandDirectory() {
        return true;
    }

    @Override
    public void onCozyEnable() {

        // Initialize the configuration file.
        this.config = ConfigurationFactory.YAML.create(
                this.getDataFolder(), "config.yml"
        );
        this.config.setDefaultPath("src/main/resources/config.yml");
        this.config.load();
        
        // Initialize the database.
        this.setupDatabase();

        // Register the commands.
        this.addCommandType(new DeliveryCommand());
    }

    private void setupDatabase() {

        // Get the instance of the database factory.
        DatabaseFactory factory = DatabaseFactory.valueOf(this.getConfiguration().getString("database.type"));

        if (factory.equals(DatabaseFactory.SQLITE)) {
            this.database = factory.create(DatabaseCredentials.SQLITE(
                    this.getDataFolder().getAbsolutePath() + "/database.sqlite"
            ));
        }

        if (factory.equals(DatabaseFactory.MONGO)) {
            this.database = factory.create(DatabaseCredentials.MONGO(
                    this.getConfiguration().getString("database.connection_string"),
                    this.getConfiguration().getString("database.database_name")
            ));
        }

        if (factory.equals(DatabaseFactory.MYSQL)) {
            this.database = factory.create(DatabaseCredentials.MYSQL(
                    this.getConfiguration().getString("database.connection_string")
            ));
        }
    }

    @Override
    public @NotNull Configuration getConfiguration() {

        // Check if the config is null.
        if (this.config == null) throw new RuntimeException(
                "Tried to get the config but the config has not been initialized yet."
        );

        return this.config;
    }

    @Override
    public @NotNull Database getDatabase() {

        // Check if the database is null.
        if (this.database == null) throw new RuntimeException(
                "Tried to get the database but the database has not been initialized yet."
        );

        return this.database;
    }

    /**
     * Used to get the instance of the plugin api methods
     * from bukkit.
     *
     * @return The optional instance of the api.
     */
    public static @NotNull Optional<CozyDeliveriesAPI> getAPI() {

        // Get the instance of the plugin.
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CozyDeliveries");

        // Check if it doesn't exist.
        if (plugin == null) return Optional.empty();
        if (plugin instanceof CozyDeliveries) return Optional.of((CozyDeliveries) plugin);
        return Optional.empty();
    }
}
