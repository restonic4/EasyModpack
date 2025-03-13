package com.chaotic_loom.easy_modpack.modules.entities;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitiesManager {
    private static List<ResourceLocation> disabledEntities = new ArrayList<>();
    private static Map<ResourceLocation, ResourceLocation> replacedEntites = new HashMap<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledEntitiesRaw = ConfigManager.getConfigArray("entities", "disabled");
        for (String entityID : disabledEntitiesRaw) {
            disabledEntities.add(new ResourceLocation(entityID));
        }

        List<Pair<String, String>> replacedItemsRaw = ConfigManager.getConfigPairArray("entities", "replace");
        for (Pair<String, String> itemRawID : replacedItemsRaw) {
            replacedEntites.put(new ResourceLocation(itemRawID.getFirst()), new ResourceLocation(itemRawID.getSecond()));
        }

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            ResourceLocation entityId = Utils.getEntityLocation(entity);

            if (isDisabled(entityId)) {
                entity.discard();
            }

            if (hasReplacement(entityId)) {
                Entity replacementEntity = Utils.getEntity(getReplacement(entityId), serverLevel, entity.position());
                serverLevel.tryAddFreshEntityWithPassengers(replacementEntity);

                entity.discard();
            }
        });
    }

    public static boolean isDisabled(ResourceLocation location){
        return disabledEntities.contains(location);
    }

    public static boolean isDisabled(Entity entity){
        EntityType<?> entityType = entity.getType();
        ResourceLocation entityLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        return disabledEntities.contains(entityLocation);
    }

    public static boolean hasReplacement(ResourceLocation original) {
        return replacedEntites.containsKey(original);
    }

    public static ResourceLocation getReplacement(ResourceLocation original) {
        return replacedEntites.get(original);
    }
}
