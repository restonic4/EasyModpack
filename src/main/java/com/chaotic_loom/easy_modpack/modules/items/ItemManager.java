package com.chaotic_loom.easy_modpack.modules.items;

import com.chaotic_loom.easy_modpack.Constants;
import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private static List<ResourceLocation> disabledItems = new ArrayList<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledItemsRaw = ConfigManager.getConfigArray("items", "disabled");
        for (String itemRawID : disabledItemsRaw) {
            String[] split = itemRawID.split(":");
            disabledItems.add(new ResourceLocation(split[0], split[1]));
        }

    }

    public static boolean isDisabled(ResourceLocation location) {
        for (ResourceLocation item : disabledItems) {
            if (location.equals(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isDisabled(Item result) {
        for (ResourceLocation item : disabledItems) {
            if (BuiltInRegistries.ITEM.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }
}
