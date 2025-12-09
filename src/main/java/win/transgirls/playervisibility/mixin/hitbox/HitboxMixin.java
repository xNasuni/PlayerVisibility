package win.transgirls.playervisibility.mixin.hitbox;

import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BatchingRenderCommandQueue.class, priority = 1001)
public class HitboxMixin {
    @Inject(method = "submitDebugHitbox", at = @At("HEAD"), cancellable = true)
    private void injectHitbox(MatrixStack matrices, EntityRenderState renderState, EntityHitboxAndView debugHitbox, CallbackInfo ci) {
        if (ModConfig.hideHitboxes && renderState instanceof LivingEntityRenderState livingEntityRenderState) {
            if (PlayerVisibility.shouldHideEntityRenderState(livingEntityRenderState)) {
                ci.cancel();
            }
        }
    }
}
