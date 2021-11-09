package me.bafbi.qdrawer.models.upgrade;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.Exeptions.NoTileStateException;
import me.bafbi.qdrawer.Exeptions.NotUpgradeException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class Upgrade {

    private final Qdrawer main = Qdrawer.getPlugin(Qdrawer.class);
    private ItemStack upgradeItem;
    private final UpgradeType type;
    private Integer tier;

    public Upgrade(UpgradeType type, Integer tier) {
        this.type = type;
        if ((type.equals(UpgradeType.STORAGE) || type.equals(UpgradeType.AUTOSELL)) && tier > 4) this.tier = 4;
        else if (type.equals(UpgradeType.COLLECTION) && tier > 2) this.tier = 2;
        else this.tier = tier;
    }

    public Upgrade(ItemStack upgradeItem) throws NotUpgradeException, NoTileStateException {

        this.upgradeItem = upgradeItem.asOne();
        PersistentDataContainer data;

        try {
            data = upgradeItem.getItemMeta().getPersistentDataContainer();
        } catch (NullPointerException e) {
            throw new NoTileStateException();
        }

        if (!data.has(new NamespacedKey(main, "type"), PersistentDataType.STRING)) throw new NotUpgradeException();
        if (!data.get(new NamespacedKey(main, "type"), PersistentDataType.STRING).equals("upgrade")) throw new NotUpgradeException();

        this.type = UpgradeType.valueOf(data.get(new NamespacedKey(main, "upgrade_type"), PersistentDataType.STRING));
        this.tier = data.get(new NamespacedKey(main, "upgrade_tier"), PersistentDataType.INTEGER);

    }

    public ItemStack getItemStack() {

        ConfigurationSection config = Qdrawer.getConfigStatic().getConfigurationSection("upgrade." + type.toString().toLowerCase());
        assert config != null;
        ConfigurationSection tierConfig = config.getConfigurationSection("tier" + tier);
        assert tierConfig != null;

        ItemStack upgradeItem = new ItemStack(Material.valueOf(tierConfig.getString("material", "BARRIER")));
        ItemMeta metaUpgrade = upgradeItem.getItemMeta();

        metaUpgrade.displayName(GsonComponentSerializer.gson().deserialize(config.getString("displayName", "{\"text\":\"baba\"}").replace("%color%", tierConfig.getString("color", "#ff0000"))));
        List<String> stringLore = (List<String>) config.getList("lore", new ArrayList<>());
        List<Component> lore = new ArrayList<>();
        for (String string : stringLore) {
            string = string.replace("%tier%", this.tier.toString()).replace("%param1%", tierConfig.getString("param1", "baba"));
            lore.add(GsonComponentSerializer.gson().deserialize(string));
        }
        metaUpgrade.lore(lore);

        if (config.getBoolean("glowing", false)) {
            metaUpgrade.addEnchant(Enchantment.LURE, 1, true);
            metaUpgrade.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        PersistentDataContainer dataUpgrade = metaUpgrade.getPersistentDataContainer();

        dataUpgrade.set(new NamespacedKey(main, "type"), PersistentDataType.STRING, "upgrade");
        dataUpgrade.set(new NamespacedKey(main, "upgrade_type"), PersistentDataType.STRING, this.type.toString());
        dataUpgrade.set(new NamespacedKey(main, "upgrade_tier"), PersistentDataType.INTEGER, this.tier);

        upgradeItem.setItemMeta(metaUpgrade);
        return upgradeItem;

    }

    public ItemStack getUpgradeItem() {
        return upgradeItem;
    }

    public UpgradeType getUpgradeType() {
        return this.type;
    }

    public Integer getTier() {
        return this.tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

}
