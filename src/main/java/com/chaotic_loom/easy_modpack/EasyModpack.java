package com.chaotic_loom.easy_modpack;

import com.chaotic_loom.easy_modpack.compatibility.terrablender.TerrablenderCompatibility;
import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.PlatformHelper;
import com.chaotic_loom.easy_modpack.modules.managers.BiomeManager;
import com.chaotic_loom.easy_modpack.modules.managers.BlockManager;
import com.chaotic_loom.easy_modpack.modules.managers.EntitiesManager;
import com.chaotic_loom.easy_modpack.modules.managers.ItemManager;
import com.chaotic_loom.easy_modpack.modules.managers.RecipeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EasyModpack implements ModInitializer {
    public static String MOD_ID = "easy_modpack";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static boolean configLoaded = false;
    private static MinecraftServer currentServer = null;

    public static final ItemManager ITEM_MANAGER = new ItemManager();
    public static final RecipeManager RECIPE_MANAGER = new RecipeManager();
    public static final EntitiesManager ENTITIES_MANAGER = new EntitiesManager();
    public static final BlockManager BLOCK_MANAGER = new BlockManager();
    public static final BiomeManager BIOME_MANAGER = new BiomeManager();

    @Override
    public void onInitialize() {
        ConfigManager.initConfig("items");
        ConfigManager.initConfig("recipes");
        ConfigManager.initConfig("entities");
        ConfigManager.initConfig("blocks");
        ConfigManager.initConfig("biomes");

        ITEM_MANAGER.register();
        RECIPE_MANAGER.register();
        ENTITIES_MANAGER.register();
        BLOCK_MANAGER.register();
        BIOME_MANAGER.register();

        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            currentServer = minecraftServer;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((minecraftServer) -> {
            currentServer = null;
        });

        configLoaded = true;

        if (PlatformHelper.isModLoaded("terrablender")) {
            TerrablenderCompatibility.warn();
        }
    }

    public static boolean isConfigLoaded() {
        return configLoaded;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }
}
