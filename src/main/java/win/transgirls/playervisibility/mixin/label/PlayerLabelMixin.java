package win.transgirls.playervisibility.mixin.label;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.PlayerLikeEntity;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntityRenderer.class, priority = 1001)
public class PlayerLabelMixin {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/PlayerLikeEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private <AvatarlikeEntity extends PlayerLikeEntity> void injectHasLabel(AvatarlikeEntity playerLikeEntity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.hideNametags && PlayerVisibility.shouldHideState(playerLikeEntity)) {
            cir.setReturnValue(false);
        }
    }
}
