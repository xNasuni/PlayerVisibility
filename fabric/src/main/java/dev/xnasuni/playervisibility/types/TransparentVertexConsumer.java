package dev.xnasuni.playervisibility.types;

import dev.xnasuni.playervisibility.PlayerVisibilityClient;
import static dev.xnasuni.playervisibility.PlayerVisibility.transparency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;

public class TransparentVertexConsumer<E extends Entity> implements VertexConsumer {
    private final VertexConsumer parent;
    private final E entity; // value is -1 if disabled, value ranges between 0 and 255 if not
    private Method vertexFFF = null;
    private Method overlayII = null;

    public TransparentVertexConsumer(VertexConsumer parent, E entity) {
        this.parent = parent;
        this.entity = entity;
        try {
            vertexFFF = parent.getClass().getMethod("method_22912", float.class, float.class, float.class);
        } catch (Throwable ignored) {
            PlayerVisibilityClient.LOGGER.error("No vertexFFF", ignored);
        }
        try {
            overlayII = parent.getClass().getMethod("method_60796", int.class, int.class);
        } catch (Throwable ignored) {
            PlayerVisibilityClient.LOGGER.error("No overlayII", ignored);
        }
    }

    @Override public VertexConsumer color(int red, int green, int blue, int alpha) {
        int finalAlpha = transparency.get(entity) == -1 ? alpha : transparency.get(entity);
        return parent.color(red, green, blue, finalAlpha);
    }

    // fill ins for 1.21+
    public VertexConsumer method_22912(float x, float y, float z) throws InvocationTargetException, IllegalAccessException {
        return (VertexConsumer) vertexFFF.invoke(parent, x, y, z);
    }
    public VertexConsumer method_60796(int u, int v) throws InvocationTargetException, IllegalAccessException {
        return (VertexConsumer) overlayII.invoke(parent, u, v);
    }

    // not modified
    @Override public VertexConsumer vertex(double x, double y, double z) {
        return parent.vertex(x, y, z);
    }
    @Override public VertexConsumer texture(float u, float v) {
        return parent.texture(u, v);
    }
    @Override public VertexConsumer overlay(int u, int v) {
        return parent.overlay(u, v);
    }
    @Override public VertexConsumer light(int u, int v) {
        return parent.light(u, v);
    }
    @Override public VertexConsumer normal(float x, float y, float z) {
        return parent.normal(x, y, z);
    }
    @Override public void next() {
        parent.next();
    }
}