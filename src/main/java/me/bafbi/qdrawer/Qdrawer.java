package me.bafbi.qdrawer;

import com.google.common.cache.LoadingCache;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.commands.CmdQD;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.listeners.*;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.recipes.RecipeDrawer;
import me.bafbi.qdrawer.models.runnables.Autosell;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public final class Qdrawer extends JavaPlugin {

    private static Configuration config;
    private static Economy econ = null;
    //public List<Location> drawerLoc = new ArrayList<>();
    //private static DataManager data;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        //data = new DataManager(this);
        config = this.getConfig();

        initAutosell();

        /*if (config.getBoolean("autosell.enable") && !setupEconomy()) {
            getLogger().log(Level.SEVERE, "You want to use Vault plugin for the autosell upgrade but the plugin is not present");
        }*/

        //setupEconomy();


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
        pluginManager.registerEvents(new EventChunk(this), this);

        if (new RecipeDrawer(this).registerRecipes()) getLogger().info("Crafts Registered");

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

    /*public static DataManager getDataStatic() {
        return data;
    }*/

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public Economy getEcon() {
        return econ;
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
