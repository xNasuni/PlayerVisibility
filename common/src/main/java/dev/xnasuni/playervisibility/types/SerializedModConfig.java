package dev.xnasuni.playervisibility.types;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

// not a record for jre compatibility
public class SerializedModConfig {
    @SerializedName("filter_presets") public final Map<String, ArrayList<String>> filterPresets;
    @SerializedName("current_preset") public final String currentPreset;
    @SerializedName("main_color") public final TextColor mainColor;
    @SerializedName("message_type") public final MessageType messageType;
    @SerializedName("filter_type") public final FilterType filterType;
    @SerializedName("hide_players") public final boolean hidePlayers;
    @SerializedName("hide_self") public final boolean hideSelf;
    @SerializedName("hide_shadows") public final boolean hideShadows;
    @SerializedName("hide_entities") public final boolean hideEntities;
    @SerializedName("hide_hitboxes") public final boolean hideHitboxes;
    @SerializedName("hide_fire") public final boolean hideFire;
    @SerializedName("hide_nametags") public final boolean hideNametags;
    @SerializedName("comfort_zone") public final boolean comfortZone;
    @SerializedName("comfort_distance") public final float comfortDistance;
    @SerializedName("comfort_falloff") public final float comfortFalloff;

    public Map<String, ArrayList<String>> filterPresets() {
        return filterPresets;
    }
    public String currentPreset() { return currentPreset; }
    public TextColor mainColor() {
        return mainColor;
    }
    public MessageType messageType() {
        return messageType;
    }
    public FilterType filterType() {
        return filterType;
    }
    public boolean hidePlayers() {
        return hidePlayers;
    }
    public boolean hideSelf() {
        return hideSelf;
    }
    public boolean hideShadows() {
        return hideShadows;
    }
    public boolean hideEntities() {
        return hideEntities;
    }
    public boolean hideHitboxes() {
        return hideHitboxes;
    }
    public boolean hideFire() {
        return hideFire;
    }
    public boolean hideNametags() {
        return hideNametags;
    }
    public boolean comfortZone() {
        return comfortZone;
    }
    public float comfortDistance() {
        return comfortDistance;
    }
    public float comfortFalloff() { return comfortFalloff; }

    public SerializedModConfig(Map<String, ArrayList<String>> filterPresets, String currentPreset, TextColor mainColor, MessageType messageType, FilterType filterType, boolean hidePlayers, boolean hideSelf, boolean hideShadows, boolean hideEntities, boolean hideHitboxes, boolean hideFire, boolean hideNametags, boolean comfortZone, float comfortDistance, float comfortFalloff) {
        this.filterPresets = filterPresets;
        this.currentPreset = currentPreset;
        this.mainColor = mainColor;
        this.messageType = messageType;
        this.filterType = filterType;
        this.hidePlayers = hidePlayers;
        this.hideSelf = hideSelf;
        this.hideShadows = hideShadows;
        this.hideEntities = hideEntities;
        this.hideHitboxes = hideHitboxes;
        this.hideFire = hideFire;
        this.hideNametags = hideNametags;
        this.comfortZone = comfortZone;
        this.comfortDistance = comfortDistance;
        this.comfortFalloff = comfortFalloff;
    }
}