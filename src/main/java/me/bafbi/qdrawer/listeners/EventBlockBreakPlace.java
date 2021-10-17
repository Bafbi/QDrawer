package me.bafbi.qdrawer.listeners;

import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.ItemStackDataType;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class EventBlockBreakPlace implements Listener {

    private final Qdrawer main;

    public EventBlockBreakPlace(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        Drawer drawer;

        try {
            drawer = new Drawer(block);
        } catch (NoTileStateException | NotDrawerException e) {
            return;
        }

        ItemFrame frame = (ItemFrame) block.getWorld().getEntity(drawer.getFrameUUID());
        assert frame != null;
        frame.remove();

        event.setDropItems(false);
        ItemStack itemDrawer = drawer.getDrawerItemStack();

        block.getWorld().dropItem(block.getLocation(), itemDrawer);

        int[] drawerUpgrades = drawer.getUpgrades();
        for (UpgradeType upgradeType: UpgradeType.values()) {
            Upgrade upgrade = new Upgrade(upgradeType, drawerUpgrades[upgradeType.ordinal()]);
            if (upgrade.getTier() > 0) {
                block.getWorld().dropItem(block.getLocation(), upgrade.getItemStack());
            }
        }

        EventCollector.removeDrawer(block);

        player.sendMessage("you break a Drawer");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        PersistentDataContainer dadaContainer = event.getItemInHand().getItemMeta().getPersistentDataContainer();
        NamespacedKey typeKey = new NamespacedKey(main, "type");

        if (!dadaContainer.has(typeKey, PersistentDataType.STRING)) {
            return;
        }
        if (Objects.equals(dadaContainer.get(typeKey, PersistentDataType.STRING), "upgrade")) {
            event.setCancelled(true);
            return;
        }
        if (!Objects.equals(dadaContainer.get(typeKey, PersistentDataType.STRING), "drawer")) {
            return;
        }

        Block blockDrawer = event.getBlockPlaced();

        TileState tileStateDrawer = (TileState) blockDrawer.getState();
        PersistentDataContainer dataDrawer = tileStateDrawer.getPersistentDataContainer();

        dataDrawer.set(typeKey, PersistentDataType.STRING, "drawer");
        tileStateDrawer.update();

        Drawer drawer;
        try {
            drawer = new Drawer(blockDrawer);
        } catch (NotDrawerException | NoTileStateException e) {
            return;
        }

        NamespacedKey itemKey = new NamespacedKey(main, "item");
        if (dadaContainer.has(itemKey, new ItemStackDataType())) {
            drawer.setItem(dadaContainer.get(itemKey, new ItemStackDataType()));
            drawer.setQuantity(dadaContainer.get(new NamespacedKey(main, "quantity"), PersistentDataType.INTEGER));
        }

        drawer.createFrame();
        EventCollector.addDrawer(blockDrawer);

        player.sendMessage(Component.text("You place a new Drawer"));
    }
}