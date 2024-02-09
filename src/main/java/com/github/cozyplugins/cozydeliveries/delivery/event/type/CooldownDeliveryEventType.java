package com.github.cozyplugins.cozydeliveries.delivery.event.type;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozydeliveries.database.CooldownRecord;
import com.github.cozyplugins.cozydeliveries.database.CooldownTable;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventHandler;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventType;
import com.github.cozyplugins.cozydeliveries.delivery.event.handler.StandardDeliveryEventHandler;
import com.github.cozyplugins.cozydeliveries.task.TaskContainer;
import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.smuddgge.squishydatabase.Query;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the cool down delivery event type.
 */
public class CooldownDeliveryEventType implements DeliveryEventType {

    @Override
    public @NotNull DeliveryEventHandler getDeliveryEventHandler() {
        return new StandardDeliveryEventHandler();
    }

    @Override
    public void onPlayerJoin(@NotNull PlayerJoinEvent event, @NotNull DeliveryEvent deliveryEvent) {

        // Get the task cooldown.
        final int cooldown = CozyDeliveries.getAPI().orElseThrow()
                .getConfiguration().getInteger("events.cooldown_check_ticks", 500);

        // Start the task.
        BukkitScheduler scheduler = CozyPlugin.getPlugin().getServer().getScheduler();
        BukkitTask task = scheduler.runTaskTimer(
                CozyPlugin.getPlugin(),
                () -> {

                    // Check the player is still online.
                    if (!Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .toList()
                            .contains(event.getPlayer().getName())) {

                        // Stop the task.
                        TaskContainer.getInstance().stopTask(
                                this.getTaskIdentifier(event.getPlayer().getUniqueId(), deliveryEvent.getIdentifier())
                        );

                        return;
                    }

                    // Check for delivery cooldown.
                    this.checkForDeliveryCooldown(
                            event.getPlayer().getUniqueId(),
                            deliveryEvent.getIdentifier(),
                            deliveryEvent
                    );
                },
                cooldown, cooldown
        );

        // Register the task with the global task container.
        TaskContainer.getInstance().registerTask(
                this.getTaskIdentifier(event.getPlayer().getUniqueId(), deliveryEvent.getIdentifier()),
                task
        );
    }

    @Override
    public void onPlayerLeave(@NotNull PlayerKickEvent event, @NotNull DeliveryEvent deliveryEvent) {

        // Stop the task registered with the global task container.
        TaskContainer.getInstance().stopTask(
                this.getTaskIdentifier(event.getPlayer().getUniqueId(), deliveryEvent.getIdentifier())
        );
    }

    /**
     * Used to check for the delivery cooldown.
     * Be careful as this contains database methods.
     */
    public void checkForDeliveryCooldown(@NotNull UUID playerUuid, @NotNull String eventIdentifier, @NotNull DeliveryEvent deliveryEvent) {

        // Attempt to get the record from the database.
        CooldownRecord record = CozyDeliveries.getAPI().orElseThrow()
                .getDatabase()
                .getTable(CooldownTable.class)
                .getFirstRecord(new Query()
                        .match("playerUuid", playerUuid.toString())
                        .match("eventIdentifier", eventIdentifier)
                );

        // Check if the record does not exist.
        if (record == null) {
            record = new CooldownRecord(
                    playerUuid,
                    eventIdentifier,
                    0L
            );
        }

        // Get the time in the future to wait for
        // giving the next delivery.
        long timeStampToWaitFor = record.getLastDeliveryTimeStampMillis()
                + (deliveryEvent.getConfigurationSection().getInteger("cooldown_seconds", 86400) * 1000L);

        // Check if it is not time yet.
        if (timeStampToWaitFor > System.currentTimeMillis()) return;

        // Otherwise first update the database
        // to stop duplication bugs.
        record.setLastDeliveryTimeStampToNow();
        CozyDeliveries.getAPI().orElseThrow().getDatabase()
                .getTable(CooldownTable.class)
                .insertRecord(record);

        // Finally, give the delivery.
        this.getDeliveryEventHandler().onEvent(deliveryEvent, playerUuid);
    }

    /**
     * Used get the unique task identifier.
     *
     * @param playerUuid      The player's uuid.
     * @param eventIdentifier The event's identifier.
     * @return The instance of the task identifier.
     */
    public @NotNull String getTaskIdentifier(@NotNull UUID playerUuid, @NotNull String eventIdentifier) {
        return playerUuid + eventIdentifier;
    }
}
