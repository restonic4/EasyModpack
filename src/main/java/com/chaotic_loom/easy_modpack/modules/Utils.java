package com.chaotic_loom.easy_modpack.modules;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Utils {
    public static Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(id));
    }

    public static Item getItem(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }

    public static ResourceLocation getItemLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
