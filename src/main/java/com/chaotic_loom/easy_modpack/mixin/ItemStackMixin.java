package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
Prevents using items
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (ItemManager.isDisabledUsage(player.getItemInHand(interactionHand).getItem())) {
            cir.setReturnValue(InteractionResultHolder.fail(player.getItemInHand(interactionHand)));
            cir.cancel();
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = useOnContext.getPlayer();
        if (player == null) {
            return;
        }

        InteractionHand interactionHand = player.getUsedItemHand();

        if (ItemManager.isDisabledUsage(player.getItemInHand(interactionHand).getItem())) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }
}
