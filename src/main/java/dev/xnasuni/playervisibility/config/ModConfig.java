package dev.xnasuni.playervisibility.config;

import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.types.TextColor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = PlayerVisibility.ModID)
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, me.shedaniel.autoconfig.serializer.JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        INSTANCE.PlayerWhitelist = PlayerVisibility.GetWhitelistedPlayers();
    }

    @ConfigEntry.Gui.PrefixText
    public String[] PlayerWhitelist = new String[]{};

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public TextColor MainColor = TextColor.YELLOW;
}

