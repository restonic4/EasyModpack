package com.chaotic_loom.easy_modpack.modules;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PlatformHelper {
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
