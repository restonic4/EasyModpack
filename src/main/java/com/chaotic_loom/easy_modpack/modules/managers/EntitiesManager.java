package com.chaotic_loom.easy_modpack.modules.managers;

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

public class EntitiesManager extends BaseManager {
    public void register() {
        reload("entities");

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            ResourceLocation entityId = Utils.getEntityLocation(entity);

            if (isDisabled(entityId)) {
                entity.discard();
            } else if (hasReplacement(entityId)) {
                Entity replacementEntity = Utils.getEntity(getReplacement(entityId), serverLevel, entity.position());
                serverLevel.tryAddFreshEntityWithPassengers(replacementEntity);
                entity.discard();
            }
        });
    }

    public boolean isDisabled(Entity entity){
        EntityType<?> entityType = entity.getType();
        ResourceLocation entityLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        return this.disabledObjects.contains(entityLocation);
    }
}
