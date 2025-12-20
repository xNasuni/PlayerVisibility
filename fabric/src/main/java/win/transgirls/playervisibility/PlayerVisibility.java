package win.transgirls.playervisibility;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import win.transgirls.crossfabric.Constants;
import win.transgirls.crossfabric.multiversion.ClientCommandManager;
import win.transgirls.crossfabric.multiversion.VersionedText;
import win.transgirls.crossfabric.tools.ClassUtils;
import win.transgirls.playervisibility.commands.VisibilityCommand;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.*;
import win.transgirls.playervisibility.util.ArrayListUtil;
import win.transgirls.playervisibility.util.ConfigUtil;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import static win.transgirls.playervisibility.PlayerVisibilityClient.LOGGER;

public class PlayerVisibility implements ClientModInitializer {
    public static MinecraftClient minecraftClient;
    public static Map<Object, Integer> transparency = new WeakHashMap<>();
    private static boolean filterEnabled = true;
    private static KeyBinding toggleFilter;

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

    public static HideInfo computeHideInfoForEntityOrState(Object entity) {
        return computeHideInfo(getEntityInfo(entity));
    }

    public static HideInfo computeHideInfo(EntityInfo info) {
        HideInfo visible = new HideInfo(false, info.distance);

        if (isVisibilityEnabled()) {
            return visible;
        }

        try {
            if (info.isPlayer) {
                if (ModConfig.hidePlayers) {
                    boolean shouldHide = shouldHidePlayerByName(info.name.getString());
                    return new HideInfo(shouldHide, info.distance);
                } else {
                    return visible;
                }
            }

            if (ModConfig.hideEntities) {
                return new HideInfo(true, info.distance);
            } else {
                return visible;
            }
        } catch (Throwable e) {
            throw new IllegalStateException(String.format(
                    "Player Visibility could not handle check on %s: \n%s",
                    Constants.getMinecraftVersion(), e));
        }
    }

    public static EntityInfo getEntityInfo(Object entityOrState) {
        Throwable e = null;
        Throwable e2 = null;
        Throwable e3 = null;

        try { // read as PlayerEntityRenderState.class
            Class<?> entityRenderState = Class.forName("net.minecraft.class_10017");
            Class<?> playerEntityRenderState = Class.forName("net.minecraft.class_10055");

            if (!playerEntityRenderState.isAssignableFrom(entityOrState.getClass())) {
                throw new IllegalStateException(String.format("%s does not inherit %s", entityOrState.getClass().getSimpleName(), playerEntityRenderState.getSimpleName()));
            }

            Text name = Text.of((String) ClassUtils.firstFieldWithName(playerEntityRenderState, "field_53529").get(entityOrState));
            Text displayName = Optional.ofNullable(ClassUtils.firstFieldWithName(playerEntityRenderState, "field_53525").get(entityOrState))
                    .map(obj -> (Text) obj)
                    .orElse(Text.of(""));
            double squaredDistanceToCamera = (double) ClassUtils.firstFieldWithName(entityRenderState, "field_53332").get(entityOrState);

            return new EntityInfo(name, displayName, squaredDistanceToCamera, entityOrState);
        } catch (Throwable _e) {
            e = _e;
            try { // read as EntityRenderState.class
                Class<?> entityRenderState = ClassUtils.forName("net.minecraft.class_10017");

                if (!entityRenderState.isAssignableFrom(entityOrState.getClass())) {
                    throw new IllegalStateException(String.format("%s does not inherit %s", entityOrState.getClass().getSimpleName(), entityRenderState.getSimpleName()));
                }

                Text name = VersionedText.of("");
                Text displayName = VersionedText.of("");
                double squaredDistanceToCamera = (double) ClassUtils.firstFieldWithName(entityRenderState, "field_53332").get(entityOrState);

                return new EntityInfo(name, displayName, squaredDistanceToCamera, entityOrState);
            } catch (Throwable _e2) {
                e2 = _e2;
                try { // read as Entity.class
                    assert minecraftClient.cameraEntity != null;

                    Entity entity = ((Entity) entityOrState);

                    Text name = entity.getName();
                    Text displayName = entity.getDisplayName();
                    double squaredDistanceToCamera = minecraftClient.cameraEntity.squaredDistanceTo(entity);

                    return new EntityInfo(name, displayName, squaredDistanceToCamera, entityOrState);
                } catch (Throwable _e3) {
                    e3 = _e3;
                }
            }
        }

        throw new IllegalStateException(String.format("PlayerVisibility could not decode entity or state '%s' on %s: \n\n%s\n\n%s\n\n%s", entityOrState.getClass().getName().replaceAll("\\.", "/"), Constants.getMinecraftVersion(), e, e2, e3));
    }

    private static boolean shouldHidePlayerByName(String name) {
        if (name.equalsIgnoreCase(minecraftClient.getSession().getUsername())) {
            return ModConfig.hideSelf;
        }

        boolean isInFilterList = ArrayListUtil.containsLowercase(ModConfig.getFilter(), name);
        return ModConfig.filterType == FilterType.WHITELIST ? !isInFilterList : isInFilterList;
    }

    public static void sendMessage(Object text) {
        try {
            if (Class.forName("net.minecraft.class_5250").isAssignableFrom(text.getClass())) { // MutableText
                sendMessage(((Text) text).getString());
                return;
            }
        } catch (Throwable ignored) {
        }
        if (text instanceof Text) {
            sendMessage(((Text) text).getString());
            return;
        }
        if (text instanceof String) {
            sendMessage(text);
            return;
        }

        LOGGER.info("SendMessage failed because {} isn't a supported type of message", text.getClass().getName().replaceAll("\\.", "/"));
    }

    public static void sendMessage(String message) {
        LOGGER.info(message);
        if (minecraftClient.player == null) {
            return;
        }

        String messagePrefix = "§fᴘʟᴀʏᴇʀ ᴠɪsɪʙɪʟɪᴛʏ" + "§f" + " » ";

        switch (ModConfig.messageType) {
            case CHAT_MESSAGE -> minecraftClient.player.sendMessage(Text.of(messagePrefix + message), false);
            case ACTION_BAR -> minecraftClient.player.sendMessage(Text.of(message), true);
            case HIDDEN -> {//do nothing LOL why did I even write this if statement I'm so silly🥺🥺🥺
            }
        }
    }

    public static int withAlphaFloat(int color, float alpha) {
        return withAlphaByte(color, (int) (alpha * 255.0f));
    }

    public static int withAlphaByte(int color, int alpha) {
        int alphaShifted = alpha << 24; // 0xAA000000

        color &= 0x00ffffff;            // 0x00RRGGBB
        color |= alphaShifted;          // 0x00RRGGBB
        //                               + 0xAA000000
        //                               ============
        //                                 0xAARRGGBB
        // with our modified alpha :3

        return color;
    }

    @Override
    public void onInitializeClient() {
        minecraftClient = MinecraftClient.getInstance();

        toggleFilter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.player-visibility.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.player-visibility.main"
        ));

        switch (ConfigUtil.init()) {
            case TooManyPlayers ->
                    LOGGER.info("TooManyPlayers whitelist was migrated with {} entries", ModConfig.getFilter().size());
            case EventUtils ->
                    LOGGER.info("EventUtils whitelist was migrated with {} entries", ModConfig.getFilter().size());
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleFilter.wasPressed()) {
                toggleFilter();
            }
        });

        // the config is saved upon changing anyway, so this is useless, but I don't wanna remove the code.
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
}