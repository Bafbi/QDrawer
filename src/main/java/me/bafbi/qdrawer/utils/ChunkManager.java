package me.bafbi.qdrawer.utils;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class ChunkManager {

    public static void addDrawer(Block drawerBlock, NamespacedKey key, boolean threeByThree) {

        List<Chunk> chunks = new ArrayList<>();

        if (threeByThree) {
            chunks.add(drawerBlock.getLocation().add(16, 0, 0).getChunk());
            chunks.add(drawerBlock.getLocation().add(16, 0, 16).getChunk());
            chunks.add(drawerBlock.getLocation().add(0, 0, 16).getChunk());
            chunks.add(drawerBlock.getLocation().add(-16, 0, 16).getChunk());
            chunks.add(drawerBlock.getLocation().add(-16, 0, 0).getChunk());
            chunks.add(drawerBlock.getLocation().add(-16, 0, -16).getChunk());
            chunks.add(drawerBlock.getLocation().add(0, 0, -16).getChunk());
            chunks.add(drawerBlock.getLocation().add(16, 0, -16).getChunk());
        }

        chunks.add(drawerBlock.getChunk());

        //Bukkit.getLogger().info(chunks.toString());

        for (Chunk chunk : chunks) {

            //Bukkit.getLogger().info(chunk.toString());
            PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

            if (!chunkData.has(key, new BlockArrayDataType())) {
                Block[] blockArray = {drawerBlock};
                chunkData.set(key, new BlockArrayDataType(), blockArray);
                continue;
            }

            List<Block> drawersList = new LinkedList<Block>(Arrays.asList(Objects.requireNonNull(chunkData.get(key, new BlockArrayDataType()))));
            //Bukkit.getLogger().info(drawersList.toString());
            drawersList.add(drawerBlock);
            //Bukkit.getLogger().info(drawersList.toString());

            Block[] blocks = {};
            blocks = (Block[]) drawersList.toArray(blocks);

            chunkData.set(key, new BlockArrayDataType(), blocks);

        }


    }

    public static void removeDrawer(Block drawerBlock, NamespacedKey key, boolean threeByThree) {

        List<Chunk> chunks = new ArrayList<>();

        if (threeByThree) {
            chunks.add(drawerBlock.getLocation().add(16, 0, 0).getChunk());
            chunks.add(drawerBlock.getLocation().add(16, 0, 16).getChunk());
            chunks.add(drawerBlock.getLocation().add(0, 0, 16).getChunk());
            chunks.add(drawerBlock.getLocation().add(-16, 0, 16).getChunk());
            chunks.add(drawerBlock.getLocation().add(-16, 0, 0).getChunk());
            chunks.add(drawerBlock.getLocation().add(-16, 0, -16).getChunk());
            chunks.add(drawerBlock.getLocation().add(0, 0, -16).getChunk());
            chunks.add(drawerBlock.getLocation().add(16, 0, -16).getChunk());
        }

        chunks.add(drawerBlock.getChunk());
        //Bukkit.getLogger().info(chunks.toString());

        for (Chunk chunk : chunks) {

            //Bukkit.getLogger().info(chunk.toString());
            PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

            if (!chunkData.has(key, new BlockArrayDataType())) {
                continue;
            }

            List<Block> drawersList = new LinkedList<Block>(Arrays.asList(Objects.requireNonNull(chunkData.get(key, new BlockArrayDataType()))));
            //Bukkit.getLogger().info(drawersList.toString());
            drawersList.remove(drawerBlock);
            //Bukkit.getLogger().info(drawersList.toString());

            Block[] blocks = {};
            blocks = (Block[]) drawersList.toArray(blocks);

            if (blocks.length == 0) {
                chunkData.remove(key);
                continue;
            }

            chunkData.set(key, new BlockArrayDataType(), blocks);

        }


    }

}
