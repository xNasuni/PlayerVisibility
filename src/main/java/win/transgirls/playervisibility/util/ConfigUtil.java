package win.transgirls.playervisibility.util;

import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.FilterType;
import win.transgirls.playervisibility.types.MessageType;
import win.transgirls.playervisibility.types.SerializedModConfig;
import win.transgirls.playervisibility.types.TextColor;
import static win.transgirls.playervisibility.PlayerVisibilityClient.LOGGER;

import com.google.gson.*;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigUtil {
    public static final Map<String, ArrayList<String>> defaultFilterPresets = new HashMap<>();
    public static final String defaultCurrentPreset = "main";
    public static final TextColor defaultMainColor = TextColor.BLUE;
    public static final MessageType defaultMessageType = MessageType.ACTION_BAR;
    public static final FilterType defaultFilterType = FilterType.WHITELIST;
    public static final boolean defaultHidePlayers = true;
    public static final boolean defaultHideSelf = false;
    public static final boolean defaultHideShadows = true;
    public static final boolean defaultHideEntities = false;
    public static final boolean defaultHideHitboxes = true;
    public static final boolean defaultHideFire = true;
    public static final boolean defaultHideNametags = false;
    public static final boolean defaultComfortZone = false;
    public static final float defaultComfortDistance = 3.0f;
    public static final float defaultComfortFalloff = 1.0f;

    public static Path configDirectory;
    private static Path modernConfigFilePath;
    private static Path v011orLessLegacyFilePath;
    private static Gson configGson;

    public static <T> T fallbackField(String field, JsonObject root, T fallback) {
        try {
            if (root.get(field).isJsonPrimitive()) {
                JsonPrimitive primitive = root.getAsJsonPrimitive(field);
                if (fallback instanceof String) {
                    String value = primitive.getAsString();
                    return (T) String.valueOf(value);
                }
                if (fallback instanceof Boolean) {
                    boolean value = primitive.getAsBoolean();
                    return (T) Boolean.valueOf(value);
                }
                if (fallback instanceof Integer) {
                    int value = primitive.getAsInt();
                    return (T) Integer.valueOf(value);
                }
                if (fallback instanceof Float) {
                    float value = primitive.getAsFloat();
                    return (T) Float.valueOf(value);
                }
                if (fallback instanceof Enum) {
                    Enum<?> value = Enum.valueOf(((Enum<?>) fallback).getDeclaringClass(), primitive.getAsString());
                    return (T) value;
                }
            }

            if (root.get(field).isJsonObject()) {
                JsonObject object = root.getAsJsonObject(field);
                if (fallback instanceof HashMap) {
                    Map<String, ArrayList<String>> value = ArrayListUtil.getAsObjectStringMap(object);
                    return (T) value;
                }
            }

            LOGGER.warn("Config option {} missing or damaged! Value fallback (default) set to {}", field, fallback);
            return fallback;
        } catch (Throwable e) {
            LOGGER.warn("Config option {} missing or damaged! Value fallback (default) set to {}", field, fallback);
        }
        return fallback;
    }
    public static ArrayList<String> getLegacyWhitelist() {
        ArrayList<String> legacyWhitelist = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(v011orLessLegacyFilePath);
            legacyWhitelist.addAll(allLines);
        } catch (IOException e) {
            LOGGER.error("Error while reading legacy whitelist file 'whitelisted-players.txt', not migrating.", e);
            return null;
        }
        return legacyWhitelist;
    }
    public static String filesReadString(Path path) throws IOException {
        return ArrayListUtil.joinSeperator(Files.readAllLines(path), "\n");
    }

    public static void init() {
        configGson = new GsonBuilder().serializeNulls().setPrettyPrinting().setLenient().create();
        configDirectory = FabricLoader.getInstance().getConfigDir().resolve("player-visibility");
        modernConfigFilePath = configDirectory.resolve("pv-config.json");
        v011orLessLegacyFilePath = configDirectory.resolve("whitelisted-players.txt");

        ArrayList<String> legacyWhitelist = null;

        try {
            Files.createDirectories(configDirectory);
        } catch (IOException e) {
            LOGGER.error("Could not create config directory, issues might arise further.", e);
        }

        // if an old whitelist file |whitelisted-players.txt| exists, but no new config |pv-config.json| exists, we will migrate the whitelist to the new config.
        if (Files.exists(v011orLessLegacyFilePath) && !Files.exists(modernConfigFilePath)) {
            legacyWhitelist = getLegacyWhitelist();
            if (legacyWhitelist != null) {
                LOGGER.info("Old whitelist file found! Migrating whitelist to new config format. Legacy data: {}", legacyWhitelist.toString());
            } else {
                LOGGER.info("Old whitelist file found, however, it couldn't be recovered :(, not migrating.");
            }
        }

        if (!Files.exists(modernConfigFilePath)) {
            try {
                Files.createFile(configDirectory.resolve("pv-config.json"));
                LOGGER.info("No config file found! Creating a new one.");
            } catch (IOException e) {
                LOGGER.error("Failed to create config file `pv-config.json`, mod will try again when saving.", e);
                return;
            }
        }

        load(legacyWhitelist);
    }
    public static void reset() {
        ModConfig.filterPresets = defaultFilterPresets;
        ModConfig.currentPreset = defaultCurrentPreset;
        ModConfig.mainColor = defaultMainColor;
        ModConfig.messageType = defaultMessageType;
        ModConfig.filterType = defaultFilterType;
        ModConfig.hidePlayers = defaultHidePlayers;
        ModConfig.hideSelf = defaultHideSelf;
        ModConfig.hideShadows = defaultHideShadows;
        ModConfig.hideEntities = defaultHideEntities;
        ModConfig.hideHitboxes = defaultHideHitboxes;
        ModConfig.hideFire = defaultHideFire;
        ModConfig.hideNametags = defaultHideNametags;
        ModConfig.comfortZone = defaultComfortZone;
        ModConfig.comfortDistance = defaultComfortDistance;
        ModConfig.comfortFalloff = defaultComfortFalloff;
    }
    public static void load(ArrayList<String> legacyWhitelist) {
        File configFile = modernConfigFilePath.toFile();

        reset();
        if (legacyWhitelist != null) {
            ModConfig.filterPresets.put(defaultCurrentPreset, legacyWhitelist);
        }

        if (configFile.canRead()) {
            String jsonConfig;
            try {
                jsonConfig = filesReadString(configFile.toPath());
            } catch (IOException e) {
                LOGGER.error("Failed to read config file `pv-config.json`, defaulting config.", e);
                return;
            }
            try {
                if (jsonConfig.trim().isEmpty()) {
                    LOGGER.warn("The config file is empty, and is most likely because of the config being initialized recently.");
                    return;
                }

                JsonObject object = configGson.fromJson(jsonConfig, JsonElement.class).getAsJsonObject();
                try {
                    // if we're migrating old player whitelist data, we shouldn't override |filter_presets| with an empty list.
                    ModConfig.filterPresets = legacyWhitelist == null ? fallbackField("filter_presets", object, defaultFilterPresets) : ModConfig.filterPresets;
                    ModConfig.currentPreset = fallbackField("current_preset", object, defaultCurrentPreset);
                    ModConfig.mainColor = fallbackField("main_color", object, defaultMainColor);
                    ModConfig.messageType = fallbackField("message_type", object, defaultMessageType);
                    ModConfig.filterType = fallbackField("filter_type", object, defaultFilterType);
                    ModConfig.hidePlayers = fallbackField("hide_players", object, defaultHidePlayers);
                    ModConfig.hideSelf = fallbackField("hide_self", object, defaultHideSelf);
                    ModConfig.hideShadows = fallbackField("hide_shadows", object, defaultHideShadows);
                    ModConfig.hideEntities = fallbackField("hide_entities", object, defaultHideEntities);
                    ModConfig.hideHitboxes = fallbackField("hide_hitboxes", object, defaultHideHitboxes);
                    ModConfig.hideFire = fallbackField("hide_fire", object, defaultHideFire);
                    ModConfig.hideNametags = fallbackField("hide_nametags", object, defaultHideNametags);
                    ModConfig.comfortZone = fallbackField("comfort_zone", object, defaultComfortZone);
                    ModConfig.comfortDistance = fallbackField("comfort_distance", object, defaultComfortDistance);
                    ModConfig.comfortFalloff = fallbackField("comfort_falloff", object, defaultComfortFalloff);
                } catch (Throwable e) {
                    LOGGER.error("Failed to load config file `pv-config.json`, defaulting config.", e);
                }
            } catch (Throwable e) {
                LOGGER.error("Failed to parse config file `pv-config.json`, defaulting config.", e);
            }
        } else {
            LOGGER.warn("Cannot read file 'pv-config.json', defaulting config.");
        }
    }
    public static void save() {
        SerializedModConfig serialized = ModConfig.serialize();
        String json = configGson.toJson(serialized);
        if (!Files.exists(modernConfigFilePath)) {
            try {
                Files.createFile(configDirectory.resolve("pv-config.json"));
                LOGGER.info("No config file found! Creating a new one.");
            } catch (Throwable e) {
                LOGGER.error("Failed to create config file `pv-config.json`, mod wont be able to save.", e);
                return;
            }
        }
        try {
            Files.write(modernConfigFilePath, json.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Throwable e) {
            LOGGER.error("Error while saving mod config file 'pv-config.json'", e);
        }
        LOGGER.info("Player Visibility has saved {} bytes to pv-config.json successfully!", json.length());
    }
}