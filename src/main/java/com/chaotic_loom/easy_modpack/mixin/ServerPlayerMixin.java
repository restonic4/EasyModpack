package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
Replaces and deletes items from player inventories
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(
            method = "doTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"
            )
    )
    public void removeItemFromInventory(CallbackInfo info, @Local int i) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ItemStack item = player.getInventory().getItem(i);

        if (EasyModpack.ITEM_MANAGER.isDisabled(item.getItem())) {
            item.setCount(0);
        } else if (EasyModpack.ITEM_MANAGER.hasReplacement(Utils.getItemLocation(item.getItem()))) {
            ResourceLocation replacement = EasyModpack.ITEM_MANAGER.getReplacement(Utils.getItemLocation(item.getItem()));

            ItemStack newItemStack = new ItemStack(Utils.getItem(replacement));
            Utils.copyItemStackProperties(item, newItemStack);

            player.getInventory().setItem(i, newItemStack);
        }
    }
}
