package com.chaotic_loom.easy_modpack.mixin.biome;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Replaces biomes
 */
@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Unique
    private Climate.ParameterList<Holder<Biome>> filteredParameters;

    @Inject(method = "parameters", at = @At("TAIL"), cancellable = true)
    private void parameters(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        if (this.filteredParameters == null) {
            this.filteredParameters = replaceBiomes(cir.getReturnValue());
        }

        cir.setReturnValue(this.filteredParameters);
    }

    @Unique
    private static Climate.ParameterList<Holder<Biome>> replaceBiomes(Climate.ParameterList<Holder<Biome>> parameterList) {
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> originalValues = parameterList.values();
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> newValues = new ArrayList<>();

        // Default biome to replace disabled biomes (plains)
        ResourceLocation plainsBiomeId = new ResourceLocation("minecraft", "plains");

        for (Pair<Climate.ParameterPoint, Holder<Biome>> pair : originalValues) {
            Climate.ParameterPoint parameterPoint = pair.getFirst();
            Holder<Biome> biomeHolder = pair.getSecond();

            Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
            if (biomeKey.isPresent()) {
                ResourceLocation biomeId = biomeKey.get().location();

                if (isBiomeDisabled(biomeId)) {
                    // If the biome is disabled, replace it with plains
                    Holder<Biome> replacementBiome = findBiomeByResourceLocation(plainsBiomeId);
                    if (replacementBiome != null) {
                        newValues.add(Pair.of(parameterPoint, replacementBiome));
                    } else {
                        // If plains cannot be found, keep the original biome
                        newValues.add(pair);
                    }
                } else if (shouldBiomeBeReplaced(biomeId)) {
                    // If the biome should be replaced, find its replacement
                    ResourceLocation replacementBiomeId = getBiomeReplacement(biomeId);
                    Holder<Biome> replacementBiome = findBiomeByResourceLocation(replacementBiomeId);

                    if (replacementBiome != null) {
                        newValues.add(Pair.of(parameterPoint, replacementBiome));
                    } else {
                        // If the replacement biome is not found, keep the original
                        newValues.add(pair);
                    }
                } else {
                    // If no changes are needed, keep the original biome
                    newValues.add(pair);
                }
            } else {
                // If the biome key cannot be obtained, keep the original pair
                newValues.add(pair);
            }
        }

        return new Climate.ParameterList<>(newValues);
    }

    @Unique
    private static Holder<Biome> findBiomeByResourceLocation(ResourceLocation biomeId) {
        MinecraftServer server = getServer();

        if (server != null) {
            // Retrieve the biome registry
            return server.registryAccess().registryOrThrow(Registries.BIOME).getHolder(
                    ResourceKey.create(Registries.BIOME, biomeId)
            ).orElse(null);
        }
        return null;
    }

    @Unique
    private static MinecraftServer getServer() {
        return EasyModpack.getCurrentServer();
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
