package win.transgirls.playervisibility.types;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public class EntityInfo {
    public final Text name;
    public final Double distance;
    public final Object instance;
    public final boolean isPlayer;
    public final boolean isSelf;

    public EntityInfo(Text name, Double distance, boolean isSelf, Object instance) {
        boolean isPlayer = instance instanceof PlayerEntityRenderState || instance instanceof PlayerEntity;

        this.name = Objects.requireNonNull(name, "name must not be null");
        this.distance = Objects.requireNonNull(distance, "distance must not be null");
        this.instance = Objects.requireNonNull(instance, "instance must not be null");
        this.isPlayer = isPlayer;
        this.isSelf = isSelf;
    }
}