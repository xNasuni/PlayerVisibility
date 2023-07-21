package dev.xnasuni.playervisibility;

import com.mojang.brigadier.CommandDispatcher;
import dev.xnasuni.playervisibility.commands.VisibilityCommand;
import dev.xnasuni.playervisibility.config.ModConfig;
import dev.xnasuni.playervisibility.util.ArrayListUtil;
import dev.xnasuni.playervisibility.util.ConfigUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class PlayerVisibility implements ModInitializer {
    public static final String ModID = "player-visibility";
    public static final String DisplayModID = "PlayerVisibility";

    public static final Logger LOGGER = LoggerFactory.getLogger(DisplayModID);
    public static MinecraftClient Minecraft;

    private static boolean Visible = true;

    private static String[] WhitelistedPlayers;

    private static KeyBinding ToggleVisibility;

    public static Path ConfigDirectory;

    @Override
    public void onInitialize() {
        ConfigDirectory = FabricLoader.getInstance().getConfigDir().resolve("player-visibility");
        try {
            Files.createDirectories(ConfigDirectory);
            if (Files.exists(ConfigDirectory.resolve("whitelisted-players.txt"))) {
                try {
                    WhitelistedPlayers = ConfigUtil.Load();//new String[]{ };
                } catch (IOException e) {
                    WhitelistedPlayers = new String[]{};
                    LOGGER.warn("File `whitelisted-players.txt` could not be loaded, defaulting to empty list.");
                }
            } else {
                WhitelistedPlayers = new String[]{};
                Files.createFile(ConfigDirectory.resolve("whitelisted-players.txt"));
            }
        } catch (IOException e) {
            WhitelistedPlayers = new String[]{};
            LOGGER.warn("Could not create directory, defaulting to empty list.");
        }

        ModConfig.init();

        Minecraft = MinecraftClient.getInstance();

        ToggleVisibility = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.player-visibility.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.player-visibility.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ToggleVisibility.wasPressed()) {
                ToggleVisibility();
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            try {
                ConfigUtil.Save(WhitelistedPlayers);
            } catch (IOException e) {
                LOGGER.error("Failed to save whitelisted players to `whitelisted-players.txt`", e);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register(PlayerVisibility::RegisterCommands);

        LOGGER.info("Player Visibility Initialized");
    }

    private static void RegisterCommands(CommandDispatcher<FabricClientCommandSource> Dispatcher, CommandRegistryAccess Registry) {
        VisibilityCommand.Register(Dispatcher);
    }

    public static void ToggleVisibility() {
        Visible = !Visible;
        String VisibleString = "§coff";
        if (Visible) {
            VisibleString = "§aon";
        }
        Minecraft.player.sendMessage(Text.of(String.format("§%cPlayer Visibility§f is now §f%s§f", ModConfig.INSTANCE.MainColor.GetChar(), VisibleString)), true);
    }

    public static boolean IsVisible() {
        return Visible;
    }

    public static String[] GetWhitelistedPlayers() {
        return WhitelistedPlayers;
    }

    public static void WhitelistPlayer(String Username) {
        String CasedName = ArrayListUtil.GetCase(WhitelistedPlayers, Username);

        if (Username.equalsIgnoreCase(Minecraft.player.getName().getString())) {
            Minecraft.player.sendMessage(Text.of("§cYou can't whitelist yourself!"), true);
            return;
        }

        if (ArrayListUtil.ContainsLowercase(WhitelistedPlayers, Username)) {
            Minecraft.player.sendMessage(Text.of(String.format("§f '%s'§c is already whitelisted!", CasedName)), true);
            return;
        }

        WhitelistedPlayers = ArrayListUtil.AddStringToArray(WhitelistedPlayers, Username);
        ModConfig.PlayerWhitelist = PlayerVisibility.GetWhitelistedPlayers();
        Minecraft.player.sendMessage(Text.of(String.format("§aAdded §f'%s'§a to the whitelist.", CasedName)), true);
    }

    public static void UnwhitelistPlayer(String Username) {
        String CasedName = ArrayListUtil.GetCase(WhitelistedPlayers, Username);

        if (Username.equalsIgnoreCase(Minecraft.player.getName().getString())) {
            Minecraft.player.sendMessage(Text.of("§cYou can't unwhitelist yourself!"), true);
            return;
        }

        if (!ArrayListUtil.ContainsLowercase(WhitelistedPlayers, Username)) {
            Minecraft.player.sendMessage(Text.of(String.format("§f'%s'§c is not whitelisted!", CasedName)), true);
            return;
        }

        WhitelistedPlayers = ArrayListUtil.RemoveStringToArray(WhitelistedPlayers, CasedName);
        ModConfig.PlayerWhitelist = PlayerVisibility.GetWhitelistedPlayers();
        Minecraft.player.sendMessage(Text.of(String.format("§aRemoved §f'%s'§a from the whitelist.", CasedName)), true);
    }

    public static void ClearWhitelist() {
        int SizeBeforeClear = WhitelistedPlayers.length;

        if (SizeBeforeClear == 0) {
            Minecraft.player.sendMessage(Text.of(String.format("§cThe whitelist is already empty §f(§c%s§f)", SizeBeforeClear)), true);
            return;
        }

        WhitelistedPlayers = new String[]{};
        ModConfig.PlayerWhitelist = PlayerVisibility.GetWhitelistedPlayers();
        Minecraft.player.sendMessage(Text.of(String.format("§aCleared the whitelist §f(§a%s§f)", SizeBeforeClear)), true);

    }

}
