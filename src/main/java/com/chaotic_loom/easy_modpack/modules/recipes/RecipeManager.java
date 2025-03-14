package com.chaotic_loom.easy_modpack.modules.recipes;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {
    private static List<ResourceLocation> disabledRecipes = new ArrayList<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledRecipesRaw = ConfigManager.getConfigArray("recipes", "disabled");
        for (String recipeID : disabledRecipesRaw) {
            disabledRecipes.add(new ResourceLocation(recipeID));
        }
    }

    public static boolean isDisabled(ResourceLocation location){
        return disabledRecipes.contains(location);
    }
}
