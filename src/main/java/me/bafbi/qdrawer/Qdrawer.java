package me.bafbi.qdrawer;

import me.bafbi.qdrawer.commands.CmdQD;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.listeners.*;
import me.bafbi.qdrawer.models.recipes.RecipeDrawer;
import me.bafbi.qdrawer.models.runnables.Autosell;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Qdrawer extends JavaPlugin {

    private static Configuration config;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        config = this.getConfig();

        initAutosell();

        Objects.requireNonNull(getCommand("qd")).setExecutor(new CmdQD(this));
        //getCommand("store").setExecutor(new CmdStore(this));
        //getCommand("upgrade").setExecutor(new CmdUpgrade(this));

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new EventBlockBreakPlace(this), this);
        pluginManager.registerEvents(new EventInteract(this), this);
        pluginManager.registerEvents(new EventInventory(this), this);
        pluginManager.registerEvents(new EventCollector(this), this);
        pluginManager.registerEvents(new EventChunk(this), this);

        if (new RecipeDrawer(this).registerRecipes()) getLogger().info("Crafts Registered");

    }

    @Override
    public void onDisable() {

    }

    public static Configuration getConfigStatic() {
        return config;
    }

    private void initAutosell() {
        this.getServer().getWorlds().forEach(world -> {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                PersistentDataContainer chunkData = loadedChunk.getPersistentDataContainer();

                if (!chunkData.has(new NamespacedKey(this, "autosell"), new BlockArrayDataType())) {
                    continue;
                }

                for (Block block : Objects.requireNonNull(chunkData.get(new NamespacedKey(this, "autosell"), new BlockArrayDataType()))) {

                    if (block == null) continue;
                    if (Autosell.loadDrawer.contains(block)) {
                        continue;
                    }
                    this.getLogger().info("drawer block added");
                    Autosell.loadDrawer.add(block);

                }

            }
        });

        new Autosell(this).runTaskTimer(this, 20, config.getInt("autosell.time_between_sell", 200));

    }
}
