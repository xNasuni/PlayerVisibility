package win.transgirls.playervisibility.mixin.hitbox;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = EntityHitboxDebugRenderer.class, priority = 1001)
@VersionedMixin({">=26.1"})
public class HitboxMixinv261 {
    @WrapMethod(method = "drawHitbox")
    private void wrapDrawHitbox(Entity entity, float tickProgress, boolean inLocalServer, Operation<Void> original) {
        if (ModConfig.hideHitboxes && PlayerVisibility.computeHideInfoForEntityOrState(entity).hide) {
            return;
        }

        original.call(entity, tickProgress, inLocalServer);
    }
}