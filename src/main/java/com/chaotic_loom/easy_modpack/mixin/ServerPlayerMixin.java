package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.Utils;
import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        if (ItemManager.isDisabled(item.getItem())) {
            //item.setCount(0);
        } /*else if (ItemManager.hasReplacement(Utils.getItemLocation(item.getItem()))) {
            ResourceLocation replacement = ItemManager.getReplacement(Utils.getItemLocation(item.getItem()));

            ItemStack newItemStack = new ItemStack(Utils.getItem(replacement));
            newItemStack.setCount(item.getCount());

            player.getInventory().setItem(i, newItemStack);
        }*/
    }
}
