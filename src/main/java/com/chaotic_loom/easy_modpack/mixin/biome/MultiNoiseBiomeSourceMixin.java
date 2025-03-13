package com.chaotic_loom.easy_modpack.mixin.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetNoiseBiome(
            int x, int y, int z,
            Climate.Sampler sampler,
            CallbackInfoReturnable<Holder<Biome>> cir
    ) {
        Holder<Biome> originalBiome = cir.getReturnValue();
        originalBiome.unwrapKey().ifPresent(biomeKey -> {
            ResourceLocation biomeId = biomeKey.location();

            if (isBiomeDisabled(biomeId)) {
                replaceBiome(cir, "minecraft:plains");
            } else if (shouldBiomeBeReplaced(biomeId)) {
                ResourceLocation replacementId = getBiomeReplacement(biomeId);
                replaceBiome(cir, replacementId.toString());
            }
        });
    }

    @Unique
    private void replaceBiome(
            CallbackInfoReturnable<Holder<Biome>> cir,
            String replacementId
    ) {
        // Usar el registro dinámico (compatible con mods)
        ResourceLocation replacementLoc = new ResourceLocation(replacementId);
        ResourceKey<Biome> replacementKey = ResourceKey.create(
                Registries.BIOME,
                replacementLoc
        );

        // Obtener el Holder del bioma de reemplazo desde el registro activo
        MultiNoiseBiomeSource self = (MultiNoiseBiomeSource) (Object) this;
        self.possibleBiomes().stream()
                .filter(holder -> holder.is(replacementKey))
                .findFirst()
                .ifPresent(cir::setReturnValue); // Reemplazar si existe
    }

    @Unique
    private static boolean isBiomeDisabled(ResourceLocation biome) {
        return com.chaotic_loom.easy_modpack.modules.biomes.BiomeManager.isDisabled(biome);
    }

    @Unique
    private static boolean shouldBiomeBeReplaced(ResourceLocation biome) {
        return com.chaotic_loom.easy_modpack.modules.biomes.BiomeManager.hasReplacement(biome);
    }

    @Unique
    private static ResourceLocation getBiomeReplacement(ResourceLocation biome) {
        return com.chaotic_loom.easy_modpack.modules.biomes.BiomeManager.getReplacement(biome);
    }
}
