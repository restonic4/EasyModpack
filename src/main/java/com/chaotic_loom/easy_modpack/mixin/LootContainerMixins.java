package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.items.ItemManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class LootContainerMixins {
    @Mixin(RandomizableContainerBlockEntity.class)
    public abstract static class RandomizableContainerBlockEntityMixin {
        @Inject(
                method = "unpackLootTable",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V",
                        shift = At.Shift.AFTER
                )
        )
        private void onUnpackLootTable(Player player, CallbackInfo ci) {
            ItemManager.removeDisabledItems((Container) (Object) this);
        }
    }

    @Mixin(ContainerEntity.class)
    public interface ContainerEntityMixin {
        @Inject(
                method = "unpackChestVehicleLootTable",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V",
                        shift = At.Shift.AFTER
                )
        )
        private void unpackChestVehicleLootTable(Player player, CallbackInfo ci) {
            ItemManager.removeDisabledItems((Container) (Object) this);
        }
    }
}
