package win.transgirls.playervisibility.commands;

import win.transgirls.crossfabric.multiversion.VersionedText;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.PlayerVisibilityClient;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.FilterType;
import win.transgirls.playervisibility.types.MessageType;
import win.transgirls.playervisibility.types.TextColor;
import win.transgirls.playervisibility.util.ArrayListUtil;
import win.transgirls.playervisibility.util.ConfigUtil;
import win.transgirls.playervisibility.util.HighlightUtil;
import static win.transgirls.playervisibility.PlayerVisibility.minecraftClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

import java.util.Objects;

import net.minecraft.client.network.PlayerListEntry;

public class VisibilityCommand {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void register(CommandDispatcher<Object> dispatcher) {
        String[] booleans = {
                "true",
                "false"
        };

        SuggestionProvider<Object> usernameSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (String Suggestion: ModConfig.getFilter()) {
                if (Suggestion.toLowerCase().startsWith(input)) {
                    builder.suggest(Suggestion);
                }
            }

            return builder.buildFuture();
        };
        SuggestionProvider<Object> ingamePlayerSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            if (minecraftClient.getNetworkHandler() != null) {
                for (PlayerListEntry player: minecraftClient.getNetworkHandler().getPlayerList()) {
                    String suggestion = player.getProfile().name();
                    if (suggestion.toLowerCase().startsWith(input)) {
                        builder.suggest(suggestion);
                    }
                }
            }

            return builder.buildFuture();
        };
        SuggestionProvider<Object> colorSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (TextColor color: TextColor.values()) {
                String suggestion = color.name();
                if (suggestion.toLowerCase().startsWith(input)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
        SuggestionProvider<Object> messageTypeSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (MessageType type: MessageType.values()) {
                String suggestion = type.name();
                if (suggestion.toLowerCase().startsWith(input)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
        SuggestionProvider<Object> filterTypeSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (FilterType type: FilterType.values()) {
                String suggestion = type.name();
                if (suggestion.toLowerCase().startsWith(input)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
        SuggestionProvider<Object> booleanSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (String suggestion: booleans) {
                if (suggestion.toLowerCase().startsWith(input)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
        SuggestionProvider<Object> presetLabelSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (String label: ModConfig.filterPresets.keySet()) {
                if (label.toLowerCase().startsWith(input)) {
                    builder.suggest(label);
                }
            }

            return builder.buildFuture();
        };

        dispatcher.register(literal("visibility")
                .then(literal("whitelist")
                        .then(literal("add")
                                .then(argument("username", string())
                                        .suggests(ingamePlayerSuggestionProvider)
                                        .executes(ctx -> {
                                            String username = getString(ctx, "username");
                                            String casedName = ArrayListUtil.getCase(ModConfig.getFilter(), username);

                                            if (ArrayListUtil.containsLowercase(ModConfig.getFilter(), username)) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.already-whitelisted", casedName));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.getFilter().add(username);
                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.add-whitelist", ModConfig.mainColor.getChar(), casedName, ModConfig.mainColor.getChar()));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("username", string())
                                        .suggests(usernameSuggestionProvider)
                                        .executes(ctx -> {
                                            String username = getString(ctx, "username");
                                            String casedName = ArrayListUtil.getCase(ModConfig.getFilter(), username);
                                            
                                            if (!ArrayListUtil.containsLowercase(ModConfig.getFilter(), username)) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.not-whitelisted", casedName));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.getFilter().remove(casedName);
                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.remove-whitelist", ModConfig.mainColor.getChar(), casedName, ModConfig.mainColor.getChar()));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("clear")
                                .executes(ctx -> {
                                    int sizeBeforeClear = ModConfig.getFilter().size();
                                    String sizeCountName = (sizeBeforeClear == 1 ? VersionedText.translatable("text.player-visibility.message.singular") : VersionedText.translatable("text.player-visibility.message.plural")).getString();

                                    if (sizeBeforeClear == 0) {
                                        PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.already-cleared"));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    ModConfig.getFilter().clear();
                                    PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.clear-whitelist", ModConfig.mainColor.getChar(), ModConfig.mainColor.getChar(), sizeBeforeClear, VersionedText.translatable("text.player-visibility.message.entry-prefix"), sizeCountName));

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(literal("view")
                                .executes(ctx -> {
                                    if (ModConfig.getFilter().isEmpty()) {
                                        PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.empty-whitelist"));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    StringBuilder whitelistString = new StringBuilder();
                                    whitelistString.append(String.format("'%s': ", ModConfig.currentPreset));

                                    int idx = 0;
                                    for (String whitelistedUsername: ModConfig.getFilter()) {
                                        idx++;
                                        if (idx <= 1) {
                                            whitelistString.append(String.format("§%c'%s'§f", ModConfig.mainColor.getChar(), whitelistedUsername));
                                        } else {
                                            whitelistString.append(String.format(", §%c'%s'§f", ModConfig.mainColor.getChar(), whitelistedUsername));
                                        }
                                    }

                                    whitelistString.append(String.format(" §f(§%c%s %s%s§f)", ModConfig.mainColor.getChar(), ModConfig.getFilter().size(), VersionedText.translatable("text.player-visibility.message.entry-prefix").getString(), ModConfig.getFilter().size() == 1 ? VersionedText.translatable("text.player-visibility.message.singular").getString() : VersionedText.translatable("text.player-visibility.message.plural").getString()));

                                    PlayerVisibility.sendMessage(whitelistString.toString());
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("config")
                        .then(literal("presetload")
                                .then(argument("label", string())
                                        .suggests(presetLabelSuggestionProvider)
                                        .executes(ctx -> {
                                            String labelInput = getString(ctx, "label");

                                            if (!ModConfig.filterPresets.containsKey(labelInput)) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.no-load-preset", labelInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.currentPreset = labelInput;
                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.load-preset", ModConfig.mainColor.getChar(), labelInput, ModConfig.mainColor.getChar(), ModConfig.getFilter().size(), VersionedText.translatable("text.player-visibility.message.entry-prefix").getString(), ModConfig.getFilter().size() == 1 ? VersionedText.translatable("text.player-visibility.message.singular").getString() : VersionedText.translatable("text.player-visibility.message.plural").getString()));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("presetsave")
                                .then(argument("label", string())
                                        .suggests(presetLabelSuggestionProvider)
                                        .executes(ctx -> {
                                            String labelInput = getString(ctx, "label");

                                            ModConfig.filterPresets.put(labelInput, ArrayListUtil.cloneref(ModConfig.getFilter()));
                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.save-preset", ModConfig.mainColor.getChar(), labelInput, ModConfig.mainColor.getChar(), ModConfig.getFilter().size(), VersionedText.translatable("text.player-visibility.message.entry-prefix").getString(), ModConfig.getFilter().size() == 1 ? VersionedText.translatable("text.player-visibility.message.singular").getString() : VersionedText.translatable("text.player-visibility.message.plural").getString()));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("presetdelete")
                                .then(argument("label", string())
                                        .suggests(presetLabelSuggestionProvider)
                                        .executes(ctx -> {
                                            String labelInput = getString(ctx, "label");

                                            PlayerVisibilityClient.LOGGER.info("So the current preset is {} while the label input is {}, they are {}", ModConfig.currentPreset, labelInput, ModConfig.currentPreset == labelInput ? "IDENTICAL" : "DIFFERENT");

                                            if (!ModConfig.filterPresets.containsKey(labelInput)) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.no-load-preset", labelInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            if (Objects.equals(ModConfig.currentPreset, labelInput)) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.no-delete-preset", labelInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.delete-preset", ModConfig.mainColor.getChar(), labelInput, ModConfig.mainColor.getChar(), ModConfig.getFilter().size(), VersionedText.translatable("text.player-visibility.message.entry-prefix").getString(), ModConfig.getFilter().size() == 1 ? VersionedText.translatable("text.player-visibility.message.singular").getString() : VersionedText.translatable("text.player-visibility.message.plural").getString()));
                                            ModConfig.filterPresets.remove(labelInput);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("print")
                                .executes(ctx -> {
                                    String jsonString = gson.toJson(ModConfig.serialize());
                                    String highlightString = HighlightUtil.highlightJson(jsonString);

                                    PlayerVisibility.sendMessage(highlightString);

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(literal("reset")
                                .executes(ctx -> {
                                    PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.reset-config", ModConfig.mainColor.getChar()));

                                    ConfigUtil.reset();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(literal("main-color")
                                .then(argument("color", string())
                                        .suggests(colorSuggestionProvider)
                                        .executes(ctx -> {
                                            String colorInput = getString(ctx, "color");
                                            TextColor color = null;
                                            boolean isValid = true;

                                            try {
                                                color = TextColor.valueOf(colorInput.toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                isValid = false;
                                            }

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-color", colorInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.mainColor = color;
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.changed-color", color.getChar(), color.getDisplayName()));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("message-type")
                                .then(argument("type", string())
                                        .suggests(messageTypeSuggestionProvider)
                                        .executes(ctx -> {
                                            String typeInput = getString(ctx, "type");
                                            MessageType type = null;
                                            boolean isValid = true;

                                            try {
                                                type = MessageType.valueOf(typeInput.toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                isValid = false;
                                            }

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-type", typeInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.messageType = type;
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.changed-type", ModConfig.mainColor.getChar(), ModConfig.messageType.getDisplayName()));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("filter-type")
                                .then(argument("type", string())
                                        .suggests(filterTypeSuggestionProvider)
                                        .executes(ctx -> {
                                            String typeInput = getString(ctx, "type");
                                            FilterType type = null;
                                            boolean isValid = true;

                                            try {
                                                type = FilterType.valueOf(typeInput.toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                isValid = false;
                                            }

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-filter", typeInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.filterType = type;
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.changed-filter", ModConfig.mainColor.getChar(), ModConfig.filterType.getDisplayName()));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-self")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hideSelf = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hideSelf ? VersionedText.translatable("text.player-visibility.message.hide-self") : VersionedText.translatable("text.player-visibility.message.show-self")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-players")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hidePlayers = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hidePlayers ? VersionedText.translatable("text.player-visibility.message.hide-players") : VersionedText.translatable("text.player-visibility.message.show-players")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-shadows")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hideShadows = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hideShadows ? VersionedText.translatable("text.player-visibility.message.hide-shadows") : VersionedText.translatable("text.player-visibility.message.show-shadows")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-entities")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hideEntities = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hideEntities ? VersionedText.translatable("text.player-visibility.message.hide-entities") : VersionedText.translatable("text.player-visibility.message.show-entities")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-hitboxes")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hideHitboxes = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hideHitboxes ? VersionedText.translatable("text.player-visibility.message.hide-hitboxes") : VersionedText.translatable("text.player-visibility.message.show-hitboxes")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-fire")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hideFire = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hideFire ? VersionedText.translatable("text.player-visibility.message.hide-fire") : VersionedText.translatable("text.player-visibility.message.show-fire")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("hide-nametags")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.hideNametags = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.hideNametags ? VersionedText.translatable("text.player-visibility.message.hide-nametags") : VersionedText.translatable("text.player-visibility.message.show-nametags")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("comfort-zone")
                                .then(argument("bool", string())
                                        .suggests(booleanSuggestionProvider)
                                        .executes(ctx -> {
                                            String boolInput = getString(ctx, "bool");
                                            boolean isValid = boolInput.equalsIgnoreCase("true") || boolInput.equalsIgnoreCase("false");

                                            if (!isValid) {
                                                PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.invalid-boolean", boolInput));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            ModConfig.comfortZone = Boolean.parseBoolean(boolInput.toLowerCase());
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), ModConfig.comfortZone ? VersionedText.translatable("text.player-visibility.message.enable-comfort-zone") : VersionedText.translatable("text.player-visibility.message.disable-comfort-zone")));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("comfort-distance")
                                .then(argument("distance", floatArg())
                                        .executes(ctx -> {
                                            float distance = getFloat(ctx, "distance");

                                            ModConfig.comfortDistance = distance;
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), VersionedText.translatable("text.player-visibility.message.change-comfort-distance", ModConfig.mainColor.getChar(), distance).getString()));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("comfort-falloff")
                                .then(argument("falloff", floatArg())
                                        .executes(ctx -> {
                                            float falloff = getFloat(ctx, "falloff");

                                            ModConfig.comfortFalloff = falloff;
                                            ConfigUtil.save();

                                            PlayerVisibility.sendMessage(VersionedText.translatable("text.player-visibility.message.action-delimiter", ModConfig.mainColor.getChar(), VersionedText.translatable("text.player-visibility.message.change-comfort-falloff", ModConfig.mainColor.getChar(), falloff).getString()));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(literal("toggle")
                        .executes(ctx -> {
                            PlayerVisibility.toggleFilter();
                            return Command.SINGLE_SUCCESS;
                        })
                ));
    }
}