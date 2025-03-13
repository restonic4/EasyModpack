package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.Utils;
import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void discard(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack itemStack = self.getItem();

        ResourceLocation itemID = Utils.getItemLocation(itemStack.getItem());

        if (ItemManager.isDisabled(itemID)) {
            self.discard();
        }

        if (ItemManager.hasReplacement(itemID)) {
            ItemStack newItemStack = new ItemStack(Utils.getItem(ItemManager.getReplacement(itemID)));
            Utils.copyItemStackProperties(itemStack, newItemStack);

            self.setItem(newItemStack);
        }
    }
}
