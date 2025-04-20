package dev.xnasuni.playervisibility.mixin.label;

import win.transgirls.crossfabric.annotation.VersionedMixin;
import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.21.1"})
public class LabelMixinv115m1211 {
    @Inject(method = "(Lnet/minecraft/class_1297;)Z", at = @At("HEAD"), cancellable = true)
    private void injectHasLabel(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.hideNametags && PlayerVisibility.shouldHideEntity(entity)) {
            cir.setReturnValue(false);
        }
    }
}