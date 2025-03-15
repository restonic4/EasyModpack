package com.chaotic_loom.easy_modpack.compatibility.terrablender;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.ConsoleBox;

public class TerrablenderCompatibility {
    public static void warn() {
        EasyModpack.LOGGER.error(ConsoleBox.createBoxForMinecraftLogger("Easy Modpack Compatibility Error", "Easy Modpack's biome replacer is incompatible with TerraBlender\ndue to his world generation system rework, check Easy Modpack's page or github."));
    }
}
