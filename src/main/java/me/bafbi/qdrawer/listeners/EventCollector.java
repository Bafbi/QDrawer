package me.bafbi.qdrawer.listeners;

import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.models.Drawer;

public class EventCollector implements Listener {

    private final Qdrawer main;
    //public static Map<Chunk, Block[]> chunkBlockMap = new HashMap<>();

    public EventCollector(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    private boolean putInDrawer(Chunk chunk, Item item) {

        //Bukkit.broadcast(Component.text("drop"));

        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

        if (!chunkData.has(new NamespacedKey(main, "collection"), new BlockArrayDataType())) {
            return false;
        }

        for (Block drawerBlock : Objects.requireNonNull(chunkData.get(new NamespacedKey(main, "collection"), new BlockArrayDataType()))) {
            Drawer drawer;
            try {
                drawer = new Drawer(drawerBlock);
            } catch (NotDrawerException | NoTileStateException e) {
                continue;
            }

            if (drawer.getItem().isSimilar(item.getItemStack())) {
                //Bukkit.broadcast(Component.text("oui"));
                if (!drawer.addItem(item.getItemStack())) continue;
                drawer.updateFrame();
                drawer.updateItemInBarrelInv();
                return true;
            }
        }
        return false;
    }


    /*@EventHandler
    public void OnBlockDrop(BlockDropItemEvent event) {

        putInDrawer(event.getBlock().getChunk(), event.getItems());

    }

    @EventHandler
    public void OnBlockBlockDrop(BlockBreakBlockEvent event) {

        Bukkit.broadcast(Component.text("ahouai"));
        //putInDrawer(event.getBlock().getChunk(), event.getDrops());

    }

    @EventHandler
    public void OnEntityDrop(EntityDeathEvent event) {

        putInDrawerBis(event.getEntity().getChunk(), event.getDrops());

    }

    @EventHandler
    public void OnPlayerDrop(PlayerDropItemEvent event) {

        if (putInDrawer(event.getItemDrop().getChunk(), event.getItemDrop())) {
            event.getItemDrop().remove();
        }

    }*/

    @EventHandler
    public void OnItemSpawn(ItemSpawnEvent event) {

        if (putInDrawer(event.getLocation().getChunk(), event.getEntity())) {
            event.getEntity().remove();
        }

    }

}
