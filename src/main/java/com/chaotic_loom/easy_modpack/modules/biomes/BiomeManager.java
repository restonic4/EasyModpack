package com.chaotic_loom.easy_modpack.modules.biomes;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeManager {
    private static List<ResourceLocation> disabledBiomes = new ArrayList<>();
    private static Map<ResourceLocation, ResourceLocation> replacedBiomes = new HashMap<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledItemsRaw = ConfigManager.getConfigArray("biomes", "disabled");
        for (String itemRawID : disabledItemsRaw) {
            disabledBiomes.add(new ResourceLocation(itemRawID));
        }

        List<Pair<String, String>> replacedItemsRaw = ConfigManager.getConfigPairArray("biomes", "replace");
        for (Pair<String, String> itemRawID : replacedItemsRaw) {
            replacedBiomes.put(new ResourceLocation(itemRawID.getFirst()), new ResourceLocation(itemRawID.getSecond()));
        }
    }

    public static boolean isDisabled(ResourceLocation location) {
        return disabledBiomes.contains(location);
    }

    public static boolean hasReplacement(ResourceLocation original) {
        return replacedBiomes.containsKey(original);
    }

    public static ResourceLocation getReplacement(ResourceLocation original) {
        return replacedBiomes.get(original);
    }
}
