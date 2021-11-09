package me.bafbi.qdrawer.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.recipes.RecipeDrawer;
import me.bafbi.qdrawer.models.runnables.Autosell;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdQD implements TabExecutor {

    private Qdrawer main;

    public CmdQD(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return false;
        if (args.length < 1) return false;

        switch (args[0]) {
            case "give" -> {
                ItemStack item = Drawer.getNewDrawerItemStack();
                if (args.length > 1) {
                    switch (args[1]) {
                        case "drawer":
                            break;
                        case "upgrade": {
                            if (args.length < 4) break;
                            item = new Upgrade(UpgradeType.valueOf(args[2].toUpperCase()), Integer.valueOf(args[3])).getItemStack();
                        }
                        break;
                    }
                }
                player.getInventory().addItem(item);
                player.updateInventory();
            }
            case "chunk" -> {
                if (args.length < 2) break;
                switch (args[1]) {
                    case "info" -> {
                        player.sendMessage(chunkInfo(player.getChunk()));
                    }
                    case "reset" -> {
                        player.getChunk().getPersistentDataContainer().remove(new NamespacedKey(main, "collection"));
                        player.getChunk().getPersistentDataContainer().remove(new NamespacedKey(main, "autosell"));
                    }
                }
            }
            case "recipe" -> {
                if (args.length < 2) break;
                switch (args[1]) {
                    case "getall" -> {
                        for (NamespacedKey key : RecipeDrawer.recipesList) {
                            player.discoverRecipe(key);
                        }
                    }
                    case "removeall" -> {
                        for (NamespacedKey key : RecipeDrawer.recipesList) {
                            player.undiscoverRecipe(key);
                        }
                    }
                    case "show" -> {
                        for (NamespacedKey key : RecipeDrawer.recipesList) {
                            Component component = Component.text(key.value()).color(player.hasDiscoveredRecipe(key) ? NamedTextColor.GREEN : NamedTextColor.RED);
                            player.sendMessage(component);
                        }
                    }
                }
            }
            case "loadedDrawer" -> {
                if (Autosell.loadDrawer.isEmpty()) {
                    player.sendMessage(Component.text("No drawer with autosell loaded"));
                    return true;
                }
                for (Block block : Autosell.loadDrawer) {
                    Drawer drawer;
                    try {
                        drawer = new Drawer(block);
                    } catch (NotDrawerException | NoTileStateException e) {
                        continue;
                    }
                    player.sendMessage(Component.text(drawer.getFrameUUID() + " | ").append(drawer.getDisplayItemStack().displayName()));
                }
            }
        }



        return false;
    }

    private Component chunkInfo(Chunk chunk) {

        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
        Component component = Component.text("");

        if (chunkData.has(new NamespacedKey(main, "collection"), new BlockArrayDataType())) {
            component = component.append(Component.text("Collection:")).append(Component.newline());

            for (Block drawerBlock : Objects.requireNonNull(chunkData.get(new NamespacedKey(main, "collection"), new BlockArrayDataType()))) {
                Drawer drawer;
                try {
                    drawer = new Drawer(drawerBlock);
                } catch (NotDrawerException | NoTileStateException e) {
                    //return Component.text("no drawer");
                    continue;
                }
                component = component.append(Component.text("Drawer : "))
                        .append(drawer.getDisplayItemStack().displayName())
                        .append(Component.newline())
                        .append(Component.text("in X=" + drawerBlock.getX() + ", Y=" + drawerBlock.getY() + ", Z=" + drawerBlock.getZ()))
                        .append(Component.newline());


            }
        }
        else {
            component = component.append(Component.text("no collection").append(Component.newline()));
        }

        if (chunkData.has(new NamespacedKey(main, "autosell"), new BlockArrayDataType())) {
            component = component.append(Component.text("Autosell:")).append(Component.newline());

            for (Block drawerBlock : Objects.requireNonNull(chunkData.get(new NamespacedKey(main, "autosell"), new BlockArrayDataType()))) {
                Drawer drawer;
                try {
                    drawer = new Drawer(drawerBlock);
                } catch (NotDrawerException | NoTileStateException e) {
                    //return Component.text("no drawer");
                    continue;
                }
                component = component.append(Component.text("Drawer : "))
                        .append(drawer.getDisplayItemStack().displayName())
                        .append(Component.newline())
                        .append(Component.text("in X=" + drawerBlock.getX() + ", Y=" + drawerBlock.getY() + ", Z=" + drawerBlock.getZ()))
                        .append(Component.newline());


            }
        }
        else {
            component = component.append(Component.text("no autosell"));
        }



        return component;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> things = new ArrayList<>();

        switch (args.length) {
            case 1:
                things.add("give");
                things.add("chunk");
                things.add("recipe");
                things.add("loadedDrawer");
                break;
            case 2:
                switch (args[0]) {
                    case "give" -> {
                        things.add("drawer");
                        things.add("upgrade");
                    }
                    case "chunk" -> {
                        things.add("info");
                        things.add("reset");
                    }
                    case "recipe" -> {
                        things.add("getall");
                        things.add("removeall");
                        things.add("show");
                    }
                }
                break;
            case 3:
                switch (args[1]) {
                    case "upgrade" -> {
                        things.add("storage");
                        things.add("collection");
                        things.add("autosell");
                    }
                }
                break;
        }

        return things;
    }
}
