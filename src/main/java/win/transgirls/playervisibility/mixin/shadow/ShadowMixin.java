package win.transgirls.playervisibility.mixin.shadow;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = EntityRenderManager.class, priority = 1001)
public class ShadowMixin {
    @Inject(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitShadowPieces(Lnet/minecraft/client/util/math/MatrixStack;FLjava/util/List;)V"))
    private <S extends EntityRenderState> void injectRenderShadowArgs(S renderState, CameraRenderState cameraRenderState, double d, double e, double f, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CallbackInfo ci) {
        if (ModConfig.hideShadows && renderState instanceof LivingEntityRenderState livingEntityRenderState) {
            if (PlayerVisibility.shouldHideEntityRenderState(livingEntityRenderState)) {
                renderState.shadowPieces.clear();
                //clear the shadow list here, because renderState is not accessible from BatchingRenderCommandQueue.submitShadowPieces
            }
        }
    }
}
