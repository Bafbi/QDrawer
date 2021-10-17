package me.bafbi.qdrawer.listeners;

import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Exeptions.NotUpgradeException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EventInventory implements Listener {

    private final Qdrawer main;

    public EventInventory(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!event.getView().title().equals(Component.text("Drawer Upgrade").color(NamedTextColor.GOLD))) {
            return;
        }
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
        Integer currentUpgradeSlot;

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

        player.getInventory().addItem(currentUpgrade.getUpgradeItem());
        currentUpgrade.setTier(0);
        event.getInventory().setItem(currentUpgradeSlot, currentUpgrade.getItemStack());


    }

}
