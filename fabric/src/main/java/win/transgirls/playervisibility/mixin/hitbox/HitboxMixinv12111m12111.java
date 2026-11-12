package win.transgirls.playervisibility.mixin.hitbox;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = EntityHitboxDebugRenderer.class, priority = 1001)
@VersionedMixin({">=1.21.11", "<=1.21.11"})
public class HitboxMixinv12111m12111 {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/EntityHitboxDebugRenderer;drawHitbox(Lnet/minecraft/entity/Entity;FZ)V"))
    private void wrapRenderInvokeSubmitHitbox(EntityHitboxDebugRenderer instance, Entity entity, float tickProgress, boolean inLocalServer, Operation<Void> original) {
        if (ModConfig.hideHitboxes && PlayerVisibility.computeHideInfoForEntityOrState(entity).hide) {
            return;
        }

        original.call(instance, entity, tickProgress, inLocalServer);
    }
}