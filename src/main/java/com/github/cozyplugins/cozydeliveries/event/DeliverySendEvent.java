/*
 * CozyDeliveries - An item and money delivery service for a minecraft server.
 * Copyright (C) 2024  Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.cozyplugins.cozydeliveries.event;

import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the delivery send event.
 * Called when a delivery is about to be sent.
 */
public class DeliverySendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled;
    private @NotNull Delivery delivery;

    /**
     * Used to create a new delivery send event.
     *
     * @param delivery The instance of the delivery.
     */
    public DeliverySendEvent(@NotNull Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    /**
     * Used to get the instance of the
     * current delivery.
     *
     * @return The instance of teh delivery.
     */
    public @NotNull Delivery getDelivery() {
        return this.delivery;
    }

    /**
     * Used to set the delivery to a different delivery.
     *
     * @param delivery The other delivery to set to.
     * @return This instance.
     */
    public @NotNull DeliverySendEvent setDelivery(@NotNull Delivery delivery) {
        this.delivery = delivery;
        return this;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
