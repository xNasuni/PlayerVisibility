package dev.xnasuni.playervisibility.mixin.fire;

import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;
import dev.xnasuni.playervisibility.multiversion.VersionedMixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.20.3", "<=1.21.1"})
public class FireMixinv1203m1211 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_1297;Lorg/joml/Quaternionf;)V", at = @At("HEAD"), cancellable = true)
    private void injectFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, @Coerce Object quaternion, CallbackInfo ci) {
        if (ModConfig.hideFire && PlayerVisibility.shouldHideEntity(entity)) {
            ci.cancel();
        }
    }
}