package dev.xnasuni.playervisibility.gui;

//import dev.xnasuni.playervisibility.config.ModConfig;
//import dev.xnasuni.playervisibility.types.MessageType;
//import dev.xnasuni.playervisibility.types.TextColor;
//import dev.xnasuni.playervisibility.util.ConfigUtil;
//
//import me.shedaniel.clothconfig2.api.ConfigBuilder;
//import me.shedaniel.clothconfig2.api.ConfigCategory;
//import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
//import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
//import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
//
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.text.Text;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;

public class ClothConfigScreen {
// Didn't make it to production to favor config in commands for better version compatibility, keeping it to archive.

//    public static Screen make(Screen parent) {
//        ConfigBuilder builder = ConfigBuilder.create()
//                .setParentScreen(parent)
//                .setTitle(new TranslatableText("text.config.player-visibility.title"))
//                .setSavingRunnable(ConfigUtil::save);
//
//        ConfigCategory config = builder.getOrCreateCategory(new TranslatableText("text.config.player-visibility.category.main"));
//        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
//
//        config.addEntry(entryBuilder.startStrList(new TranslatableText("text.config.player-visibility.option.playerWhitelist"), ModConfig.playerWhitelist)
//                .setDefaultValue(ModConfig.playerWhitelist)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.playerWhitelist.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.playerWhitelist = new ArrayList<>(newValue)) // java gc please save me
//                .build());
//
//        List<Enum<?>> enums = Arrays.asList(TextColor.values());
//        config.addEntry(entryBuilder.startDropdownMenu(new TranslatableText("text.config.player-visibility.option.mainColor"), DropdownMenuBuilder.TopCellElementBuilder.of(ModConfig.mainColor, (str) -> {
//                    Iterator<Enum<?>> iterator = enums.iterator();
//                    Enum<?> current;
//                    do {
//                        if (!iterator.hasNext()) {
//                            return null;
//                        }
//
//                        current = iterator.next();
//                    }  while (!EnumListEntry.DEFAULT_NAME_PROVIDER.apply(current).getString().equals(str));
//
//                    return current;
//                }, EnumListEntry.DEFAULT_NAME_PROVIDER),DropdownMenuBuilder.CellCreatorBuilder.of(EnumListEntry.DEFAULT_NAME_PROVIDER))
//                .setSelections(List.of(TextColor.values()))
//                .setDefaultValue(ModConfig.mainColor)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.mainColor.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.mainColor = (TextColor) newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startEnumSelector(new TranslatableText("text.config.player-visibility.option.messageType"), MessageType.class, ModConfig.messageType)
//                .setDefaultValue(ModConfig.messageType)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.messageType.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.messageType = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hideSelf"), ModConfig.hideSelf)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hideSelf.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hideSelf = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hidePlayers"), ModConfig.hidePlayers)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hidePlayers.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hidePlayers = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hideShadows"), ModConfig.hideShadows)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hideShadows.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hideShadows = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hideEntities"), ModConfig.hideEntities)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hideEntities.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hideEntities = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hideHitboxes"), ModConfig.hideHitboxes)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hideHitboxes.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hideHitboxes = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hideFire"), ModConfig.hideFire)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hideFire.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hideFire = newValue)
//                .build());
//
//        config.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.player-visibility.option.hideNametags"), ModConfig.hideNametags)
//                .setDefaultValue(false)
//                .setTooltip(new TranslatableText("text.config.player-visibility.option.hideNametags.tooltip"))
//                .setSaveConsumer(newValue -> ModConfig.hideNametags = newValue)
//                .build());
//
//        return builder.build();
//    }

}