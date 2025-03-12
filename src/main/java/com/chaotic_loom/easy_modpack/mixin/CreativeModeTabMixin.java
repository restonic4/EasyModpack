package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

    // IDK how but this makes JEI not show the items, cool, not with REI though
    @Inject(method = "buildContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;rebuildSearchTree()V"))
    private void removeItems(CreativeModeTab.ItemDisplayParameters displayParameters, CallbackInfo ci) {
        //this.displayItems.removeIf(stack -> ItemManager.isDisabled(stack.getItem()));
        //this.displayItemsSearchTab.removeIf(stack -> ItemManager.isDisabled(stack.getItem()));
    }
}
