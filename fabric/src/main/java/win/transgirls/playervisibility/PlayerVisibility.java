package win.transgirls.playervisibility;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import win.transgirls.crossfabric.Constants;
import win.transgirls.crossfabric.multiversion.ClientCommandManager;
import win.transgirls.playervisibility.commands.VisibilityCommand;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.EntityInfo;
import win.transgirls.playervisibility.types.FilterType;
import win.transgirls.playervisibility.types.HideInfo;
import win.transgirls.playervisibility.util.ArrayListUtil;
import win.transgirls.playervisibility.util.ConfigUtil;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import static win.transgirls.playervisibility.PlayerVisibilityClient.LOGGER;

public class PlayerVisibility implements ClientModInitializer {
    public static MinecraftClient mc;
    public static Map<Object, Integer> transparency = new WeakHashMap<>();
    private static boolean filterEnabled = true;
    private static KeyBinding toggleFilter;

    public static void registerCommands(CommandDispatcher<Object> dispatcher) {
        VisibilityCommand.register(dispatcher);
    }

    public static void toggleFilter() {
        filterEnabled = !filterEnabled;
        String VisibleString = "§c" + (Text.translatable("text.player-visibility.message.off")).getString();
        if (filterEnabled) {
            VisibleString = "§" + ModConfig.mainColor.getChar() + (Text.translatable("text.player-visibility.message.on")).getString();
        }
        sendMessage((Text.translatable("text.player-visibility.message.visibility-toggle", ModConfig.mainColor.getChar(), VisibleString)));
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
                    boolean shouldHide = info.isSelf ? ModConfig.hideSelf : shouldHidePlayerByName(info.name.getString());
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
            throw new IllegalStateException(String.format("Player Visibility could not handle check on %s: \n%s", Constants.getMinecraftVersion(), e));
        }
    }

    public static EntityInfo getEntityInfo(Object entityOrState) {
        if (entityOrState instanceof PlayerEntityRenderState state) {
            return new EntityInfo(Objects.requireNonNullElse(state.playerName, Text.empty()), smoothSquaredDistance(state), mc.player != null && state.id == mc.player.getId(), entityOrState);
        }
        if (entityOrState instanceof EntityRenderState state) {
            return new EntityInfo(Text.empty(), smoothSquaredDistance(state), false, entityOrState);
        }
        if (entityOrState instanceof Entity state) {
            return new EntityInfo(state.getName(), mc.getCameraEntity().squaredDistanceTo(state), state == mc.player, entityOrState);
        }

        throw new IllegalStateException(String.format("PlayerVisibility could not decode entity or state '%s' on %s", entityOrState.getClass().getName().replaceAll("\\.", "/"), Constants.getMinecraftVersion()));
    }

    private static double smoothSquaredDistance(EntityRenderState state) {
        return mc.gameRenderer.getCamera().getCameraPos().squaredDistanceTo(state.x, state.y, state.z);
    }

    private static boolean shouldHidePlayerByName(String name) {
        boolean isInFilterList = ArrayListUtil.containsLowercase(ModConfig.getFilter(), name);
        return ModConfig.filterType == FilterType.WHITELIST ? !isInFilterList : isInFilterList;
    }

    public static void sendMessage(Text text) {
        sendMessage(text.getString());
    }

    public static void sendMessage(String message) {
        LOGGER.info(message);
        if (mc.player == null) {
            return;
        }

        String messagePrefix = "§fᴘʟᴀʏᴇʀ ᴠɪsɪʙɪʟɪᴛʏ" + "§f" + " » ";

        switch (ModConfig.messageType) {
            case CHAT_MESSAGE -> mc.player.sendMessage(Text.of(messagePrefix + message), false);
            case ACTION_BAR -> mc.player.sendMessage(Text.of(message), true);
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
        mc = MinecraftClient.getInstance();

        toggleFilter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.player-visibility.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyBinding.Category.create(Identifier.of("player-visibility", "main"))
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