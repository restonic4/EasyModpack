package com.chaotic_loom.easy_modpack.modules;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.stream.Collectors;

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

    public static Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(id);
    }

    public static ResourceLocation getBlockLocation(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static <T> Set<ResourceLocation> getTagElements(Registry<T> registry, String tagString) {
        // Construir la TagKey a partir del identificador del registro y la tag proporcionada.
        TagKey<T> tagKey = TagKey.create(registry.key(), new ResourceLocation(tagString));

        // Obtener la lista de entradas para esa tag; puede no estar presente.
        HolderSet<T> tagEntries = registry.getTag(tagKey).orElse(null);
        if (tagEntries == null) {
            return Set.of();
        }

        // Mapear cada entrada a su Identifier (ResourceLocation) y colectarlo en un Set.
        return tagEntries.stream()
                .map(Holder::value)
                .map(registry::getKey)
                .collect(Collectors.toSet());
    }

    public static void copyItemStackProperties(ItemStack old, ItemStack newStack) {
        newStack.setCount(old.getCount());
        newStack.setTag(old.getTag());
        newStack.setDamageValue(old.getDamageValue());
        //newStack.setHoverName(old.getHoverName());
        newStack.setPopTime(old.getPopTime());
        newStack.setRepairCost(old.getBaseRepairCost());
    }
}
