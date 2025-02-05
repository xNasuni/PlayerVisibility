package dev.xnasuni.playervisibility.multiversion;

import static dev.xnasuni.playervisibility.PlayerVisibilityClient.LOGGER;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class Text {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static Object translatable(String key, Object... args) {
        try {
            Class<?> textClass = Class.forName("net.minecraft.class_2561");
            Class<?> mutableTextClass = Class.forName("net.minecraft.class_5250");
            MethodType methodType = MethodType.methodType(mutableTextClass, String.class, Object[].class);
            MethodHandle translatableMethod = lookup.findStatic(textClass, "method_43469", methodType);
            return translatableMethod.invoke(key, args);
        } catch (Throwable e) {
            try {
                Class<?> translatableTextClass = Class.forName("net.minecraft.class_2588");
                Constructor<?> translatableTextCtor = translatableTextClass.getConstructor(String.class, Object[].class);

                return translatableTextCtor.newInstance(key, args);
            } catch (Throwable e2) {
                LOGGER.error("Couldn't create translatable text for {}", key, e2);
            }
        }
        throw new IllegalStateException(String.format("Text.translatable(key, args) class couldn't be found for %s", VersionMixinPlugin.getMinecraftVersion()));
    }
    public static Object translatable(String key) {
        try {
            Class<?> textClass = Class.forName("net.minecraft.class_2561");
            Class<?> mutableTextClass = Class.forName("net.minecraft.class_5250");
            MethodType methodType = MethodType.methodType(mutableTextClass, String.class);
            MethodHandle translatableMethod = lookup.findStatic(textClass, "method_43471", methodType);
            return translatableMethod.invoke(key);
        } catch (Throwable e) {
            try {
                Class<?> translatableTextClass = Class.forName("net.minecraft.class_2588");

                try {
                    MethodType ctorType = MethodType.methodType(void.class, String.class);
                    MethodHandle ctorMethod = lookup.findConstructor(translatableTextClass, ctorType);
                    return ctorMethod.invoke(key);
                } catch (Throwable ignored) {
                }

                MethodType ctorType = MethodType.methodType(void.class, String.class, Object[].class);
                MethodHandle ctorMethod = lookup.findConstructor(translatableTextClass, ctorType);
                return ctorMethod.invoke(key, new Object[0]);
            } catch (Throwable e2) {
                LOGGER.error("Couldn't create translatable text for {}", key, e2);
            }
        }
        throw new IllegalStateException(String.format("Text.translatable(key) class couldn't be found for %s", VersionMixinPlugin.getMinecraftVersion()));
    }

    public static Object of(String text) {
        try {
            Class<?> textClass = Class.forName("net.minecraft.class_8828");
            Class<?> mutableTextClass = Class.forName("net.minecraft.class_5250");
            MethodType methodType = MethodType.methodType(mutableTextClass, String.class);
            MethodHandle literalMethod = lookup.findStatic(textClass, "method_43470", methodType);
            return literalMethod.invoke(text);
        } catch (Throwable e) {
            try {
                Class<?> textClass = Class.forName("net.minecraft.class_2561");
                MethodType methodType = MethodType.methodType(textClass, String.class);
                MethodHandle ofMethod = lookup.findStatic(textClass, "method_30163", methodType);
                return ofMethod.invoke(text);
            } catch (Throwable e2) {
                try {
                    Class<?> literalTextClass = Class.forName("net.minecraft.class_2585");
                    MethodType ctorType = MethodType.methodType(void.class, String.class);
                    MethodHandle ctorMethod = lookup.findConstructor(literalTextClass, ctorType);

                    return ctorMethod.invoke(text);
                } catch (Throwable e3) {
                    LOGGER.error("Couldn't create literal text for {}", text, e3);
                }
            }
        }
        throw new IllegalStateException(String.format("Text.of class couldn't be found for %s", VersionMixinPlugin.getMinecraftVersion()));
    }
}