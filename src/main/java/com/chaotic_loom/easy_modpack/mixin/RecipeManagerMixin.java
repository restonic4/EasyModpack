package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD")
    )
    private void inject(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
        System.out.println("Disabling recipes");

        Map<ResourceLocation, JsonElement> filteredMap = new HashMap<>();
        int disabledRecipes = 0;

        // Checking all recipes
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation recipeID = entry.getKey();
            JsonElement recipeData = entry.getValue();
            ResourceLocation resultID = getResultItemId(recipeData);

            boolean disabled = false;

            // Disabling specific recipes
            if (isRecipeDisabled(recipeID)) {
                disabled = true;
            }

            // Disabling recipes by result
            if (isItemDisabled(resultID)) {
                System.out.println(resultID);
                disabled = true;
            }

            // Disabling recipes by ingredients
            //TODO

            // Checking the result
            if (!disabled) {
                filteredMap.put(recipeID, recipeData);
            } else {
                System.out.println("Recipe " + recipeID + " disabled");
                disabledRecipes++;
            }
        }

        map.clear();
        map.putAll(filteredMap);

        System.out.println("Disabled " + disabledRecipes + " recipes");
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
            /*System.out.println("RESULT NOT FOUND");
            System.out.println(jsonElement);*/
        }

        return null;
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

/*
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow public abstract void replaceRecipes(Iterable<Recipe<?>> iterable);

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
        ResourceLocation resultItemId = getResultItemId(entry.getValue());

        if (isRecipeDisabled(builder, recipeId, recipe, resultItemId)) {
            return builder;
        }

        //printCraftingRecipe(recipeId, recipe);
        //replaceItems(recipeId, recipe, resultItemId);

        builder.put(recipeId, recipe);
        return builder;
    }

    @Unique
    private void replaceItems(ResourceLocation recipeId, Recipe<?> recipe, ResourceLocation resultItemId) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        if (recipeId.toString().equals("minecraft:crafting_table")) {
            System.out.println("STARTING WITH CRAFTING RECIPE");
        }

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            JsonElement jsonElement = ingredient.toJson();

            // Verificar si el ingrediente es un tag
            boolean isTag = false;
            if (jsonElement.isJsonObject()) {
                JsonObject json = jsonElement.getAsJsonObject();
                if (json.has("tag")) {
                    isTag = true;
                }
            }

            // Saltar tags para evitar romper recetas
            if (isTag) {
                continue; // No modificar ingredientes basados en tags
            }

            if (recipeId.toString().equals("minecraft:crafting_table")) {
                System.out.println("CRAFTING RECIPE IS REPLACING ITEMS");
            }

            // Procesar solo ingredientes que NO son tags
            ItemStack[] originalItems = ingredient.getItems();
            List<ItemStack> newItems = new ArrayList<>();

            for (ItemStack stack : originalItems) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

                if (ItemManager.hasReplacement(itemId)) {
                    // Crear nuevo ítem reemplazado
                    ItemStack newStack = new ItemStack(Utils.getItem(ItemManager.getReplacement(itemId)));
                    newStack.setCount(stack.getCount());

                    // Copiar NBT si existe
                    if (stack.hasTag()) {
                        newStack.setTag(stack.getTag().copy());
                    }

                    newItems.add(newStack);
                } else {
                    newItems.add(stack.copy());
                }
            }

            // Actualizar el ingrediente solo si hubo cambios
            if (!newItems.isEmpty()) {
                ingredients.set(i, Ingredient.of(newItems.toArray(new ItemStack[0])));
            }
        }

        if (recipeId.toString().equals("minecraft:crafting_table")) {
            System.out.println("CRAFTING RECIPE ENDED");
        }
    }

    @Unique
    private void printCraftingRecipe(ResourceLocation recipeId, Recipe<?> recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        if (recipeId.toString().equals("minecraft:crafting_table")) {
            System.out.println("CRAFTING TABLE RECIPE");
            System.out.println(" ");

            for (Ingredient ingredient : ingredients) {
                System.out.println("Ingredient:");
                System.out.println(ingredient.toJson());

                JsonElement jsonElement = ingredient.toJson();

                // Verificar si el ingrediente es un tag
                boolean isTag = false;
                if (jsonElement.isJsonObject()) {
                    JsonObject json = jsonElement.getAsJsonObject();
                    if (json.has("tag")) {
                        isTag = true;
                    }
                }

                // Saltar tags para evitar romper recetas
                if (isTag) {
                    System.out.println("Is recipe with tags");
                } else {
                    System.out.println("No tags found");
                }
            }

            System.out.println(" ");
            System.out.println("END OF CRAFTING TABLE RECIPE");
        }
    }

    @Unique
    private boolean isRecipeDisabled(ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder, ResourceLocation recipeId, Recipe<?> recipe, ResourceLocation resultItemId) {
        if (isRecipeDisabled(recipeId)) {
            return true;
        }

        if (resultItemId != null && isItemDisabled(resultItemId)) {
            return true;
        }

        return hasDisabledIngredients(recipe);
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
        if (recipe.getId().toString().equals("minecraft:crafting_table")) {
            System.out.println("CHECKING CRAFTING TABLE INGREDIENTS");
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (recipe.getId().toString().equals("minecraft:crafting_table")) {
                System.out.println("Ingredient: " + ingredient.toJson());
            }

            for (ItemStack stack : ingredient.getItems()) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

                if (isItemDisabled(itemId)) {
                    return true;
                }
            }
        }

        if (recipe.getId().toString().equals("minecraft:crafting_table")) {
            System.out.println("RECIPE NOT DISABLED");
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
*/