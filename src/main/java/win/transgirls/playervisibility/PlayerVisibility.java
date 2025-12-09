package win.transgirls.playervisibility;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import win.transgirls.crossfabric.multiversion.ClientCommandManager;
import win.transgirls.crossfabric.multiversion.VersionedText;
import win.transgirls.playervisibility.commands.VisibilityCommand;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.FilterType;
import win.transgirls.playervisibility.types.MessageType;
import win.transgirls.playervisibility.util.ArrayListUtil;
import win.transgirls.playervisibility.util.ConfigUtil;

import static win.transgirls.playervisibility.PlayerVisibilityClient.LOGGER;

import com.mojang.brigadier.CommandDispatcher;

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
    public static final String MOD_ID = "player-visibility";

    public static MinecraftClient minecraftClient;
    public static boolean debugKey = false;

    private static boolean filterEnabled = true;
    private static KeyBinding toggleFilter;

    @Override
    public void onInitializeClient() {
        ConfigUtil.init();

        minecraftClient = MinecraftClient.getInstance();

        toggleFilter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.player-visibility.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyBinding.Category.create(Identifier.of(MOD_ID, "category.player-visibility.main"))
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

    public static boolean shouldHideEntityRenderState(LivingEntityRenderState state) {
        if (isVisibilityEnabled()) {
            return false;
        }

        if (checkComfortZone(new Vec3d(state.x, state.y, state.z))) {
            return false;
        }
        try {
            if (state instanceof PlayerEntityRenderState playerEntityRenderState) {
                String playerUsername = playerEntityRenderState.playerName.getString();
                if (playerUsername.equals(minecraftClient.getSession().getUsername())) {
                    return ModConfig.hideSelf;
                }

                boolean isInFilterList = ArrayListUtil.containsLowercase(ModConfig.getFilter(), playerUsername);

                if (ModConfig.filterType == FilterType.BLACKLIST) {
                    return isInFilterList;
                } else {
                    return !isInFilterList;
                }
            } else if (state instanceof LivingEntityRenderState) {
                return ModConfig.hideEntities;
            }
        } catch (NullPointerException ignored) {}


        return true; // default behavior
    }

    public static <E extends Entity> boolean shouldHideState(E entity) {
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

            if (checkComfortZone(entity.getEntityPos())) {
                return false;
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
        if (ModConfig.messageType == MessageType.SYSTEM_TOAST) {
            minecraftClient.getToastManager().add(new SystemToast(new SystemToast.Type(2769), VersionedText.of("Player Visibility"), VersionedText.of(message)));
        }

//        if (ModConfig.messageType == MessageType.HIDDEN) {
//          //do nothing LOL why did I even write this if statement I'm so silly🥺🥺🥺
//        }
    }

    public static <S extends LivingEntityRenderState> float getAlphaFor(S state) {
        if (ModConfig.comfortZone) {
            double sqDst = getSquaredDistanceToCamera(new Vec3d(state.x, state.y, state.z));
            double distance = Math.sqrt(sqDst);

            if (distance <= ModConfig.comfortDistance + ModConfig.comfortFalloff) {
                double transitionStart = ModConfig.comfortDistance; // 🏳️‍⚧️ :3
                double transitionEnd = ModConfig.comfortDistance + ModConfig.comfortFalloff;

                return (float) Math.min(Math.max((distance - transitionStart) / (transitionEnd - transitionStart), 0), 1);
            }
            return 1f;
        }

        return 0f;
    }

    public static <S extends LivingEntityRenderState> boolean checkComfortZone(Vec3d vec3d) {
        if (ModConfig.comfortZone) {
            double sqDst = getSquaredDistanceToCamera(vec3d);
            double distance = Math.sqrt(sqDst);

            return !(distance <= ModConfig.comfortDistance + ModConfig.comfortFalloff);
        }

        return false;
    }

    public static double getSquaredDistanceToCamera(Vec3d vec3d) {
        assert MinecraftClient.getInstance().getCameraEntity() != null;
        return MinecraftClient.getInstance().getCameraEntity().squaredDistanceTo(vec3d);
    }
}