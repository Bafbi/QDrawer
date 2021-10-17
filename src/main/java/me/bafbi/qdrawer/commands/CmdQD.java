package me.bafbi.qdrawer.commands;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.models.Drawer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CmdQD implements TabExecutor {

    private Qdrawer main;

    public CmdQD(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        ItemStack itemDrawer = Drawer.getNewDrawerItemStack();

        player.getInventory().addItem(itemDrawer);
        player.updateInventory();

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
