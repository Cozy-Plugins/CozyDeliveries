package com.github.cozyplugins.cozydeliveries.delivery;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.indicator.ConfigurationConvertable;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

/**
 * Represents the content of a delivery.
 */
public class DeliveryContent implements ConfigurationConvertable<DeliveryContent>, Replicable<DeliveryContent> {

    private @NotNull List<CozyItem> itemList;
    private @NotNull List<String> commandList;
    private int money;

    private @NotNull List<String> lore;
    private @Nullable CozyItem item;

    /**
     * Used to create a new instance
     * of a delivery content.
     */
    public DeliveryContent() {
        this.itemList = new ArrayList<>();
        this.commandList = new ArrayList<>();
        this.money = 0;
        this.lore = new ArrayList<>();
        this.item = null;
    }

    /**
     * Used to get the instance of
     * the item list.
     *
     * @return The instance of the item list.
     */
    public @NotNull List<CozyItem> getItemList() {
        return this.itemList;
    }

    /**
     * Used to get the item list as a
     * configuration section.
     *
     * @return The instance of the item list
     * as a configuration section.
     */
    public @NotNull ConfigurationSection getItemListAsConfigurationSection() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());
        int index = 0;

        for (CozyItem item : this.itemList) {
            try {
                section.set(Integer.toString(index), item.convert().getMap());
            } catch (Exception exception) {
                CozyDeliveries.getPlugin().getLogger().log(Level.WARNING, "Failed to convert item with index " + index);
                if (item != null) CozyDeliveries.getPlugin().getLogger().log(Level.WARNING, item.getMaterial().name());
                throw new RuntimeException(exception);
            }
            index++;
        }

        return section;
    }

    /**
     * Used to get the instance of the
     * command list that will be executed when
     * given to a player.
     *
     * @return The instance of the command list.
     */
    public @NotNull List<String> getCommandList() {
        return this.commandList;
    }

    /**
     * Used to get the amount of money that
     * will be given to the player.
     *
     * @return The amount of money to give to
     * the player.
     */
    public int getMoney() {
        return this.money;
    }

    /**
     * Used to get the delivery content lore.
     *
     * @return The specific lore for this delivery.
     */
    public @NotNull List<String> getLore() {
        return this.lore;
    }

    /**
     * Used to get the lore as default.
     *
     * @return The default lore.
     */
    public @NotNull List<String> getDefaultLore() {

        // Get the default lore section.
        ConfigurationSection section = CozyDeliveries.getAPI().orElseThrow()
                .getConfiguration().getSection("delivery").getSection("default_lore");

        // Get the base lore string formatting.
        String loreString = section.getAdaptedString("format", "\n", "{money}\n{items}\n{commands}")
                .replace("{money}",
                        this.money > 0
                                ? section.getAdaptedString("money", "\n", "&7Money &a{money}")
                                .replace("{money}", Integer.toString(this.money))
                                : ""
                )
                .replace("{items}",
                        !this.itemList.isEmpty()
                                ? section.getAdaptedString("items.format", "\n", "&7Items\n{items}")
                                .replace("{items}", this.getItemLore(section))
                                : ""
                )
                .replace("{commands}",
                        !this.commandList.isEmpty()
                                ? section.getAdaptedString("commands.format", "\n", "&7Commands\n{commands}")
                                .replace("{commands}", this.getCommandLore(section))
                                : ""
                );

        return List.of(loreString.split("\n"));
    }

    private @NotNull String getItemLore(@NotNull ConfigurationSection section) {
        List<String> lore = new ArrayList<>();

        for (CozyItem item : this.itemList) {
            lore.add(section.getAdaptedString("items.items", "\n", "&7- &f{item}")
                    .replace("{item}", item.getMaterial().name())
            );
        }

        return String.join("\n", lore);
    }

    private @NotNull String getCommandLore(@NotNull ConfigurationSection section) {
        List<String> lore = new ArrayList<>();

        for (String command : this.commandList) {
            lore.add(section.getAdaptedString("commands.commands", "\n", "&7- &f{command}")
                    .replace("{command}", command)
            );
        }

        return String.join("\n", lore);
    }

    /**
     * Checks if the lore is empty if so it
     * returns the default lore.
     *
     * @return The instance of the lore.
     */
    public @NotNull List<String> getLoreNotEmpty() {
        return this.lore.isEmpty() ? this.getDefaultLore() : this.getLore();
    }

    /**
     * Used to get the item that can be used as
     * the interface for the content.
     *
     * @return The instance of the item.
     */
    public @Nullable CozyItem getItem() {
        return this.item;
    }

    /**
     * Used to add items to the content to give.
     *
     * @param items The instance of the items.
     * @return This instance.
     */
    public @NotNull DeliveryContent addItems(@NotNull CozyItem... items) {
        this.itemList.addAll(List.of(items));
        return this;
    }

    /**
     * Used to add items to the content to give.
     *
     * @param itemList The list of items.
     * @return This instance.
     */
    public @NotNull DeliveryContent addItems(@NotNull List<CozyItem> itemList) {
        this.itemList.addAll(itemList);
        return this;
    }

    /**
     * Used to add commands to the content.
     *
     * @param commands The commands to add.
     * @return This instance.
     */
    public @NotNull DeliveryContent addCommands(@NotNull String... commands) {
        this.commandList.addAll(List.of(commands));
        return this;
    }

    /**
     * Used to set the amount of money to
     * give the player.
     *
     * @param money The amount of money to give.
     * @return This instance.
     */
    public @NotNull DeliveryContent setMoney(int money) {
        this.money = money;
        return this;
    }

    /**
     * Used to set the custom item that should
     * be used as the item interface for this
     * delivery.
     *
     * @param item The custom item.
     * @return This instance.
     */
    public @NotNull DeliveryContent setCustomItem(@NotNull CozyItem item) {
        this.item = item;
        return this;
    }

    /**
     * Used to check if the player has enough
     * room in there inventory to get the delivery.
     *
     * @param user The instance of the user to check.
     * @return True if they have enough inventory space.
     */
    public boolean hasInventorySpace(@NotNull PlayerUser user) {
        return this.itemList.size() <= Arrays.stream(user.getPlayer().getInventory().getContents())
                .filter(Objects::isNull).toList().size();
    }

    /**
     * Used to clone and give the content of
     * the delivery to a player.
     *
     * @param user The instance of the user to
     *             give the content to.
     * @return True if the content was given.
     * If false, there was not enough space in there inventory.
     */
    public boolean give(@NotNull PlayerUser user) {

        // Check if the user has enough inventory space.
        if (!this.hasInventorySpace(user)) return false;

        // Give the player the items.
        this.itemList.forEach(item -> user.getPlayer().getInventory().addItem(item.duplicate()));

        // Execute the commands in terms of the player.
        user.runCommandsAsOp(commandList);

        // Give the player the money.
        user.giveMoney(this.money);

        return true;
    }

    @Override
    public DeliveryContent duplicate() {
        return new DeliveryContent().convert(this.convert());
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        section.set("items", this.getItemListAsConfigurationSection().getMap());
        section.set("commands", this.commandList);
        section.set("money", this.money);
        section.set("lore", this.lore);
        section.set("item", this.item == null ? null : this.item.convert());

        return section;
    }

    @Override
    public @NotNull DeliveryContent convert(@NotNull ConfigurationSection section) {

        // Add the items.
        for (String itemKey : section.getSection("items").getKeys()) {
            CozyItem item = new CozyItem()
                    .convert(section.getSection("items").getSection(itemKey));

            // Check if the item is air.
            if (item.getMaterial().equals(Material.AIR)) {
                CozyDeliveries.getPlugin().getLogger()
                        .log(Level.WARNING, "Found material AIR. {"
                                + section.getSection("items").getSection(itemKey).getMap()
                                + "}"
                        );

                continue;
            }

            this.itemList.add(item);
        }

        this.commandList = section.getListString("commands", new ArrayList<>());
        this.money = section.getInteger("money", 0);
        this.lore = section.getListString("lore", new ArrayList<>());
        if (section.getKeys().contains("item")) this.item = new CozyItem().convert(section.getSection("item"));

        return this;
    }

    @Override
    public String toString() {
        return String.join(", ", this.lore);
    }
}
