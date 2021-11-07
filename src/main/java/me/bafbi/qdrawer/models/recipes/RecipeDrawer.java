package me.bafbi.qdrawer.models.recipes;

import me.bafbi.qdrawer.Qdrawer;
import me.bafbi.qdrawer.models.Drawer;
import me.bafbi.qdrawer.models.upgrade.Upgrade;
import me.bafbi.qdrawer.models.upgrade.UpgradeType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ShapedRecipe;
import java.util.*;

public class RecipeDrawer {

    private Qdrawer main;
    public static List<NamespacedKey> recipesList = new ArrayList<>();

    public RecipeDrawer(Qdrawer qdrawer) {
        this.main = qdrawer;
    }

    public void registerRecipe() {

        //main.getLogger().info("what");

        // create a NamespacedKey for your recipe
        NamespacedKey key = new NamespacedKey(this.main, "drawertest");

        // Create our custom recipe variable
        ShapedRecipe recipe = new ShapedRecipe(key, Drawer.getNewDrawerItemStack());

        // Here we will set the places. E and S can represent anything, and the letters can be anything. Beware; this is case sensitive.
        recipe.shape(" W ", "HBH", "DDD");

        recipe.setIngredient('B', Material.CHEST);
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('W', Material.HAY_BLOCK);
        recipe.setIngredient('H', Material.HOPPER);

        // Finally, add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);
    }

    public boolean registerRecipes() {

        if (Bukkit.getRecipe(new NamespacedKey(main, "drawer")) != null) {
            return true;
        }

        ConfigurationSection config = this.main.getConfig().getConfigurationSection("craft");

        NamespacedKey key;
        ShapedRecipe recipe;

        for (String string : config.getKeys(false)) {
            //main.getLogger().info(string + " :");

            switch (string) {
                case "drawer":
                    key = new NamespacedKey(this.main, string);
                    recipe = new ShapedRecipe(key, Drawer.getNewDrawerItemStack());
                    Bukkit.addRecipe(setShape(recipe, config.getStringList("drawer")));
                    recipesList.add(key);
                    //main.getLogger().info("register drawer");
                    break;
                case "upgrade":
                    for (String upgradekey : config.getConfigurationSection("upgrade").getKeys(false)) {
                        for (String tierkey : config.getConfigurationSection("upgrade." + upgradekey).getKeys(false)) {
                            key = new NamespacedKey(this.main, upgradekey + "_upgrade_" + tierkey);
                            recipe = new ShapedRecipe(key, new Upgrade(UpgradeType.valueOf(upgradekey.toUpperCase()), Integer.valueOf(tierkey)).getItemStack());
                            Bukkit.addRecipe(setShape(recipe, config.getStringList("upgrade." + upgradekey + "." + tierkey)));
                            recipesList.add(key);
                            //main.getLogger().info("register " + upgradekey + " " + tierkey);
                        }
                    }
                    break;
            }
        }

        return true;

    }

    private ShapedRecipe setShape(ShapedRecipe recipe, List<String> materials) {

        char[] key = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        short counter = 0;
        String[] shape = {" ", " ", " ", " ", " ", " ", " ", " ", " "};

        for (String material : materials) {

            if (Objects.equals(material, "AIR")) {
                counter++;
                continue;
            }

            shape[counter] = String.valueOf(key[counter]);
            recipe.shape(shape[0] + shape[1] + shape[2], shape[3] + shape[4] + shape[5], shape[6] + shape[7] + shape[8]);
            //main.getLogger().info(Arrays.toString(shape));
            recipe.setIngredient(key[counter], Material.valueOf(material));
            counter++;

        }

        return recipe;
    }

}
