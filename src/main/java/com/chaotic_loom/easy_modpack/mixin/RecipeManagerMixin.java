package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Redirect(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"
            )
    )
    private ImmutableMap.Builder<ResourceLocation, Recipe<?>> redirectPut(ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder, Object key, Object value) {
        ResourceLocation recipeId = (ResourceLocation) key;
        Recipe<?> recipe = (Recipe<?>) value;

        // Verificar si la receta está deshabilitada
        if (isRecipeDisabled(recipeId)) {
            return builder;
        }

        // Obtener el ítem resultante de la receta
        RegistryAccess registryAccess = RegistryAccess.EMPTY;
        ItemStack resultStack = recipe.getResultItem(registryAccess);
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(resultStack.getItem());

        System.out.println(itemId);

        // Si el ítem está deshabilitado, se omite la inserción
        if (isItemDisabled(itemId)) {
            return builder;
        }

        builder.put(recipeId, recipe);
        return builder;
    }

    @Unique
    private boolean isItemDisabled(ResourceLocation location) {
        return ItemManager.isDisabled(location);
    }

    @Unique
    private boolean isRecipeDisabled(ResourceLocation recipeId) {
        return com.chaotic_loom.easy_modpack.modules.recipes.RecipeManager.isDisabled(recipeId);
    }
}
