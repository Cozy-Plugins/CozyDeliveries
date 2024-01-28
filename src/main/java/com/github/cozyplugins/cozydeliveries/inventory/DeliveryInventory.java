package com.github.cozyplugins.cozydeliveries.inventory;

import com.github.cozyplugins.cozylibrary.inventory.InventoryInterface;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the delivery inventory.
 * Contains all the player's deliveries.
 */
public class DeliveryInventory extends InventoryInterface {

    private final @NotNull PlayerUser deliveryUser;

    /**
     * Used to create a new instance of the
     * delivery inventory in terms of a specific
     * player.
     *
     * @param deliveryUser The user to show the delivery's of.
     */
    public DeliveryInventory(@NotNull PlayerUser deliveryUser) {
        super(54, "&f");

        this.deliveryUser = deliveryUser;
    }

    @Override
    protected void onGenerate(PlayerUser player) {

    }
}
