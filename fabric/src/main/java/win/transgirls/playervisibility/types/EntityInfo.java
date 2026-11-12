package win.transgirls.playervisibility.types;

import net.minecraft.text.Text;

import java.util.Objects;

public class EntityInfo {
    public final Text name;
    public final Double distance;
    public final Object instance;
    public final boolean isPlayer;
    public final boolean isSelf;

    public EntityInfo(Text name, Double distance, boolean isSelf, Object instance) {
        boolean isPlayer = false;

        try {
            Class<?> playerEntityRenderStateClass = Class.forName("net.minecraft.class_10055");
            isPlayer = playerEntityRenderStateClass.isAssignableFrom(instance.getClass());
        } catch (Throwable ignored) {
        }
        if (!isPlayer) {
            try {
                Class<?> playerClass = Class.forName("net.minecraft.class_1657");
                isPlayer = playerClass.isAssignableFrom(instance.getClass());
            } catch (Throwable ignored2) {
            }
        }

        this.name = Objects.requireNonNull(name, "name must not be null");
        this.distance = Objects.requireNonNull(distance, "distance must not be null");
        this.instance = Objects.requireNonNull(instance, "instance must not be null");
        this.isPlayer = isPlayer;
        this.isSelf = isSelf;
    }
}