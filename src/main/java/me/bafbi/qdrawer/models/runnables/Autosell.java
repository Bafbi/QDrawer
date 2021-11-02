package me.bafbi.qdrawer.models.runnables;

import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.models.Drawer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Autosell extends BukkitRunnable {

    private Qdrawer main;
    public static List<Block> loadDrawer = new ArrayList<>();

    public Autosell(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @Override
    public void run() {

        //Bukkit.broadcast(Component.text("sell"));

        /*for (Block block : loadDrawer) {

            Drawer drawer;
            try {
                drawer = new Drawer(block);
            } catch (NotDrawerException | NoTileStateException e) {
                continue;
            }

            if (drawer.getUpgrades()[2] < 1) continue;

            Integer qtyToRemove = drawer.getUpgrades()[2] * 4 * 64;

            if (drawer.getQuantity() < qtyToRemove) {
                qtyToRemove = drawer.getQuantity();
            }

            Bukkit.dispatchCommand(main.getServer().getConsoleSender(), main.getConfig().getString("autosell.command", "sell <MATERIAL> <quantity> <player>").replace("<MATERIAL>", drawer.getItem().getType().name()).replace("<quantity>", qtyToRemove.toString()).replace("<player>", Objects.requireNonNull(Bukkit.getOfflinePlayer(UUID.fromString(drawer.getPlayerUUID())).getName())));
            drawer.takeItem(qtyToRemove);
            drawer.updateFrame();

        }*/

    }
}
