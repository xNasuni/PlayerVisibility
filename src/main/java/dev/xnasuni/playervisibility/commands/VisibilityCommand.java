package dev.xnasuni.playervisibility.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VisibilityCommand {

    public static void Register(CommandDispatcher<FabricClientCommandSource> Dispatcher) {
        SuggestionProvider<FabricClientCommandSource> UsernameSuggestionProvider = (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();

            for (String Suggestion : PlayerVisibility.GetWhitelistedPlayers()) {
                if (Suggestion.toLowerCase().startsWith(input)) {
                    builder.suggest(Suggestion);
                }
            }

            return builder.buildFuture();
        };

        Dispatcher.register(literal("visibility")
                .then(literal("whitelist")
                        .then(literal("add")
                                .then(argument("username", string())
                                        .executes(ctx -> {
                                            String Username = getString(ctx, "username");
                                            PlayerVisibility.WhitelistPlayer(Username);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("username", string())
                                        .suggests(UsernameSuggestionProvider)
                                        .executes(ctx -> {
                                            String Username = getString(ctx, "username");
                                            PlayerVisibility.UnwhitelistPlayer(Username);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("clear")
                                .executes(ctx -> {
                                    PlayerVisibility.ClearWhitelist();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(literal("view")
                                .executes(ctx -> {
                                    StringBuilder WhitelistString = new StringBuilder();
                                    for (String WhitelistedUsername : PlayerVisibility.GetWhitelistedPlayers()) {
                                        if (WhitelistString.toString().equals("")) {
                                            WhitelistString.append(String.format("§%c'%s'§f", ModConfig.INSTANCE.MainColor.GetChar(), WhitelistedUsername));
                                        } else {
                                            WhitelistString.append(String.format(", §%c'%s'§f", ModConfig.INSTANCE.MainColor.GetChar(), WhitelistedUsername));
                                        }
                                    }

                                    WhitelistString.append(String.format(" §f(§%c%s§f)", ModConfig.INSTANCE.MainColor.GetChar(), PlayerVisibility.GetWhitelistedPlayers().length));

                                    PlayerVisibility.Minecraft.player.sendMessage(Text.of(WhitelistString.toString()), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("toggle")
                        .executes(ctx -> {
                            PlayerVisibility.ToggleVisibility();
                            return Command.SINGLE_SUCCESS;
                        })
                ));
    }
}
