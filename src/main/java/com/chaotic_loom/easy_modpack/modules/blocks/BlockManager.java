package com.chaotic_loom.easy_modpack.modules.blocks;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockManager {
    private static List<ResourceLocation> disabledBlocks = new ArrayList<>();
    private static Map<ResourceLocation, ResourceLocation> replacedBlocks = new HashMap<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledItemsRaw = ConfigManager.getConfigArray("blocks", "disabled");
        for (String itemRawID : disabledItemsRaw) {
            disabledBlocks.add(new ResourceLocation(itemRawID));
        }

        List<Pair<String, String>> replacedItemsRaw = ConfigManager.getConfigPairArray("blocks", "replace");
        for (Pair<String, String> itemRawID : replacedItemsRaw) {
            replacedBlocks.put(new ResourceLocation(itemRawID.getFirst()), new ResourceLocation(itemRawID.getSecond()));
        }
    }

    public static boolean isDisabled(ResourceLocation location) {
        return disabledBlocks.contains(location);
    }

    public static boolean isDisabled(Block result) {
        for (ResourceLocation item : disabledBlocks) {
            if (BuiltInRegistries.BLOCK.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasReplacement(ResourceLocation original) {
        return replacedBlocks.containsKey(original);
    }

    public static ResourceLocation getReplacement(ResourceLocation original) {
        return replacedBlocks.get(original);
    }
}
