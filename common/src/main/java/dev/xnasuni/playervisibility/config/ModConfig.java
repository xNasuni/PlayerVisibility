package dev.xnasuni.playervisibility.config;

import dev.xnasuni.playervisibility.types.FilterType;
import dev.xnasuni.playervisibility.types.MessageType;
import dev.xnasuni.playervisibility.types.SerializedModConfig;
import dev.xnasuni.playervisibility.types.TextColor;
import dev.xnasuni.playervisibility.util.ConfigUtil;

import java.util.ArrayList;
import java.util.Map;

public class ModConfig {
    public static Map<String, ArrayList<String>> filterPresets = ConfigUtil.defaultFilterPresets;
    public static String currentPreset = ConfigUtil.defaultCurrentPreset;
    public static TextColor mainColor = ConfigUtil.defaultMainColor;
    public static MessageType messageType = ConfigUtil.defaultMessageType;
    public static FilterType filterType = ConfigUtil.defaultFilterType;
    public static boolean hidePlayers = ConfigUtil.defaultHidePlayers;
    public static boolean hideSelf = ConfigUtil.defaultHideSelf;
    public static boolean hideShadows = ConfigUtil.defaultHideShadows;
    public static boolean hideEntities = ConfigUtil.defaultHideEntities;
    public static boolean hideHitboxes = ConfigUtil.defaultHideHitboxes;
    public static boolean hideFire = ConfigUtil.defaultHideFire;
    public static boolean hideNametags = ConfigUtil.defaultHideNametags;
    public static boolean comfortZone = ConfigUtil.defaultComfortZone;
    public static float comfortDistance = ConfigUtil.defaultComfortDistance;
    public static float comfortFalloff = ConfigUtil.defaultComfortFalloff;

    public static ArrayList<String> getFilter() {
        return filterPresets.computeIfAbsent(currentPreset, k -> new ArrayList<>());
    }
    public static SerializedModConfig serialize() {
        return new SerializedModConfig(filterPresets, currentPreset, mainColor, messageType, filterType, hidePlayers, hideSelf, hideShadows, hideEntities, hideHitboxes, hideFire, hideNametags, comfortZone, comfortDistance, comfortFalloff);
    }
}