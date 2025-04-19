package dev.xnasuni.playervisibility.mixin.fire;

import dev.xnasuni.crossfabric.annotation.VersionedMixin;
import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.20.2"})
public class FireMixinv115m1202 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_1297;)V", at = @At("HEAD"), cancellable = true)
    private void injectFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, CallbackInfo ci) {
        if (ModConfig.hideFire && PlayerVisibility.shouldHideEntity(entity)) {
            ci.cancel();
        }
    }
}