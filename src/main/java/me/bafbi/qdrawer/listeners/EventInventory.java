package me.bafbi.qdrawer.listeners;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Exeptions.NotUpgradeException;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.runnables.Autosell;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.utils.ChunkManager;

public class EventInventory implements Listener {

    private final Qdrawer main;

    public EventInventory(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        /*if (!event.getView().title().equals(Component.text("Drawer Upgrade").color(NamedTextColor.GOLD))) {
            return;
        }*/
        if (event.getInventory().getItem(3) == null) return;
        if (!event.getInventory().getItem(3).getItemMeta().getPersistentDataContainer().has(new NamespacedKey(main, "drawer_id"), PersistentDataType.STRING)) {
            return;
        }

        if (event.getCurrentItem() == null) return;
        if (event.getSlot() == 3 && event.getClickedInventory().equals(event.getView().getTopInventory())) {
            event.getWhoClicked().sendMessage("aa");
            event.setCancelled(true);
            return;
        }

        Drawer drawer;
        try {
            drawer = new Drawer(event.getInventory().getItem(3).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "drawer_id"), PersistentDataType.STRING), event.getWhoClicked().getWorld());
        } catch (NotDrawerException | NoTileStateException e) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        /*if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }*/

        Upgrade clickedUpgrade;
        Upgrade currentUpgrade;
        int currentUpgradeSlot;

        try {
            clickedUpgrade = new Upgrade(item);
            currentUpgradeSlot= clickedUpgrade.getUpgradeType().ordinal();
            currentUpgrade = new Upgrade(event.getInventory().getItem(currentUpgradeSlot));
        } catch (NotUpgradeException | NoTileStateException e) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {

            event.getInventory().setItem(currentUpgradeSlot, clickedUpgrade.getUpgradeItem());
            player.getInventory().setItem(event.getSlot(), clickedUpgrade.getUpgradeItem().asQuantity(item.getAmount() - 1));
            int[] upgrades = drawer.getUpgrades();
            upgrades[currentUpgradeSlot] = clickedUpgrade.getTier();
            drawer.setUpgrades(upgrades);

            switch (currentUpgradeSlot) {
                case 1 -> {
                    switch (clickedUpgrade.getTier()) {
                        case 1 -> {
                            //ChunkManager.removeDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "collection"), false);
                            ChunkManager.addDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "collection"), false);
                        }
                        case 2 -> {
                            //ChunkManager.removeDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "collection"), true);
                            ChunkManager.addDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "collection"), true);
                        }
                    }
                }
                case 2 -> {
                    //ChunkManager.removeDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "autosell"), false);
                    ChunkManager.addDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "autosell"), false);
                    if (!Autosell.loadDrawer.contains(drawer.getBlockDrawer())) {
                        main.getLogger().info("drawer block added");
                        Autosell.loadDrawer.add(drawer.getBlockDrawer());
                    }
                }

            }

            if (currentUpgrade.getTier() >= 1) {
                player.getInventory().addItem(currentUpgrade.getUpgradeItem());
            }

            return;
        }

        if (currentUpgrade.getTier() == 0) {
            return;
        }

        int[] upgrades = drawer.getUpgrades();
        upgrades[currentUpgradeSlot] = 0;
        drawer.setUpgrades(upgrades);
        switch (currentUpgradeSlot) {
            case 1 -> {
                switch (currentUpgrade.getTier()) {
                    case 1 -> ChunkManager.removeDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "collection"), false);
                    case 2 -> ChunkManager.removeDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "collection"), true);
                }
            }
            case 2 -> {
                ChunkManager.removeDrawer(drawer.getBlockDrawer(), new NamespacedKey(main, "autosell"), false);
                if (Autosell.loadDrawer.contains(drawer.getBlockDrawer())) {
                    main.getLogger().info("drawer block removed");
                    Autosell.loadDrawer.remove(drawer.getBlockDrawer());
                }
            }

        }

        player.getInventory().addItem(currentUpgrade.getUpgradeItem());
        currentUpgrade.setTier(0);
        event.getInventory().setItem(currentUpgradeSlot, currentUpgrade.getItemStack());


    }

    @EventHandler
    public void OnItemMove(InventoryMoveItemEvent event) {

        if (event.isCancelled()) return;

        Drawer drawer;
        boolean destination = true;

        try {
            drawer = new Drawer(event.getDestination().getLocation().getBlock());
        } catch (NoTileStateException | NotDrawerException e) {
            destination = false;

            try {
                drawer = new Drawer(event.getSource().getLocation().getBlock());
            } catch (NoTileStateException | NotDrawerException ex) {
                return;
            }
        }

        if (destination) {

            if (!drawer.getItem().isSimilar(event.getItem())) {
                event.setCancelled(true);
                return;
            }
            if (!drawer.addItem(event.getItem())) {
                event.setCancelled(true);
                return;
            }

            drawer.updateFrame();
            drawer.updateItemInBarrelInv();
            event.setItem(new ItemStack(Material.AIR));
        }
        else {
            Drawer finalDrawer = drawer;
            AtomicBoolean good = new AtomicBoolean(true);
            Arrays.stream(event.getDestination().getContents()).forEach(itemStack -> {
                if (itemStack != null && !itemStack.isSimilar(finalDrawer.getItem())) {
                    event.setCancelled(true);
                    good.set(false);
                }
            });
            if (!good.get()) return;
            drawer.takeItem(event.getItem().getAmount());
            drawer.updateFrame();
            drawer.updateItemInBarrelInv();
        }


    }

}
