package win.transgirls.playervisibility.mixin.label;

import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Affects all entities except players for some reason, see PlayerEntityRenderer.class
@Mixin(value = EntityRenderer.class, priority = 1001)
public abstract class LabelMixin {
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void injectHasLabel(Entity entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.hideNametags && PlayerVisibility.shouldHideState(entity)) {
            cir.setReturnValue(false);
        }
    }
}
