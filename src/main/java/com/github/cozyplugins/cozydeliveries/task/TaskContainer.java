package com.github.cozyplugins.cozydeliveries.task;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a class that contains runnable tasks.
 */
public class TaskContainer {

    private static @Nullable TaskContainer instance;
    private @NotNull Map<String, BukkitTask> taskMap;

    /**
     * Used to create a task container.
     */
    public TaskContainer() {
        this.taskMap = new HashMap<>();
    }

    /**
     * Used to register a task within this class.
     *
     * @param identifier The identifier of the task.
     * @param task       The instance of the task.
     */
    public void registerTask(@NotNull String identifier, @NotNull BukkitTask task) {
        this.taskMap.put(identifier, task);
    }

    /**
     * Used to stop a task.
     *
     * @param identifier The task's identifier.
     */
    public void stopTask(@NotNull String identifier) {
        if (!this.taskMap.containsKey(identifier)) return;
        BukkitTask task = this.taskMap.get(identifier);
        this.taskMap.remove(identifier);
        task.cancel();
    }

    /**
     * Used to stop all the tasks located in this class.
     */
    public void stopAllTasks() {
        for (BukkitTask task : this.taskMap.values()) {
            task.cancel();
        }

        this.taskMap = new HashMap<>();
    }

    /**
     * Used to get the instance of the global task container.
     *
     * @return The global task container.
     */
    public static @NotNull TaskContainer getInstance() {

        // Check if the task container instance
        // has not been initialized.
        if (TaskContainer.instance == null) {
            TaskContainer.instance = new TaskContainer();
        }

        return TaskContainer.instance;
    }
}
