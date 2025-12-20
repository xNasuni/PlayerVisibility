package win.transgirls.playervisibility.mixin.hitbox;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.21.5", "<=1.21.8"})
public class HitboxMixinv1215m1218 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_10017;Lnet/minecraft/class_10933;Lnet/minecraft/class_4597;)V", at = @At("HEAD"), cancellable = true)
    private void injectRenderHitboxes(MatrixStack matrices, @Coerce Object state, @Coerce Object hitbox, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (ModConfig.hideHitboxes && PlayerVisibility.computeHideInfoForEntityOrState(state).hide) {
            ci.cancel();
        }
    }
}