package win.transgirls.playervisibility.mixin.fire;

import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.21.5"})
public class FireMixinv1215 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_10017;Lorg/joml/Quaternionf;)V", at = @At("HEAD"), cancellable = true)
    private void injectFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Coerce Object entity, @Coerce Object quaternion, CallbackInfo ci) {
        if (ModConfig.hideFire && PlayerVisibility.shouldHideEntityRenderState(entity)) {
            ci.cancel();
        }
    }
}