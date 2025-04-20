package dev.xnasuni.playervisibility;

import win.transgirls.crossfabric.multiversion.ClientCommandManager;
import win.transgirls.crossfabric.multiversion.VersionedText;
import dev.xnasuni.playervisibility.commands.VisibilityCommand;
import dev.xnasuni.playervisibility.config.ModConfig;
import dev.xnasuni.playervisibility.types.FilterType;
import dev.xnasuni.playervisibility.types.MessageType;
import dev.xnasuni.playervisibility.util.ArrayListUtil;
import dev.xnasuni.playervisibility.util.ConfigUtil;
import static dev.xnasuni.playervisibility.PlayerVisibilityClient.LOGGER;

import com.mojang.brigadier.CommandDispatcher;

import java.lang.reflect.Field;

import java.util.HashMap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import org.lwjgl.glfw.GLFW;

public class PlayerVisibility implements ClientModInitializer {
    public static MinecraftClient minecraftClient;
    public static boolean debugKey = false;

    private static boolean filterEnabled = true;
    private static KeyBinding toggleFilter;

    public static HashMap<Entity, Integer> transparency = new HashMap<>();

    @Override public void onInitializeClient() {
        ConfigUtil.init();

        minecraftClient = MinecraftClient.getInstance();

        toggleFilter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.player-visibility.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.player-visibility.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            debugKey = toggleFilter.isPressed();
            while (toggleFilter.wasPressed()) {
                toggleFilter();
            }
        });

        // The config is saved upon changing anyway, so this is useless, but I don't wanna remove the code.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ConfigUtil.save();
        });

        // Visibility command
        ClientCommandManager.register((dispatcher) -> {
            try {
                PlayerVisibility.registerCommands(dispatcher);
            } catch (ClassCastException e) {
                LOGGER.error("Couldn't cast dispatcher as command source", e);
            }
        });

        LOGGER.info("Player Visibility has initialized successfully :3");
    }

    public static void registerCommands(CommandDispatcher<Object> dispatcher) {
        VisibilityCommand.register(dispatcher);
    }

    public static void toggleFilter() {
        filterEnabled = !filterEnabled;
        String VisibleString = "§c" + (VersionedText.translatable("text.player-visibility.message.off")).getString();
        if (filterEnabled) {
            VisibleString = "§" + ModConfig.mainColor.getChar() + (VersionedText.translatable("text.player-visibility.message.on")).getString();
        }
        sendMessage((VersionedText.translatable("text.player-visibility.message.visibility-toggle", ModConfig.mainColor.getChar(), VisibleString)));
    }

    public static boolean isVisibilityEnabled() {
        return filterEnabled;
    }

    public static boolean shouldHideEntityRenderState(Object entity) {
        if (isVisibilityEnabled()) {
            return false;
        }

        try {
            Class<?> playerEntityRenderStateClass = Class.forName("net.minecraft.class_10055");
            Class<?> livingEntityRenderStateClass = Class.forName("net.minecraft.class_10042");

            if (entity.getClass().isAssignableFrom(playerEntityRenderStateClass)) {
                String playerUsername = null;
                Field nameField = entity.getClass().getField("name");
                nameField.setAccessible(true); // not sure if it's necessary also I'm not sure if I'm stupid but this SHOULD be okay????

                Object textName = nameField.get(entity);
                if (textName instanceof net.minecraft.text.Text) {
                    playerUsername = ((net.minecraft.text.Text) textName).getString();
                }

                if (playerUsername != null) {
                    if (playerUsername.equalsIgnoreCase(minecraftClient.getSession().getUsername())) {
                        return ModConfig.hideSelf;
                    }

                    boolean isInFilterList = ArrayListUtil.containsLowercase(ModConfig.getFilter(), playerUsername);

                    if (ModConfig.filterType == FilterType.BLACKLIST) {
                        return isInFilterList;
                    } else {
                        return !isInFilterList;
                    }
                } else {
                    return true; // default behavior
                }
            }

            if (entity.getClass().isAssignableFrom(livingEntityRenderStateClass)) {
//                return ModConfig.hideEntities;
                return true;
            }

        } catch (Throwable ignored){
        }

        return true; // default behavior
    }

    public static <E extends Entity>  boolean shouldHideEntity(E entity) {
        if (isVisibilityEnabled()) {
            return false;
        }

        if (entity instanceof MobEntity) {
            return true;
        }

        if (entity instanceof PlayerEntity) {
            String playerUsername = entity.getName().getString();

            if (playerUsername.equalsIgnoreCase(minecraftClient.getSession().getUsername())) {
                return ModConfig.hideSelf;
            }

            boolean isInFilterList = ArrayListUtil.containsLowercase(ModConfig.getFilter(), playerUsername);

            if (ModConfig.filterType == FilterType.BLACKLIST) {
                return isInFilterList;
            } else {
                return !isInFilterList;
            }
        }

        return true; // default behavior
    }

    public static void sendMessage(Object text) {
        try {
            if (Class.forName("net.minecraft.class_5250").isAssignableFrom(text.getClass())) { // MutableText
                sendMessage(((net.minecraft.text.Text) text).getString());
                return;
            }
        } catch (Throwable ignored) {
        }
        if (text instanceof net.minecraft.text.Text) {
            sendMessage(((net.minecraft.text.Text) text).getString());
            return;
        }
        if (text instanceof String) {
            sendMessage(text);
            return;
        }
    }

    public static void sendMessage(String message) {
        LOGGER.info(message);
        if (minecraftClient.player == null) {
            return;
        }

        String messagePrefix = "§fᴘʟᴀʏᴇʀ ᴠɪsɪʙɪʟɪᴛʏ" + "§f" + " » ";

//        switch (ModConfig.messageType) {
//            case CHAT_MESSAGE -> minecraftClient.player.sendMessage(Text.of(messagePrefix + message), false);
//            case ACTION_BAR -> minecraftClient.player.sendMessage(Text.of(message), true);
//            case HIDDEN -> {}
//        }

        if (ModConfig.messageType == MessageType.CHAT_MESSAGE) {
            minecraftClient.player.sendMessage((VersionedText.of(messagePrefix + message)), false);
        }
        if (ModConfig.messageType == MessageType.ACTION_BAR) {
            minecraftClient.player.sendMessage((VersionedText.of(message)), true);
        }
//        if (ModConfig.messageType == MessageType.HIDDEN) {
//          //do nothing LOL why did I even write this if statement I'm so silly🥺🥺🥺
//        }
    }
}