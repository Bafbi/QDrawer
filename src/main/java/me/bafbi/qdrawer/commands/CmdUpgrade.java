package me.bafbi.qdrawer.commands;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CmdUpgrade implements TabExecutor {

    private Qdrawer main;

    public CmdUpgrade(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return true;
        if (args.length < 2) return false;

        Player player = (Player) sender;

        ItemStack upgradeItem = new Upgrade(UpgradeType.valueOf(args[0].toUpperCase()), Integer.valueOf(args[1])).getItemStack();

        player.getInventory().addItem(upgradeItem);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
