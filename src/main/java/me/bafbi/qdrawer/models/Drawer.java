package me.bafbi.qdrawer.models;

import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotDrawerException;
import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.datatype.ItemStackDataType;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Drawer {

    private static final Qdrawer main = Qdrawer.getPlugin(Qdrawer.class);

    private final NamespacedKey itemKey = new NamespacedKey(main, "item");
    private final NamespacedKey quantityKey = new NamespacedKey(main, "quantity");
    private final NamespacedKey frameUUIDKey = new NamespacedKey(main, "frameUUID");
    private final NamespacedKey upgradesKey = new NamespacedKey(main, "upgrades");

    private final Block blockDrawer;
    private TileState tileStateDrawer;
    private PersistentDataContainer data;

    private ItemStack item;
    private Integer quantity;
    private String frameUUID;
    private int @NotNull [] upgrades;

    public Drawer(@NotNull Block blockDrawer) throws NoTileStateException, NotDrawerException {

        this.blockDrawer = blockDrawer;

        this.initDrawer();

    }

    public Drawer(String uuid, World world) throws NotDrawerException, NoTileStateException {

        ItemFrame frame = (ItemFrame) world.getEntity(UUID.fromString(uuid));

        assert frame != null;
        this.blockDrawer = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());

        this.initDrawer();

    }

    private void initDrawer() throws NoTileStateException, NotDrawerException {

        try {
            this.tileStateDrawer = (TileState) this.blockDrawer.getState();
            this.data = this.tileStateDrawer.getPersistentDataContainer();
        } catch (ClassCastException e) {
            throw new NoTileStateException();
        }

        if (!this.data.has(new NamespacedKey(main, "type"), PersistentDataType.STRING)) throw new NotDrawerException();
        if (!this.data.get(new NamespacedKey(main, "type"), PersistentDataType.STRING).equals("drawer")) throw new NotDrawerException();

        this.item = this.data.getOrDefault(itemKey, new ItemStackDataType(), new ItemStack(Material.AIR));
        this.quantity = this.data.getOrDefault(quantityKey, PersistentDataType.INTEGER, 0);
        this.frameUUID = this.data.get(frameUUIDKey, PersistentDataType.STRING);
        this.upgrades = this.data.getOrDefault(upgradesKey, PersistentDataType.INTEGER_ARRAY, new int[]{0, 0, 0});

    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        this.data.set(this.itemKey, new ItemStackDataType(), item);
        this.tileStateDrawer.update();
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.data.set(this.quantityKey, PersistentDataType.INTEGER, quantity);
        this.tileStateDrawer.update();
    }

    public UUID getFrameUUID() { return UUID.fromString(this.frameUUID); }

    public void setFrameUUID(UUID frameUUID) {
        String stringFrameUUID = frameUUID.toString();
        this.frameUUID = stringFrameUUID;
        this.data.set(this.frameUUIDKey, PersistentDataType.STRING, stringFrameUUID);
        this.tileStateDrawer.update();
    }

    public ItemStack takeItem(Integer quantity) {
        ItemStack item = this.item.asQuantity(quantity);
        this.setQuantity(this.getQuantity() - quantity);

        return item;
    }

    public void addItem(ItemStack item) {
        this.setItem(item.asOne());
        this.setQuantity(this.getQuantity() + item.getAmount());
    }

    public int[] getUpgrades() {
        return this.upgrades;
    }

    public void setUpgrades(int[] upgrades) {
        this.upgrades = upgrades;
        this.data.set(this.upgradesKey, PersistentDataType.INTEGER_ARRAY, upgrades);
        this.tileStateDrawer.update();
    }


    public ItemStack getDisplayItemStack() {

        ItemStack displayItem = this.item.clone();

        if (displayItem.getType().equals(Material.AIR)) {
            displayItem.setType(Material.BARRIER);
        }

        ItemMeta itemMeta = displayItem.getItemMeta();

        if (this.quantity <= 0) {
            itemMeta.displayName(Component.text("Empty"));
        }
        else {
            itemMeta.displayName(Component.text("x" + this.quantity + " ").append(this.item.displayName()));
        }
        displayItem.setItemMeta(itemMeta);

        return displayItem;
    }

    public void createFrame() {
        Directional blockDirection = (Directional) this.blockDrawer.getBlockData();
        Block frontBlock = this.blockDrawer.getRelative(blockDirection.getFacing());

        World world = this.blockDrawer.getWorld();
        ItemFrame frame = (ItemFrame) world.spawnEntity(frontBlock.getLocation(), EntityType.ITEM_FRAME);
        frame.setFacingDirection(blockDirection.getFacing());
        frame.setFixed(true);
        frame.setVisible(false);
        PersistentDataContainer dataFrame = frame.getPersistentDataContainer();
        dataFrame.set(new NamespacedKey(main, "type"), PersistentDataType.STRING, "drawerframe");
        this.setFrameUUID(frame.getUniqueId());

        this.updateFrame();
    }

    public void updateFrame() {

        ItemFrame frame = (ItemFrame) this.tileStateDrawer.getWorld().getEntity(UUID.fromString(this.frameUUID));

        assert frame != null;
        frame.setItem(this.getDisplayItemStack());

    }

    public ItemStack getDrawerItemStack() {

        ItemStack itemDrawer = new ItemStack(Material.BARREL);
        ItemMeta metaDrawer = itemDrawer.getItemMeta();
        PersistentDataContainer dataDrawer = metaDrawer.getPersistentDataContainer();

        dataDrawer.set(new NamespacedKey(main, "type"), PersistentDataType.STRING, "drawer");
        dataDrawer.set(this.itemKey, new ItemStackDataType(), this.item);
        dataDrawer.set(this.quantityKey, PersistentDataType.INTEGER, this.quantity);

        metaDrawer.displayName(Component.text("Drawer").color(NamedTextColor.GOLD));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Contain " + this.quantity + " ").append(this.item.displayName()));
        metaDrawer.lore(lore);

        itemDrawer.setItemMeta(metaDrawer);

        return itemDrawer;
    }

    public static ItemStack getNewDrawerItemStack() {

        ItemStack itemDrawer = new ItemStack(Material.BARREL);
        ItemMeta metaDrawer = itemDrawer.getItemMeta();
        PersistentDataContainer dataDrawer = metaDrawer.getPersistentDataContainer();

        dataDrawer.set(new NamespacedKey(main, "type"), PersistentDataType.STRING, "drawer");
        metaDrawer.displayName(Component.text("Drawer").color(NamedTextColor.GOLD));

        itemDrawer.setItemMeta(metaDrawer);

        return itemDrawer;
    }

    public Inventory getGui() {

        Inventory drawerGui = Bukkit.createInventory(null, InventoryType.BREWING, Component.text("Drawer Upgrade").color(NamedTextColor.GOLD));

        drawerGui.setItem(0, new Upgrade(UpgradeType.STORAGE, this.upgrades[0]).getItemStack());
        drawerGui.setItem(1, new Upgrade(UpgradeType.COLLECTION, this.upgrades[1]).getItemStack());
        drawerGui.setItem(2, new Upgrade(UpgradeType.AUTOSELL, this.upgrades[2]).getItemStack());
        ItemStack infoPaper = new ItemStack(Material.PAPER);
        ItemMeta metaInfoPaper = infoPaper.getItemMeta();
        metaInfoPaper.displayName(Component.text("Information"));
        List<Component> infoPaperlore = new ArrayList<>();
        infoPaperlore.add(GsonComponentSerializer.gson().deserialize(main.getConfig().getString("drawer.gui.information", "{\"text\":\"baba\"}")));
        metaInfoPaper.lore(infoPaperlore);
        metaInfoPaper.getPersistentDataContainer().set(new NamespacedKey(main, "drawer_id"), PersistentDataType.STRING, this.frameUUID);
        infoPaper.setItemMeta(metaInfoPaper);
        drawerGui.setItem(3,infoPaper);

        return drawerGui;

    }

    public void putItemInBarrelInv() {
        Inventory drawerInv = ((Container) this.blockDrawer.getState()).getInventory();
        drawerInv.clear();
        drawerInv.addItem(this.item.asQuantity(this.quantity < 864 ? this.quantity : 864));
    }

    public Block getBlockDrawer() {
        return blockDrawer;
    }
}
