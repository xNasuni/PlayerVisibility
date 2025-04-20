package dev.xnasuni.playervisibility.mixin.hitbox;

import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;
import net.minecraft.client.render.VertexConsumer;
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
public class HitboxMixinv1215 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_10017;Lnet/minecraft/class_10933;Lnet/minecraft/class_4597;)V", at = @At("HEAD"), cancellable = true)
    private void injectHitbox(MatrixStack matrices, @Coerce Object entity, @Coerce Object hitbox, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (ModConfig.hideHitboxes && PlayerVisibility.shouldHideEntityRenderState(entity)) {
            ci.cancel();
        }
    }
}