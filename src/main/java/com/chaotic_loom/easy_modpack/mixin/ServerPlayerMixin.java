package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.llamalad7.mixinextras.sugar.Local;
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
        }
    }

    // Item cancel use
    /*@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void cancelItemUse(Entity entity, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        Item item = player.getMainHandItem().getItem();

        if () {
            ci.cancel();
        }
    }*/
}
