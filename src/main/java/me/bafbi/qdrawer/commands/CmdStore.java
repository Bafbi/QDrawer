package me.bafbi.qdrawer.commands;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.ItemStackDataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CmdStore implements TabExecutor {

    private Qdrawer main;

    public CmdStore(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();
        NamespacedKey itemKey = new NamespacedKey(main, "item");

        if (item.getType().equals(Material.AIR)) {
            player.sendMessage("aa");
            ItemStack storedItem = player.getPersistentDataContainer().get(itemKey, new ItemStackDataType());
            player.sendMessage(storedItem.displayName());
            player.getInventory().setItemInMainHand(storedItem);
            player.getPersistentDataContainer().remove(itemKey);
            return true;
        }

        player.getPersistentDataContainer().set(itemKey, new ItemStackDataType(), item);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
