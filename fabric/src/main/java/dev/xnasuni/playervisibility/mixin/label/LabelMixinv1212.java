package dev.xnasuni.playervisibility.mixin.label;

import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.config.ModConfig;
import dev.xnasuni.playervisibility.multiversion.VersionedMixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class, priority = 1001)
@VersionedMixin({">=1.21.2"})
public class LabelMixinv1212 {
    @Inject(method = "(Lnet/minecraft/class_1297;D)Z", at = @At("HEAD"), cancellable = true)
    private void injectHasLabel(Entity entity, double unk, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.hideNametags && PlayerVisibility.shouldHideEntity(entity)) {
            cir.setReturnValue(false);
        }
    }
}