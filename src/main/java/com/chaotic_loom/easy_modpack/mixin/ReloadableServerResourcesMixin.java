package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.mixin.accessors.RecipeManagerAccessor;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Disables and replaces recipes and it's ingredients
 */
@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Shadow @Final private RecipeManager recipes;

    @Inject(
            method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V",
            at = @At("TAIL")
    )
    private void filterRecipes(RegistryAccess registryManager, CallbackInfo ci) {
        RecipeManagerAccessor accessibleManager = (RecipeManagerAccessor) this.recipes;

        Map<ResourceLocation, Recipe<?>> originalFlatMap = accessibleManager.getAllRecipesMap();
        Map<ResourceLocation, Recipe<?>> filteredFlatMap = new HashMap<>();

        originalFlatMap.forEach((recipeId, recipe) -> {
            Recipe<?> modifiedRecipe = replaceIngredientsInRecipe(recipe, registryManager);
            modifiedRecipe = replaceResultInRecipe(modifiedRecipe, registryManager);
            if (shouldRemoveRecipe(registryManager, recipeId, modifiedRecipe)) {
                return;
            }
            filteredFlatMap.put(recipeId, modifiedRecipe);
        });

        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> groupedRecipes = new HashMap<>();
        filteredFlatMap.forEach((recipeId, recipeHolder) -> {
            RecipeType<?> type = recipeHolder.getType();
            groupedRecipes.computeIfAbsent(type, t -> new HashMap<>()).put(recipeId, recipeHolder);
        });

        ImmutableMap.Builder<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesBuilder = ImmutableMap.builder();
        groupedRecipes.forEach((type, map) ->
                recipesBuilder.put(type, ImmutableMap.copyOf(map))
        );

        accessibleManager.setRecipes(recipesBuilder.build());
        accessibleManager.setAllRecipesMap(ImmutableMap.copyOf(filteredFlatMap));
    }

    @Unique
    private Recipe<?> replaceIngredientsInRecipe(Recipe<?> original, RegistryAccess registryAccess) {
        if (original instanceof ShapelessRecipe) {
            return replaceShapelessIngredients((ShapelessRecipe) original, registryAccess);
        } else if (original instanceof ShapedRecipe) {
            return replaceShapedIngredients((ShapedRecipe) original, registryAccess);
        }
        return original;
    }

    @Unique
    private Recipe<?> replaceResultInRecipe(Recipe<?> original, RegistryAccess registryAccess) {
        if (original instanceof ShapelessRecipe) {
            return replaceShapelessResult((ShapelessRecipe) original, registryAccess);
        } else if (original instanceof ShapedRecipe) {
            return replaceShapedResult((ShapedRecipe) original, registryAccess);
        }
        return original;
    }

    @Unique
    private Recipe<?> replaceShapelessResult(ShapelessRecipe original, RegistryAccess registryAccess) {
        ItemStack originalResult = original.getResultItem(registryAccess);
        ItemStack modifiedResult = replaceResultItem(originalResult);

        return new ShapelessRecipe(
                original.getId(),
                original.getGroup(),
                original.category(),
                modifiedResult,
                original.getIngredients()
        );
    }

    @Unique
    private Recipe<?> replaceShapedResult(ShapedRecipe original, RegistryAccess registryAccess) {
        ItemStack originalResult = original.getResultItem(registryAccess);
        ItemStack modifiedResult = replaceResultItem(originalResult);

        return new ShapedRecipe(
                original.getId(),
                original.getGroup(),
                original.category(),
                original.getWidth(),
                original.getHeight(),
                original.getIngredients(),
                modifiedResult
        );
    }

    @Unique
    private ItemStack replaceResultItem(ItemStack originalResult) {
        ResourceLocation itemId = Utils.getItemLocation(originalResult.getItem());
        if (shouldItemBeReplaced(itemId)) {
            ResourceLocation replacementId = getItemReplacement(itemId);
            Item replacementItem = Utils.getItem(replacementId);
            if (replacementItem != null) {
                return new ItemStack(replacementItem, originalResult.getCount());
            }
        }
        return originalResult.copy();
    }

    @Unique
    private Recipe<?> replaceShapelessIngredients(ShapelessRecipe original, RegistryAccess registryAccess) {
        NonNullList<Ingredient> modifiedIngredients = NonNullList.create();
        for (Ingredient ingredient : original.getIngredients()) {
            modifiedIngredients.add(replaceIngredient(ingredient, registryAccess));
        }
        return new ShapelessRecipe(
                original.getId(),
                original.getGroup(),
                original.category(),
                original.getResultItem(registryAccess),
                modifiedIngredients
        );
    }

    @Unique
    private Recipe<?> replaceShapedIngredients(ShapedRecipe original, RegistryAccess registryAccess) {
        NonNullList<Ingredient> modifiedIngredients = NonNullList.create();
        for (Ingredient ingredient : original.getIngredients()) {
            modifiedIngredients.add(replaceIngredient(ingredient, registryAccess));
        }
        return new ShapedRecipe(
                original.getId(),
                original.getGroup(),
                original.category(),
                original.getWidth(),
                original.getHeight(),
                modifiedIngredients,
                original.getResultItem(registryAccess)
        );
    }

    @Unique
    private Ingredient replaceIngredient(Ingredient original, RegistryAccess registryAccess) {
        List<ItemStack> modifiedStacks = new ArrayList<>();
        for (ItemStack stack : original.getItems()) {
            ResourceLocation itemId = Utils.getItemLocation(stack.getItem());
            if (shouldItemBeReplaced(itemId)) {
                ResourceLocation replacementId = getItemReplacement(itemId);
                Item replacementItem = Utils.getItem(replacementId);
                if (replacementItem != null) {
                    modifiedStacks.add(new ItemStack(replacementItem, stack.getCount()));
                    continue;
                }
            }
            modifiedStacks.add(stack);
        }
        return Ingredient.of(modifiedStacks.toArray(new ItemStack[0]));
    }

    @Unique
    private boolean shouldRemoveRecipe(RegistryAccess registryManager, ResourceLocation recipeId, Recipe<?> recipe) {
        if (isRecipeDisabled(recipeId)) {
            return true;
        }

        ResourceLocation resultId = Utils.getItemLocation(recipe.getResultItem(registryManager).getItem());
        if (isItemDisabled(resultId)) {
            return true;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            boolean hasValidItem = false;
            if (ingredient.isEmpty()) continue;

            for (ItemStack stack : ingredient.getItems()) {
                ResourceLocation itemId = Utils.getItemLocation(stack.getItem());
                if (!isItemDisabled(itemId)) {
                    hasValidItem = true;
                    break;
                }
            }

            if (!hasValidItem) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean isItemDisabled(ResourceLocation itemId) {
        return ItemManager.isDisabled(itemId);
    }

    @Unique
    private boolean isRecipeDisabled(ResourceLocation recipeId) {
        return com.chaotic_loom.easy_modpack.modules.recipes.RecipeManager.isDisabled(recipeId);
    }

    @Unique
    private boolean shouldItemBeReplaced(ResourceLocation itemId) {
        return ItemManager.hasReplacement(itemId);
    }

    @Unique
    private ResourceLocation getItemReplacement(ResourceLocation itemId) {
        return ItemManager.getReplacement(itemId);
    }
}
