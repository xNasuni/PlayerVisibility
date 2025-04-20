package win.transgirls.playervisibility.mixin.shadow;

import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.21.5"})
public class ShadowMixinv1215 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_10017;FLnet/minecraft/class_4538;F)V", at = @At("HEAD"), cancellable = true)
    private static void injectShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Coerce Object entity, float opacity, WorldView world, float radius, CallbackInfo ci) {
        if (ModConfig.hideShadows && PlayerVisibility.shouldHideEntityRenderState(entity)) {
            ci.cancel();
        }
    }
}