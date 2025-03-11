package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Redirect(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"
            )
    )
    private ImmutableMap.Builder<ResourceLocation, Recipe<?>> redirectPut(ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder, Object key, Object value, @Local Map.Entry<ResourceLocation, JsonElement> entry) {
        ResourceLocation recipeId = (ResourceLocation) key;
        Recipe<?> recipe = (Recipe<?>) value;

        if (isRecipeDisabled(recipeId)) {
            return builder;
        }

        ResourceLocation resultItemId = getResultItemId(entry.getValue());
        if (resultItemId != null && isItemDisabled(resultItemId)) {
            return builder;
        }

        if (hasDisabledIngredients(recipe)) {
            return builder;
        }

        builder.put(recipeId, recipe);
        return builder;
    }

    @Unique
    private ResourceLocation getResultItemId(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("result")) {
            JsonElement result = jsonObject.get("result");

            if (result.isJsonObject()) {
                return new ResourceLocation(result.getAsJsonObject().get("item").getAsString());
            } else {
                return new ResourceLocation(result.getAsString());
            }
        } else if (jsonObject.has("template")) {
            JsonElement template = jsonObject.get("template");

            return new ResourceLocation(template.getAsJsonObject().get("item").getAsString());
        } else {
            System.out.println("RESULT NOT FOUND");
            System.out.println(jsonElement);
        }

        return null;
    }

    @Unique
    private boolean hasDisabledIngredients(Recipe<?> recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack stack : ingredient.getItems()) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (isItemDisabled(itemId)) {
                    return true;
                }
            }
        }

        return false;
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
