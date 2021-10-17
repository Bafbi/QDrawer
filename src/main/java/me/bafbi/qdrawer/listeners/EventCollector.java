package me.bafbi.qdrawer.listeners;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.models.Drawer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class EventCollector implements Listener {

    private final Qdrawer main;
    //public static Map<Chunk, Block[]> chunkBlockMap = new HashMap<>();

    public EventCollector(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    public static void addDrawer(Block drawerBlock) {

        Chunk chunk = drawerBlock.getChunk();
        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();


        /*if (chunkBlockMap == null || !chunkBlockMap.containsKey(chunk)) {
            Block[] blockArray = {drawerBlock};
            chunkBlockMap.put(chunk, blockArray);
            return;
        }*/

        if (!chunkData.has(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType())) {
            Block[] blockArray = {drawerBlock};
            chunkData.set(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType(), blockArray);
            return;
        }

        List<Block> drawersList = new LinkedList<Block>(Arrays.asList(Objects.requireNonNull(chunkData.get(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType()))));
        drawersList.add(drawerBlock);

        Block[] blocks = {};
        blocks = (Block[]) drawersList.toArray(blocks);

        /*Qdrawer.getDataStatic().getConfig().set("chunk_blocks_map.", blocks);
        Qdrawer.getDataStatic().saveConfig();*/

        //chunkBlockMap.put(chunk, blocks);

        chunkData.set(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType(), blocks);

    }

    public static void removeDrawer(Block drawerBlock) {

        Chunk chunk = drawerBlock.getChunk();
        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();


        /*if (chunkBlockMap == null || !chunkBlockMap.containsKey(chunk)) {
            return;
        }*/

        if (!chunkData.has(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType())) {
            return;
        }

        List<Block> drawersList = new LinkedList<Block>(Arrays.asList(Objects.requireNonNull(chunkData.get(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType()))));
        drawersList.remove(drawerBlock);

        /*int count = 0;
        drawersList.forEach(e -> {
            if (e.equals(drawerBlock)) {
                drawersList.remove(count);
            }
        });*/
        /*for (Block block : chunkBlockMap.get(chunk)) {
            if (block.equals(drawerBlock)) continue;
            drawersList.add(block);
        }*/
        Block[] blocks = {};
        blocks = (Block[]) drawersList.toArray(blocks);

        /*Qdrawer.getDataStatic().getConfig().set("chunk_blocks_map.", blocks);
        Qdrawer.getDataStatic().saveConfig();*/

        //chunkBlockMap.put(chunk, blocks);

        chunkData.set(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType(), blocks);

    }

    private void putInDrawer(Chunk chunk, List<Item> items) {

        Bukkit.broadcast(Component.text("drop"));

        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

        if (!chunkData.has(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType())) {
            return;
        }

        for (Block drawerBlock : Objects.requireNonNull(chunkData.get(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType()))) {
            Drawer drawer;
            try {
                drawer = new Drawer(drawerBlock);
            } catch (NotDrawerException | NoTileStateException e) {
                return;
            }

            for (Item item : List.copyOf(items)) {
                if (drawer.getItem().isSimilar(item.getItemStack())) {
                    Bukkit.broadcast(Component.text("oui"));
                    drawer.addItem(item.getItemStack());
                    drawer.updateFrame();
                    drawer.putItemInBarrelInv();
                    items.remove(item);
                }
            }
        }
    }

    private boolean putInDrawer(Chunk chunk, Item item) {

        Bukkit.broadcast(Component.text("drop"));

        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

        if (!chunkData.has(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType())) {
            return false;
        }

        for (Block drawerBlock : Objects.requireNonNull(chunkData.get(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType()))) {
            Drawer drawer;
            try {
                drawer = new Drawer(drawerBlock);
            } catch (NotDrawerException | NoTileStateException e) {
                return false;
            }

            if (drawer.getItem().isSimilar(item.getItemStack())) {
                Bukkit.broadcast(Component.text("oui"));
                drawer.addItem(item.getItemStack());
                drawer.updateFrame();
                drawer.putItemInBarrelInv();
                return true;
            }
        }
        return false;
    }


    @EventHandler
    public void OnBlockDrop(BlockDropItemEvent event) {

        putInDrawer(event.getBlock().getChunk(), event.getItems());

    }

    /*@EventHandler
    public void OnBlockBlockDrop(BlockBreakBlockEvent event) {

        Bukkit.broadcast(Component.text("ahouai"));
        //putInDrawer(event.getBlock().getChunk(), event.getDrops());

    }*/

    @EventHandler
    public void OnEntityDrop(EntityDropItemEvent event) {

        event.setCancelled(putInDrawer(event.getItemDrop().getChunk(), event.getItemDrop()));

    }

    @EventHandler
    public void OnPlayerDrop(PlayerDropItemEvent event) {

        event.setCancelled(putInDrawer(event.getItemDrop().getChunk(), event.getItemDrop()));
        event.getPlayer().getInventory().removeItem(event.getItemDrop().getItemStack());

    }

}
