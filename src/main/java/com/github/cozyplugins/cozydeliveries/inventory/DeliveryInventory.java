package com.github.cozyplugins.cozydeliveries.inventory;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozydeliveries.Delivery;
import com.github.cozyplugins.cozylibrary.inventory.InventoryInterface;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Represents the delivery inventory.
 * Contains all the player's deliveries.
 */
public class DeliveryInventory extends InventoryInterface {

    private final @NotNull UUID deliveryPlayerUuid;
    private final @NotNull ConfigurationSection section;

    /**
     * Used to create a new instance of the
     * delivery inventory in terms of a specific
     * player.
     *
     * @param deliveryPlayerUuid The user to show the delivery's of.
     * @param section The configuration section that represents the inventory.
     */
    public DeliveryInventory(@NotNull UUID deliveryPlayerUuid, @NotNull ConfigurationSection section) {
        super(54, section.getString("title", "&8&lDeliveries"));

        this.deliveryPlayerUuid = deliveryPlayerUuid;
        this.section = section;
    }

    @Override
    protected void onGenerate(PlayerUser player) {
        this.resetInventory();

        // Get the player's deliveries.
        List<Delivery> deliveryList = CozyDeliveries.getAPI()
                .orElseThrow().getDeliveryList(this.deliveryPlayerUuid);

        // Loop though all the deliveries.
        Iterator<Integer> slotIterator = section.getListInteger("slots").iterator();
        for (Delivery delivery : deliveryList) {

            // Check if there are any more slots to assign.
            if (!slotIterator.hasNext()) return;
            this.setItem(new InventoryItem()
                    .setMaterial(Material.valueOf(section.getString("material", "BARREL").toUpperCase()))
                    .setCustomModelData(section.getInteger("custom_model_data", 0))
                    .setName(section.getString("name", "&6&lDelivery"))
                    .setLore(section.getAdaptedString("lore", "\n", "&7Click to collect")
                            .replace("{content}", delivery.getContenceString().replace(", ", "\n&f"))
                            .replace("{from}", delivery.getFromName("None"))
                            .split("\n")
                    )
                    .addSlot(slotIterator.next())
                    .addAction((ClickAction) (user, type, inventory) -> {

                        // Check if they have inventory space.
                        if (!delivery.hasInventorySpace(user)) {
                            user.sendMessage(section.getAdaptedString(
                                    "inventory_space", "\n", "&7You dont have enough inventory space to collect this delivery."
                            ));
                            return;
                        }

                        // Give the delivery to the player.
                        boolean success = delivery.give(user);
                        if (success) {
                            user.sendMessage(section.getAdaptedString(
                                    "success", "\n", "&7You have received a delivery."
                            ));
                            this.onGenerate(user);
                            return;
                        }

                        user.sendMessage(section.getAdaptedString("failed", "\n", "&7Failed to receive a delivery."));
                    })
            );
        }
    }
}
