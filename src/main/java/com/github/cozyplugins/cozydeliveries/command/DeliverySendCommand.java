package com.github.cozyplugins.cozydeliveries.command;

import com.github.cozyplugins.cozydeliveries.CozyDeliveries;
import com.github.cozyplugins.cozylibrary.command.command.CommandType;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandArguments;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandStatus;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandSuggestions;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandTypePool;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ConfirmAction;
import com.github.cozyplugins.cozylibrary.inventory.inventory.ConfirmationInventory;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DeliverySendCommand implements CommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "send";
    }

    @Override
    public @Nullable String getSyntax() {
        return "/[parent] [name]";
    }

    @Override
    public @Nullable String getDescription() {
        return "Used to send a delivery to a player.";
    }

    @Override
    public @Nullable CommandTypePool getSubCommandTypes() {
        return null;
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return new CommandSuggestions()
                .append(List.of("item", "inventory"))
                .append(Stream.of(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayer(@NotNull PlayerUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Check if they have provided the correct number of arguments.
        if (arguments.getArguments().size() < 2) {
            user.sendMessage(section.getString("incorrect_arguments", "&7Incorrect arguments. &e/deliveries send <collection> <player>"));
            return new CommandStatus();
        }

        // Check if the database is disabled.
        if (CozyDeliveries.getAPI().orElseThrow().getDatabase().isDisabled()) {
            user.sendMessage(section.getString("database_disabled", "&7The database is currently disabled. This could be an error."));
            return new CommandStatus();
        }

        if (!Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .toList()
                .contains(arguments.getArguments().get(1))) {

            user.sendMessage(section.getString("incorrect_arguments_player", "&7Incorrect arguments. &e/deliveries send <collection> <player>"));
            return new CommandStatus();
        }

        // Get the player to send the delivery to.
        final OfflinePlayer player = Bukkit.getOfflinePlayer(arguments.getArguments().get(1));

        // Check if they have selected "item" as there first argument.
        if (arguments.getArguments().get(0).equals("item")) {
            final ItemStack itemStack = user.getPlayer().getItemInHand();
            this.checkBeforeDeliver(user, section, player, itemStack);
            return new CommandStatus();
        }

        // Check if they have selected "inventory" as there first argument.
        if (arguments.getArguments().get(0).equals("inventory")) {
            this.checkBeforeDeliver(user, section, player, user.getPlayer().getInventory().getContents());
            return new CommandStatus();
        }

        user.sendMessage(section.getString("incorrect_arguments_collection", "&7Incorrect arguments. &e/deliveries send <collection> <player>"));
        return new CommandStatus();
    }

    private void checkBeforeDeliver(@NotNull PlayerUser fromUser, @NotNull ConfigurationSection section, @NotNull OfflinePlayer toPlayer, @NotNull ItemStack... itemStacks) {
        new ConfirmationInventory(new ConfirmAction()
                .setAnvilTitle("&8Send Delivery")
                .setAbort(user -> {
                    user.sendMessage(section.getAdaptedString("aborted", "\n", "&7Aborted delivery."));
                })
                .setConfirm(user -> {
                    this.deliver(fromUser, section, toPlayer, itemStacks);
                })
        ).open(fromUser.getPlayer());
    }

    private void deliver(@NotNull PlayerUser fromUser, @NotNull ConfigurationSection section, @NotNull OfflinePlayer toPlayer, @NotNull ItemStack... itemStacks) {

        CozyItem[] cozyItemList = (CozyItem[]) Arrays.stream(itemStacks).map(CozyItem::new).toArray();

        // Create the delivery.
        boolean success = CozyDeliveries.getAPI().orElseThrow().sendDelivery(
                toPlayer.getUniqueId(),
                fromUser.getName(),
                cozyItemList
        );

        if (success) {
            fromUser.sendMessage(section.getAdaptedString("sent", "\n", "&7Sent a delivery to &f{player}&7."
                    .replace("{player}", toPlayer.getName() == null ? "null" : toPlayer.getName())));
            Arrays.stream(itemStacks).forEach(item -> item.setAmount(0));
            return;
        }

        fromUser.sendMessage(section.getAdaptedString("cancelled", "\n", "&7Unable to send this delivery to &f{player}&7."
                .replace("{player}", toPlayer.getName() == null ? "null" : toPlayer.getName())));
    }

    @Override
    public @Nullable CommandStatus onFakeUser(@NotNull FakeUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onConsole(@NotNull ConsoleUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }
}
