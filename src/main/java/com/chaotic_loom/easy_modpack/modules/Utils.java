package com.chaotic_loom.easy_modpack.modules;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class Utils {
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

    public static ResourceLocation getEntityLocation(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }

    public static Entity getEntity(ResourceLocation id, ServerLevel serverLevel, Vec3 position) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);

        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", id.toString());

        return EntityType.loadEntityRecursive(compoundTag, serverLevel, finalEntity -> {
            finalEntity.moveTo(position.x, position.y, position.z, finalEntity.getYRot(), finalEntity.getXRot());
            return finalEntity;
        });
    }

    public static void copyItemStackProperties(ItemStack old, ItemStack newStack) {
        newStack.setCount(old.getCount());
        newStack.setTag(old.getTag());
        newStack.setDamageValue(old.getDamageValue());
        //newStack.setHoverName(old.getHoverName());
        newStack.setPopTime(old.getPopTime());
        newStack.setRepairCost(old.getBaseRepairCost());
    }

    public static Holder<Biome> findBiomeByResourceLocation(MinecraftServer server, ResourceLocation biomeId) {
        if (server != null) {
            return server.registryAccess().registryOrThrow(Registries.BIOME)
                    .getHolder(ResourceKey.create(Registries.BIOME, biomeId))
                    .orElse(null);
        }
        return null;
    }
}
