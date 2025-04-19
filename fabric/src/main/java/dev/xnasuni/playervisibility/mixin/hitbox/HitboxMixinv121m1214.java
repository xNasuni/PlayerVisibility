package dev.xnasuni.playervisibility.mixin.hitbox;

import dev.xnasuni.crossfabric.annotation.VersionedMixin;
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
@VersionedMixin({">=1.21", "<=1.21.4"})
public class HitboxMixinv121m1214 {
    @Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;Lnet/minecraft/class_1297;FFFF)V", at = @At("HEAD"), cancellable = true)
    private static void injectHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float unk1, float unk2, float unk3, CallbackInfo ci) {
        if (ModConfig.hideHitboxes && PlayerVisibility.shouldHideEntity(entity)) {
            ci.cancel();
        }
    }
}