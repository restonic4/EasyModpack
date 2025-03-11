package com.chaotic_loom.easy_modpack;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.entities.EntitiesManager;
import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.chaotic_loom.easy_modpack.modules.recipes.RecipeManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EasyModpack implements ModInitializer {
    public static String MOD_ID = "easy_modpack";
    private static boolean configLoaded = false;

    @Override
    public void onInitialize() {
        ConfigManager.initConfig("items");
        ConfigManager.initConfig("recipes");
        ConfigManager.initConfig("entities");

        ItemManager.register();
        RecipeManager.register();
        EntitiesManager.register();

        configLoaded = true;
    }

    public static boolean isConfigLoaded() {
        return configLoaded;
    }
}
