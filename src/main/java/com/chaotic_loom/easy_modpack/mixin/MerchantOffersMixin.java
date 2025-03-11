package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Mixin(MerchantOffers.class)
public class MerchantOffersMixin extends ArrayList<MerchantOffer> {
    @Override
    public boolean add(MerchantOffer offer) {
        if (ItemManager.isDisabled(offer.getResult().getItem())) {
            return false;
        }

        if (ItemManager.isDisabled(offer.getBaseCostA().getItem())) {
            return false;
        }

        if (ItemManager.isDisabled(offer.getCostB().getItem())) {
            return false;
        }

        return super.add(offer);
    }
}
