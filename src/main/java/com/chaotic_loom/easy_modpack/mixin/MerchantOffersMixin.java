package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

/*
Disabled and replaces villagers (Or other modded traders, probably...) trades and items.
 */
@Mixin(MerchantOffers.class)
public class MerchantOffersMixin extends ArrayList<MerchantOffer> {
    @Override
    public boolean add(MerchantOffer offer) {
        // Disabled items check
        if (isItemDisabled(offer.getResult())
                || isItemDisabled(offer.getBaseCostA())
                || isItemDisabled(offer.getCostB())) {
            return false;
        }

        ItemStack newCostA = replaceItemStack(offer.getBaseCostA());
        ItemStack newCostB = replaceItemStack(offer.getCostB());
        ItemStack newResult = replaceItemStack(offer.getResult());

        if (hasChanges(offer, newCostA, newCostB, newResult)) {
            MerchantOffer modifiedOffer = new MerchantOffer(
                    newCostA,
                    newCostB,
                    newResult,
                    offer.getUses(),
                    offer.getMaxUses(),
                    offer.getXp(),
                    offer.getPriceMultiplier(),
                    offer.getDemand()
            );

            return super.add(modifiedOffer);
        }

        return super.add(offer);
    }

    @Unique
    private boolean isItemDisabled(ItemStack stack) {
        return !stack.isEmpty() && EasyModpack.ITEM_MANAGER.isDisabled(stack.getItem());
    }

    @Unique
    private ItemStack replaceItemStack(ItemStack original) {
        if (original.isEmpty()) return original;

        ResourceLocation originalId = Utils.getItemLocation(original.getItem());
        if (EasyModpack.ITEM_MANAGER.hasReplacement(originalId)) {
            Item replacement = Utils.getItem(EasyModpack.ITEM_MANAGER.getReplacement(originalId));

            if (replacement != null) {
                ItemStack newStack = new ItemStack(replacement, original.getCount());

                if (original.hasTag()) {
                    newStack.setTag(original.getTag().copy());
                }
                return newStack;
            }
        }
        return original.copy();
    }

    @Unique
    private boolean hasChanges(MerchantOffer original, ItemStack costA, ItemStack costB, ItemStack result) {
        return !original.getBaseCostA().equals(costA)
                || !original.getCostB().equals(costB)
                || !original.getResult().equals(result);
    }
}
