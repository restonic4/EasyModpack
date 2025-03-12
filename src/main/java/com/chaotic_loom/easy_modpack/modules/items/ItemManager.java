package com.chaotic_loom.easy_modpack.modules.items;

import com.chaotic_loom.easy_modpack.Constants;
import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
    private static List<ResourceLocation> disabledItems = new ArrayList<>();
    private static List<ResourceLocation> disabledUsageItems = new ArrayList<>();
    private static Map<ResourceLocation, ResourceLocation> replacedItems = new HashMap<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledItemsRaw = ConfigManager.getConfigArray("items", "disabled");
        for (String itemRawID : disabledItemsRaw) {
            disabledItems.add(new ResourceLocation(itemRawID));
        }

        List<String> disabledItemsUsageRaw = ConfigManager.getConfigArray("items", "disabled_usage");
        for (String itemRawID : disabledItemsUsageRaw) {
            disabledUsageItems.add(new ResourceLocation(itemRawID));
        }

        List<Pair<String, String>> replacedItemsRaw = ConfigManager.getConfigPairArray("items", "replace");
        for (Pair<String, String> itemRawID : replacedItemsRaw) {
            replacedItems.put(new ResourceLocation(itemRawID.getFirst()), new ResourceLocation(itemRawID.getSecond()));
        }
    }

    public static boolean isDisabled(ResourceLocation location) {
        return disabledItems.contains(location);
    }

    public static boolean isDisabled(Item result) {
        for (ResourceLocation item : disabledItems) {
            if (BuiltInRegistries.ITEM.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isDisabledUsage(ResourceLocation location) {
        return disabledUsageItems.contains(location);
    }

    public static boolean isDisabledUsage(Item result) {
        for (ResourceLocation item : disabledUsageItems) {
            if (BuiltInRegistries.ITEM.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasReplacement(ResourceLocation original) {
        return replacedItems.containsKey(original);
    }

    public static ResourceLocation getReplacement(ResourceLocation original) {
        return replacedItems.get(original);
    }

    public static void removeDisabledItems(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (ItemManager.isDisabled(itemStack.getItem())) {
                container.removeItem(i, itemStack.getCount());
            }
        }
    }
}
