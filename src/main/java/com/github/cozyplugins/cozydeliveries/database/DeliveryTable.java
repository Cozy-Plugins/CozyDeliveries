package com.github.cozyplugins.cozydeliveries.database;

import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the delivery table in the database.
 * Contains records of deliveries that have not been opened.
 */
public class DeliveryTable extends TableAdapter<DeliveryRecord> {

    @Override
    public @NotNull String getName() {
        return "delivery";
    }
}
