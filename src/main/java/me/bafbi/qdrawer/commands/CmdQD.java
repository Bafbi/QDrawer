package me.bafbi.qdrawer.commands;

import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.BlockArrayDataType;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CmdQD implements TabExecutor {

    private Qdrawer main;

    public CmdQD(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return false;
        if (args.length < 1) return false;

        Player player = (Player) sender;

        switch (args[0]) {
            case "give":
                {
                    ItemStack item = Drawer.getNewDrawerItemStack();
                    if (args.length > 1) {
                        switch (args[1]) {
                            case "drawer" :
                                break;
                            case "upgrade" :
                                {
                                    if (args.length < 4) break;
                                    item = new Upgrade(UpgradeType.valueOf(args[2].toUpperCase()), Integer.valueOf(args[3])).getItemStack();
                                }
                                break;
                        }
                    }
                    player.getInventory().addItem(item);
                    player.updateInventory();
                }
                break;
            case "chunk" :
                {
                    if (args.length < 2) break;
                    switch (args[1]) {
                        case "info" :
                            {
                                player.sendMessage(chunkInfo(player.getChunk()));
                            }
                            break;
                        case "reset" :
                            {
                                player.getChunk().getPersistentDataContainer().remove(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"));
                            }
                    }
                }
                break;
        }



        return false;
    }

    private Component chunkInfo(Chunk chunk) {

        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
        Component component = Component.text("");

        if (!chunkData.has(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType())) {
            return Component.text("no drawer");
        }

        for (Block drawerBlock : Objects.requireNonNull(chunkData.get(new NamespacedKey(Qdrawer.getPlugin(Qdrawer.class), "drawers"), new BlockArrayDataType()))) {
            Drawer drawer;
            try {
                drawer = new Drawer(drawerBlock);
            } catch (NotDrawerException | NoTileStateException e) {
                continue;
            }
            component = component.append(Component.text("Drawer : "))
                    .append(drawer.getDisplayItemStack().displayName())
                    .append(Component.newline())
                    .append(Component.text("in X=" + drawerBlock.getX() + ", Y=" + drawerBlock.getY() + ", Z=" + drawerBlock.getZ()))
                    .append(Component.newline());


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
                break;
            case 2:
                switch (args[0]) {
                    case "give":
                        things.add("drawer");
                        things.add("upgrade");
                        break;
                    case "chunk":
                        things.add("info");
                        things.add("reset");
                        break;
                }
                break;
            case 3:
                switch (args[1]) {
                    case "upgrade":
                        things.add("storage");
                        things.add("collection");
                        things.add("autosell");
                        break;
                }
                break;
        }

        return things;
    }
}
