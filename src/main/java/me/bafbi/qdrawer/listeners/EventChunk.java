package me.bafbi.qdrawer.listeners;

import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.runnables.Autosell;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.security.spec.NamedParameterSpec;
import java.util.Objects;

public class EventChunk implements Listener {

    private Qdrawer main;

    public EventChunk(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @EventHandler
    public void OnLoad(ChunkLoadEvent event) {

        if (event.isNewChunk()) return;

        PersistentDataContainer chunkData = event.getChunk().getPersistentDataContainer();

        if (!chunkData.has(new NamespacedKey(main, "autosell"), new BlockArrayDataType())) {
            return;
        }

        for (Block block : Objects.requireNonNull(chunkData.get(new NamespacedKey(main, "autosell"), new BlockArrayDataType()))) {

            if (Autosell.loadDrawer.contains(block)) {
                continue;
            }
            main.getLogger().info("drawer block added");
            Autosell.loadDrawer.add(block);

        }

    }

    @EventHandler
    public void OnUnLoad(ChunkUnloadEvent event) {

        PersistentDataContainer chunkData = event.getChunk().getPersistentDataContainer();

        if (!chunkData.has(new NamespacedKey(main, "autosell"), new BlockArrayDataType())) {
            return;
        }

        Block[] blocks = chunkData.get(new NamespacedKey(main, "autosell"), new BlockArrayDataType());

        assert blocks != null;
        for (Block block : blocks) {

            if (!Autosell.loadDrawer.contains(block)) {
                continue;
            }

            main.getLogger().info("drawer block removed");
            Autosell.loadDrawer.remove(block);

        }

    }

}
