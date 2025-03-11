package com.chaotic_loom.easy_modpack.modules.entities;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class EntitiesManager {
    private static List<ResourceLocation> disabledEntities = new ArrayList<>();

    public static void register() {
        reload();
    }

    public static void reload() {
        List<String> disabledEntitiesRaw = ConfigManager.getConfigArray("entities", "disabled");
        for (String entityID : disabledEntitiesRaw) {
            disabledEntities.add(new ResourceLocation(entityID));
        }

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            if (isDisabled(entity)) {
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
}
