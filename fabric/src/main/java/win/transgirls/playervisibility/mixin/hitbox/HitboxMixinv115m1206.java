package win.transgirls.playervisibility.mixin.hitbox;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;

import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.20.6"})
public class HitboxMixinv115m1206 {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void injectRenderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if (ModConfig.hideHitboxes && PlayerVisibility.computeHideInfoForEntityOrState(entity).hide) {
            ci.cancel();
        }
    }
}