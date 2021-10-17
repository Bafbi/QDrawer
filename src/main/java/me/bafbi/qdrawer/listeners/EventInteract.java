package me.bafbi.qdrawer.listeners;

import io.papermc.paper.event.player.PlayerItemCooldownEvent;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.models.Drawer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class EventInteract implements Listener {

    private final Qdrawer main;

    public EventInteract(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (!event.hasBlock()) {
            return;
        }

        NamespacedKey typeKey = new NamespacedKey(main, "type");

        if (event.hasItem() && Objects.equals(event.getItem().getItemMeta().getPersistentDataContainer().get(typeKey, PersistentDataType.STRING), "upgrade")) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        Drawer drawer;

        try {
            drawer = new Drawer(Objects.requireNonNull(block));
        } catch (NoTileStateException | NotDrawerException e) {
            return;
        }

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK: //Gauche prendre
                {

                    if (player.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("axe")){
                        return;
                    }

                    if (drawer.getQuantity() <= 0) {
                        player.sendMessage("The drawer is empty");
                        return;
                    }

                    Integer amount = 1;
                    if (player.isSneaking()) {
                        if (drawer.getQuantity() > 64) {
                            amount = 64;
                        } else {
                            amount = drawer.getQuantity();
                        }
                    }

                    ItemStack item = drawer.takeItem(amount);
                    if (item.getType().equals(Material.AIR)) return;
                    player.getInventory().addItem(item);
                    player.updateInventory();
                    player.sendMessage(Component.text("You get " + amount + " ").append(item.displayName()).append(Component.text(" from the drawer | " + drawer.getQuantity())));
                    drawer.updateFrame();



                }
                break;
            case RIGHT_CLICK_BLOCK: //Droit poser
                {
                    event.setCancelled(true);

                    poseItem(drawer, player);

                }
        }


    }

    @EventHandler
    public void onEntityInteractRight(PlayerInteractEntityEvent event) {

        if (!event.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            return;
        }

        ItemFrame frame = (ItemFrame) event.getRightClicked();
        PersistentDataContainer dataFrame = frame.getPersistentDataContainer();
        NamespacedKey typeKey = new NamespacedKey(main, "type");

        if (!dataFrame.has(typeKey, PersistentDataType.STRING)) {
            return;
        }
        if (!dataFrame.get(typeKey, PersistentDataType.STRING).equals("drawerframe")) {
            return;
        }

        Player player = event.getPlayer();
        Block drawerBlock = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());

        Drawer drawer;

        try {
            drawer = new Drawer(drawerBlock);
        } catch (NotDrawerException | NoTileStateException e) {
            return;
        }

        poseItem(drawer, player);
    }


    private void poseItem(Drawer drawer, Player player) {
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem.getType().equals(Material.AIR)) {
            if (player.isSneaking()) {
                player.sendMessage("open gui");
                player.openInventory(drawer.getGui());
                return;
            }
            if (drawer.getQuantity() <= 0) {
                player.sendMessage("The drawer is empty");
                return;
            }
        }

        if (!handItem.asOne().equals(drawer.getItem().asOne()) || handItem.getType().equals(Material.AIR)) {
            if (drawer.getQuantity() > 0) {
                player.sendMessage(Component.text("The drawer contain " + drawer.getQuantity() + " ").append(drawer.getItem().displayName()));
                return;
            }
        }

        PersistentDataContainer handItemData = handItem.getItemMeta().getPersistentDataContainer();

        if (handItemData.has(new NamespacedKey(main, "type"), PersistentDataType.STRING)) {
            if (handItemData.get(new NamespacedKey(main, "type"), PersistentDataType.STRING).equals("drawer")) {
                player.sendMessage("You can't place a drawer inside of a drawer");
                return;
            }
        }

        if (drawer.getQuantity() >= Math.pow(2.0, drawer.getUpgrades()[0] + 5) * 64) {
            return;
        }

        Integer amount = 1;
        if (player.isSneaking()) {
            amount = handItem.getAmount();
        }

        drawer.addItem(handItem.asQuantity(amount));
        player.sendMessage(Component.text("You put " + amount + " ").append(handItem.displayName()).append(Component.text(" from the drawer | " + drawer.getQuantity())));

        handItem.setAmount(handItem.getAmount() - amount);
        player.getInventory().setItemInMainHand(handItem);
        player.updateInventory();

        drawer.updateFrame();
    }
}
