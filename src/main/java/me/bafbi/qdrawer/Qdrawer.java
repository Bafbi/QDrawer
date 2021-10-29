package me.bafbi.qdrawer;

import me.bafbi.qdrawer.commands.CmdQD;
import me.bafbi.qdrawer.commands.CmdStore;
import me.bafbi.qdrawer.commands.CmdUpgrade;
import me.bafbi.qdrawer.listeners.EventBlockBreakPlace;
import me.bafbi.qdrawer.listeners.EventCollector;
import me.bafbi.qdrawer.listeners.EventInteract;
import me.bafbi.qdrawer.listeners.EventInventory;
import me.bafbi.qdrawer.models.recipes.RecipeDrawer;
import me.bafbi.qdrawer.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Qdrawer extends JavaPlugin {

    private static Configuration config;
    private static DataManager data;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        data = new DataManager(this);
        config = this.getConfig();

        /*for (String chunkKeyString : data.getConfig().getConfigurationSection("chunk_blocks_map").getKeys(false)) {
            Long chunkKey = Long.getLong(chunkKeyString);
            Chunk chunk = Chunk.

        }*/

        /*for (Player player : Bukkit.getOnlinePlayers()) {
            Chunk chunk = player.getChunk();
            PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

            chunkData.remove(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"));
        }*/

        getCommand("qd").setExecutor(new CmdQD(this));
        //getCommand("store").setExecutor(new CmdStore(this));
        //getCommand("upgrade").setExecutor(new CmdUpgrade(this));

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new EventBlockBreakPlace(this), this);
        pluginManager.registerEvents(new EventInteract(this), this);
        pluginManager.registerEvents(new EventInventory(this), this);
        pluginManager.registerEvents(new EventCollector(this), this);

        new RecipeDrawer(this).registerRecipes();
        new RecipeDrawer(this).registerRecipe();

    }

    @Override
    public void onDisable() {

        /*for (Chunk chunk : EventCollector.chunkBlockMap.keySet()) {

            data.getConfig().set("chunk_blocks_map." + chunk.getChunkKey(), EventCollector.chunkBlockMap.get(chunk));

        }

        data.saveConfig();*/

    }

    public static Configuration getConfigStatic() {
        return config;
    }

    public static DataManager getDataStatic() {
        return data;
    }
}
