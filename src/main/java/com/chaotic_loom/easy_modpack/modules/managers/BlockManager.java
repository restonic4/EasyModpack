package com.chaotic_loom.easy_modpack.modules.managers;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockManager extends BaseManager {
    public void register() {
        reload("blocks");
    }

    public boolean isDisabled(Block result) {
        for (ResourceLocation item : this.disabledObjects) {
            if (BuiltInRegistries.BLOCK.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }
}
