package com.chaotic_loom.easy_modpack.modules;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

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
     * Inicializa la configuración copiando el archivo por defecto desde el jar si no existe,
     * y carga en memoria la configuración ya fusionada con la plantilla por defecto.
     *
     * @param configName nombre base de la configuración (por ejemplo, "items")
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
                        throw new IOException("No se encontró el recurso por defecto: " + getDefaultConfigFileName(configName));
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
     * Carga la configuración desde un archivo utilizando Gson.
     *
     * @param configFile Path al archivo de configuración.
     * @return JsonObject con la configuración o un objeto vacío en caso de error.
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
     * Carga la configuración del usuario y la fusiona con la plantilla por defecto.
     * Si alguna clave de la plantilla no existe en la configuración del usuario,
     * se agrega y se actualiza el archivo.
     *
     * @param configFile Path al archivo de configuración del usuario.
     * @param configName Nombre base de la configuración.
     * @return JsonObject resultante de la fusión.
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
     * Guarda el JsonObject de configuración en el archivo correspondiente.
     *
     * @param configName Nombre base de la configuración.
     * @param config     JsonObject de configuración.
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
     * Obtiene un array de strings correspondiente a la key especificada.
     * Se consulta la configuración cargada en memoria.
     *
     * @param configName nombre base de la configuración (por ejemplo, "items")
     * @param key        la key a leer (por ejemplo, "disabled")
     * @return lista de strings asociada a la key, o una lista vacía en caso de error.
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
}