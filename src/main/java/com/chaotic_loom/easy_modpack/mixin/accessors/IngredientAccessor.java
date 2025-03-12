package com.chaotic_loom.easy_modpack.mixin.accessors;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
    @Accessor("itemStacks")
    @Mutable
    void setItemStacks(ItemStack[] itemStacks);
}
