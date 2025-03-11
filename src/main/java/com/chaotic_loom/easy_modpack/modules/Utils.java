package com.chaotic_loom.easy_modpack.modules;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Utils {
    public static Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(getResourceLocation(id));
    }

    public static ResourceLocation getResourceLocation(String id) {
        String[] split = id.split(":");

        if (split.length == 1) {
            return new ResourceLocation(id);
        } else if (split.length == 2) {
            return new ResourceLocation(split[0], split[1]);
        }

        return new ResourceLocation(id);
    }
}
