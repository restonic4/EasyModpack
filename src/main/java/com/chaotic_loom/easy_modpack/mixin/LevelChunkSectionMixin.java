package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
Replaces blocks on terrain generation and other cases

TODO: Sometimes blocks gets de-sync, for example, a tree growing (Replace log with other block)
TODO: Maybe WorldGenRegionMixin is not needed.
 */
@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin {

    @Shadow public abstract BlockState setBlockState(int i, int j, int k, BlockState blockState, boolean bl);

    @Inject(method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("HEAD"), cancellable = true)
    private void replaceState(int x, int y, int z, BlockState state, boolean flag, CallbackInfoReturnable<BlockState> cir) {
        ResourceLocation blockId = Utils.getBlockLocation(state.getBlock());

        if (EasyModpack.BLOCK_MANAGER.isDisabled(blockId)) {
            BlockState replacementState = Blocks.AIR.defaultBlockState();
            copyBlockProperties(state, replacementState);

            cir.setReturnValue(setBlockState(x, y, z, replacementState, flag));
        }

        if (EasyModpack.BLOCK_MANAGER.hasReplacement(blockId)) {
            ResourceLocation replacementId = EasyModpack.BLOCK_MANAGER.getReplacement(blockId);
            Block replacementBlock = Utils.getBlock(replacementId);

            if (replacementBlock != null) {
                BlockState replacementState = replacementBlock.defaultBlockState();
                copyBlockProperties(state, replacementState);

                cir.setReturnValue(setBlockState(x, y, z, replacementState, flag));
            }
        }
    }

    @Unique
    private void copyBlockProperties(BlockState source, BlockState target) {
        for (Property<?> prop : source.getProperties()) {
            if (target.hasProperty(prop)) {
                target = copyProperty(source, target, prop);
            }
        }
    }

    @Unique
    private <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.setValue(property, source.getValue(property));
    }
}
