package win.transgirls.playervisibility.mixin.entity;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "getMixColor", at = @At("RETURN"), cancellable = true)
    private <S extends LivingEntityRenderState> void injectAlpha(S state, CallbackInfoReturnable<Integer> cir) {
        boolean shouldHide = (state instanceof PlayerEntityRenderState && ModConfig.hidePlayers) || (!(state instanceof PlayerEntityRenderState) && ModConfig.hideEntities);

        if (shouldHide && PlayerVisibility.shouldHideEntityRenderState(state)) {
            cir.setReturnValue(ColorHelper.withAlpha(PlayerVisibility.getAlphaFor(state), cir.getReturnValue()));
        }
    }

    @Inject(method = "shouldRenderFeatures", at = @At("RETURN"), cancellable = true)
    private <S extends LivingEntityRenderState> void injectFeatures(S state, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerVisibility.shouldHideEntityRenderState(state)) {
            cir.setReturnValue(false);
        }
    }
}
