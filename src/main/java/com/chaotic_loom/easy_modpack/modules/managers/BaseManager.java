package com.chaotic_loom.easy_modpack.modules.managers;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseManager {
    protected List<ResourceLocation> disabledObjects = new ArrayList<>();
    protected Map<ResourceLocation, ResourceLocation> replacedObjects = new HashMap<>();

    public void reload(String configCategory) {
        // Load disabled objects
        List<String> disabledObjectsRaw = ConfigManager.getConfigArray(configCategory, "disabled");
        for (String rawID : disabledObjectsRaw) {
            disabledObjects.add(new ResourceLocation(rawID));
        }

        // Load replaced objects
        List<Pair<String, String>> replacedObjectsRaw = ConfigManager.getConfigPairArray(configCategory, "replace");
        for (Pair<String, String> pair : replacedObjectsRaw) {
            replacedObjects.put(new ResourceLocation(pair.getFirst()), new ResourceLocation(pair.getSecond()));
        }
    }

    public boolean isDisabled(ResourceLocation location) {
        return disabledObjects.contains(location);
    }

    public boolean hasReplacement(ResourceLocation original) {
        return replacedObjects.containsKey(original);
    }

    public ResourceLocation getReplacement(ResourceLocation original) {
        return replacedObjects.get(original);
    }
}