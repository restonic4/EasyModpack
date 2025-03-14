package com.chaotic_loom.easy_modpack.mixin.accessors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor("recipes")
    @Mutable
    void setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes);

    @Accessor("byName")
    Map<ResourceLocation, Recipe<?>> getAllRecipesMap();

    @Accessor("byName")
    @Mutable
    void setAllRecipesMap(Map<ResourceLocation, Recipe<?>> recipeFlatMap);
}
