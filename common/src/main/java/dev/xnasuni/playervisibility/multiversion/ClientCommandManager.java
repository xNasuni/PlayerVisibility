package dev.xnasuni.playervisibility.multiversion;

import static dev.xnasuni.playervisibility.PlayerVisibilityClient.LOGGER;

import com.mojang.brigadier.CommandDispatcher;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.function.Consumer;

public class ClientCommandManager {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    // register commands via reflection for better compatibility
    public static void Register(Consumer<Object> func) {
        try { // (greater than) >=1.19 register commands (I am so sorry that this code ever touched the earth but I WANTTTT MULTIVERSION AHHHH)
            Class<?> clientCommandRegistrationCallbackClass = Class.forName("net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback");

            Field eventField = clientCommandRegistrationCallbackClass.getDeclaredField("EVENT");
            eventField.setAccessible(true);

            Object event = eventField.get(null);

            Class<?> clientCommandRegistrationCallbackInterface = Class.forName("net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback");
            InvocationHandler lambdaHandler = (proxy, method, args) -> {
                if (method.getName().equals("register")) {
                    CommandDispatcher<?> dispatcher = (CommandDispatcher<?>) args[0];
                    func.accept(dispatcher);
                }
                return null;
            };

            Object callback = Proxy.newProxyInstance(
                    clientCommandRegistrationCallbackInterface.getClassLoader(),
                    new Class<?>[]{clientCommandRegistrationCallbackInterface},
                    lambdaHandler
            );

            Method registerMethod = event.getClass().getDeclaredMethod("register", Object.class);
            registerMethod.setAccessible(true);
            registerMethod.invoke(event, callback);

            LOGGER.info("[>=1.19] Successfully registered commands for {}.", VersionMixinPlugin.getMinecraftVersion());
        } catch (Throwable e) { // (less than) <=1.18.2 register commands fallback
            LOGGER.warn("Failed >=1.19 command registration attempts, trying <=1.18.2...");
            try {
                Class<?> clientCommandManager = Class.forName("net.fabricmc.fabric.api.client.command.v1.ClientCommandManager");
                Field dispatcherField = clientCommandManager.getDeclaredField("DISPATCHER");
                dispatcherField.setAccessible(true);

                Object dispatcher = dispatcherField.get(null);
                func.accept(dispatcher);

                LOGGER.info("[<=1.18.2] Successfully registered commands for {} :3", VersionMixinPlugin.getMinecraftVersion());
            } catch (Throwable e2) { // last fallback
                LOGGER.error("All command registration attempts failed.\nThis is a bug!! Please report this to the developer at https://github.com/xNasuni/PlayerVisibility/issues\nRunning Minecraft {}, Exceptions: {} {}", VersionMixinPlugin.getMinecraftVersion(), e, e2);
            }
        }
    }
}