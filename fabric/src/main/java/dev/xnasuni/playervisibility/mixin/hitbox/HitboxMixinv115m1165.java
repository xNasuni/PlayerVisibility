package dev.xnasuni.playervisibility.mixin.hitbox;

import win.transgirls.crossfabric.annotation.VersionedMixin;
import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.16.5"})
public class HitboxMixinv115m1165 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;Lnet/minecraft/class_1297;F)V", at = @At("HEAD"), cancellable = true)
    private void injectHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if (ModConfig.hideHitboxes && PlayerVisibility.shouldHideEntity(entity)) {
            ci.cancel();
        }
    }
}