package com.chaotic_loom.easy_modpack.modules;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.util.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ConfigManager {
    private static final Path CONFIG_FOLDER = PlatformHelper.getConfigDir().resolve(EasyModpack.MOD_ID);
    private static final String CONFIG_FILE_EXTENSION = "json";
    private static final String DEFAULT_CONFIG_DIR = "/config_templates";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, JsonObject> CONFIG_CACHE = new ConcurrentHashMap<>();

    public static String getConfigFileName(String configName) {
        return configName + "." + CONFIG_FILE_EXTENSION;
    }

    public static String getDefaultConfigFileName(String configName) {
        return DEFAULT_CONFIG_DIR + "/" + configName + "." + CONFIG_FILE_EXTENSION;
    }

    /**
     * Initializes the configuration by copying the default template from the JAR if it does not exist.
     * It then loads and merges the configuration with the default template.
     *
     * @param configName The base name of the configuration (e.g., "items").
     */
    public static void initConfig(String configName) {
        try {
            if (!Files.exists(CONFIG_FOLDER)) {
                Files.createDirectories(CONFIG_FOLDER);
            }
            Path configFile = CONFIG_FOLDER.resolve(getConfigFileName(configName));
            if (!Files.exists(configFile)) {
                try (InputStream in = ConfigManager.class.getResourceAsStream(getDefaultConfigFileName(configName))) {
                    if (in == null) {
                        throw new IOException("Default resource not found: " + getDefaultConfigFileName(configName));
                    }
                    Files.copy(in, configFile);
                }
            }

            JsonObject config = loadAndMergeConfig(configFile, configName);
            CONFIG_CACHE.put(configName, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a configuration file using Gson.
     *
     * @param configFile The path to the configuration file.
     * @return A JsonObject containing the configuration, or an empty object in case of an error.
     */
    private static JsonObject loadConfig(Path configFile) {
        try (Reader reader = new FileReader(configFile.toFile())) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    /**
     * Loads the user configuration and merges it with the default template.
     * If any keys from the template are missing in the user's configuration,
     * they are added and the file is updated.
     *
     * @param configFile The path to the user's configuration file.
     * @param configName The base name of the configuration.
     * @return A JsonObject containing the merged configuration.
     */
    private static JsonObject loadAndMergeConfig(Path configFile, String configName) {
        JsonObject userConfig = loadConfig(configFile);

        try (InputStream in = ConfigManager.class.getResourceAsStream(getDefaultConfigFileName(configName))) {
            if (in != null) {
                Reader reader = new InputStreamReader(in);
                JsonObject defaultConfig = GSON.fromJson(reader, JsonObject.class);
                boolean modified = false;
                for (String key : defaultConfig.keySet()) {
                    if (!userConfig.has(key)) {
                        userConfig.add(key, defaultConfig.get(key));
                        modified = true;
                    }
                }
                if (modified) {
                    saveConfig(configName, userConfig);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userConfig;
    }

    /**
     * Saves a configuration JsonObject to the corresponding file.
     *
     * @param configName The base name of the configuration.
     * @param config     The JsonObject containing the configuration data.
     */
    private static void saveConfig(String configName, JsonObject config) {
        Path configFile = CONFIG_FOLDER.resolve(getConfigFileName(configName));
        try (Writer writer = new FileWriter(configFile.toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an array of strings for the specified key from the cached configuration.
     *
     * @param configName The base name of the configuration (e.g., "items").
     * @param key        The key to retrieve the values from (e.g., "disabled").
     * @return A list of strings associated with the key, or an empty list if an error occurs.
     */
    public static List<String> getConfigArray(String configName, String key) {
        JsonObject userConfig = CONFIG_CACHE.get(configName);
        List<String> result = new ArrayList<>();
        if (userConfig != null && userConfig.has(key) && userConfig.get(key).isJsonArray()) {
            JsonArray array = userConfig.getAsJsonArray(key);
            array.forEach(element -> result.add(element.getAsString()));
        }
        return result;
    }

    /**
     * Retrieves an array of string pairs from the specified key in the cached configuration.
     *
     * @param configName The base name of the configuration.
     * @param key        The key to retrieve the values from.
     * @return A list of pairs (key-value strings) associated with the specified key.
     */
    public static List<Pair<String, String>> getConfigPairArray(String configName, String key) {
        JsonObject userConfig = CONFIG_CACHE.get(configName);
        List<Pair<String, String>> result = new ArrayList<>();
        if (userConfig != null && userConfig.has(key) && userConfig.get(key).isJsonArray()) {
            JsonArray array = userConfig.getAsJsonArray(key);

            array.forEach(element -> {
                JsonArray pair = element.getAsJsonArray();

                result.add(new Pair<>(pair.get(0).getAsString(), pair.get(1).getAsString()));
            });
        }
        return result;
    }
}