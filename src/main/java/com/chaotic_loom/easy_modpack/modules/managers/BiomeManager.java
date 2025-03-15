package com.chaotic_loom.easy_modpack.modules.managers;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.*;

public class BiomeManager extends BaseManager {
    public void register() {
        reload("biomes");
    }

    public Climate.ParameterList<Holder<Biome>> replaceBiomes(Climate.ParameterList<Holder<Biome>> parameterList) {
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> originalValues = parameterList.values();
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> newValues = new ArrayList<>();

        ResourceLocation plainsBiomeId = new ResourceLocation("minecraft", "plains");

        for (Pair<Climate.ParameterPoint, Holder<Biome>> pair : originalValues) {
            Climate.ParameterPoint parameterPoint = pair.getFirst();
            Holder<Biome> biomeHolder = pair.getSecond();

            Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
            if (biomeKey.isPresent()) {
                ResourceLocation biomeId = biomeKey.get().location();

                if (this.isDisabled(biomeId)) {
                    Holder<Biome> replacementBiome = Utils.findBiomeByResourceLocation(EasyModpack.getCurrentServer(), plainsBiomeId);
                    newValues.add(Pair.of(parameterPoint, replacementBiome != null ? replacementBiome : biomeHolder));
                } else if (this.hasReplacement(biomeId)) {
                    ResourceLocation replacementId = this.getReplacement(biomeId);
                    Holder<Biome> replacementBiome = Utils.findBiomeByResourceLocation(EasyModpack.getCurrentServer(), replacementId);
                    newValues.add(Pair.of(parameterPoint, replacementBiome != null ? replacementBiome : biomeHolder));
                } else {
                    newValues.add(pair);
                }
            } else {
                newValues.add(pair);
            }
        }

        return new Climate.ParameterList<>(newValues);
    }
}
