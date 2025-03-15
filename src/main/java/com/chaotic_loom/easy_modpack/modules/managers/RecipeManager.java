package com.chaotic_loom.easy_modpack.modules.managers;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager extends BaseManager {
    public void register() {
        reload("recipes");
    }
}
