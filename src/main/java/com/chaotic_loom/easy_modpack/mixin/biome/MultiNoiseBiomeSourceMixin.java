package com.chaotic_loom.easy_modpack.mixin.biome;

import com.chaotic_loom.easy_modpack.EasyModpack;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
Replaces biomes
 */
@Mixin(value = MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Unique
    private Climate.ParameterList<Holder<Biome>> filteredParameters;

    @Inject(method = "parameters", at = @At("TAIL"), cancellable = true)
    private void parameters(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        Climate.ParameterList<Holder<Biome>> original = cir.getReturnValue();

        // Vanilla handling with caching
        if (this.filteredParameters == null) {
            this.filteredParameters = EasyModpack.BIOME_MANAGER.replaceBiomes(original);
        }
        cir.setReturnValue(this.filteredParameters);
    }
}
