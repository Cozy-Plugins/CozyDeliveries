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

package com.github.cozyplugins.cozydeliveries.delivery.event.handler;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozydeliveries.delivery.Delivery;
import com.github.cozyplugins.cozydeliveries.delivery.DeliveryContent;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEvent;
import com.github.cozyplugins.cozydeliveries.delivery.event.DeliveryEventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

/**
 * Represents the standard delivery event handler.
 */
public class StandardDeliveryEventHandler implements DeliveryEventHandler {

    @Override
    public void onEvent(@NotNull DeliveryEvent event, @NotNull UUID playerUuid) {

        // Get the deliveries to choose from.
        List<String> pickingList = event.getConfigurationSection().getListString("deliveries", new ArrayList<>());

        // Check if the list is empty.
        if (pickingList.isEmpty()) return;

        // Create the list of deliveries.
        List<Delivery> deliveryList = new ArrayList<>();

        // Add the deliveries.
        for (int i = 0; i < event.getConfigurationSection().getInteger("amount", 1); i++) {
            DeliveryContent content = this.pickDeliveryContent(pickingList, playerUuid);
            if (content == null) continue;

            // Create the delivery.
            Delivery delivery = new Delivery(playerUuid, System.currentTimeMillis());
            delivery.setFromName("Server");
            delivery.setDeliveryContent(content);

            // Check if the delivery should be expired.
            if (event.getConfigurationSection().getKeys().contains("remove_after_seconds")) {
                delivery.setTimeStampExpire(
                        System.currentTimeMillis() + (event.getConfigurationSection().getInteger("remove_after_seconds", -1) * 1000L)
                );
            }

            // Add the delivery to the list.
            deliveryList.add(delivery);
        }

        // Send the deliveries.
        deliveryList.forEach(
                delivery -> CozyDeliveries.getAPI().orElseThrow().sendDelivery(delivery)
        );
    }

    public @Nullable DeliveryContent pickDeliveryContent(@NotNull List<String> deliveryIdentnfierList, @NotNull UUID playerUuid) {
        Map<String, Double> map = this.createMap(deliveryIdentnfierList);

        // If the min is 0.02 -> 200 places = 3 -> 1 / (10^(3-1)) = 0.
        // IF the min is 0.023 -> 2300 places = 4
        final double minValue = this.getMin(map);
        final double factor = 1D / minValue;
        final int places = String.valueOf(factor).length();

        // places = 3 -> 1 / (10^(3-1)) = 0.01 -> 100
        final double increment = 1D / (Math.pow(10D, places - 1));
        final double factorIncrement = 1D / increment;

        // [0.5, 0.5, 0.3] -> (1.3 * 100) == 130
        final double outOf = this.getSum(map.values()) * factorIncrement;

        // Pick random number.
        final int random = new Random().nextInt((int) outOf + 1);

        // Get the place in the map.
        // rand(0-130) = 34 -> 34 / 100 = 0.34
        final double randomPlace = ((double) random) / factorIncrement;

        String identifier = this.getIdentifierForPlace(randomPlace, map);

        // Check if the identifier randomly chosen is null.
        if (identifier == null) {
            CozyDeliveries.getPlugin().getLogger().log(
                    Level.WARNING,
                    "Something went wrong when calculating the random delivery.\n"
                            + "places:" + places + " factorIncrement:" + factorIncrement + " outOf:" + outOf + " randomPlace:" + randomPlace
            );
            identifier = map.keySet().stream().toList().get(0);
        }

        // Get the instance of the delivery content.
        DeliveryContent content = CozyDeliveries.getAPI().orElseThrow().getContentConfiguration().getType(identifier).orElse(null);

        // Check if the content is null.
        if (content == null) {
            CozyDeliveries.getPlugin().getLogger().log(
                    Level.WARNING,
                    "Could not find delivery content in the configuration directory..\n"
                            + "contentIdentifier:" + identifier
            );
            return null;
        }

        return content;
    }

    public @NotNull Map<String, Double> createMap(@NotNull List<String> deliveryIdentnfierList) {
        Map<String, Double> map = new LinkedHashMap<>();

        for (String entry : deliveryIdentnfierList) {
            map.put(entry.split(" ")[0], Double.parseDouble(entry.split(" ")[1]));
        }

        return map;
    }

    public double getMin(@NotNull Map<String, Double> deliveryIdentnfierMap) {
        double min = deliveryIdentnfierMap.values().stream().toList().get(0);
        for (double number : deliveryIdentnfierMap.values()) {
            min = Math.min(min, number);
        }
        return min;
    }

    private double getSum(@NotNull Collection<Double> values) {
        double sum = 0D;
        for (double number : values) {
            sum += number;
        }
        return sum;
    }

    public @Nullable String getIdentifierForPlace(double randomPlace, @NotNull Map<String, Double> map) {
        double currentPlace = 0D;

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            final double value = entry.getValue();
            if (randomPlace < (value + currentPlace)) return entry.getKey();
            currentPlace += value;
        }

        return null;
    }
}
