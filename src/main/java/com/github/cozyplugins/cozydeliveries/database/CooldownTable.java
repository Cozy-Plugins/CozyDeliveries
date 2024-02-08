package com.github.cozyplugins.cozydeliveries.database;

import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the cooldown table.
 * Contains records for events with the cooldown type.
 */
public class CooldownTable extends TableAdapter<CooldownRecord> {

    @Override
    public @NotNull String getName() {
        return "cooldown";
    }
}
