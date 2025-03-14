package com.chaotic_loom.easy_modpack.mixin;

import com.chaotic_loom.easy_modpack.modules.Utils;
import com.chaotic_loom.easy_modpack.modules.blocks.BlockManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/*
Replaces blocks on structures and features.
 */
@Mixin(WorldGenRegion.class)
public class WorldGenRegionMixin {
    @ModifyVariable(method = "setBlock", at = @At("HEAD"), argsOnly = true)
    private BlockState modifyBlockState(BlockState originalState) {
        ResourceLocation originalId = Utils.getBlockLocation(originalState.getBlock());

        if (BlockManager.isDisabled(originalId)) {
            BlockState replacementState = Blocks.AIR.defaultBlockState();
            copyBlockProperties(originalState, replacementState);

            return replacementState;
        }

        if (BlockManager.hasReplacement(originalId)) {
            ResourceLocation replacementId = BlockManager.getReplacement(originalId);
            Block replacementBlock = Utils.getBlock(replacementId);

            if (replacementBlock != null) {
                BlockState replacementState = replacementBlock.defaultBlockState();
                copyBlockProperties(originalState, replacementState);

                return replacementState;
            }
        }

        return originalState;
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
