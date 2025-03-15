package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
Replaces items on ItemEntities
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void discard(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack itemStack = self.getItem();

        ResourceLocation itemID = Utils.getItemLocation(itemStack.getItem());

        if (EasyModpack.ITEM_MANAGER.isDisabled(itemID)) {
            self.discard();
        }

        if (EasyModpack.ITEM_MANAGER.hasReplacement(itemID)) {
            ItemStack newItemStack = new ItemStack(Utils.getItem(EasyModpack.ITEM_MANAGER.getReplacement(itemID)));
            Utils.copyItemStackProperties(itemStack, newItemStack);

            self.setItem(newItemStack);
        }
    }
}
