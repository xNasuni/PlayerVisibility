package win.transgirls.playervisibility.mixin.fire;

import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = BatchingRenderCommandQueue.class, priority = 1001)
public class FireMixin {
    @Inject(method = "submitFire", at = @At("HEAD"), cancellable = true)
    private void injectFire(MatrixStack matrices, EntityRenderState renderState, Quaternionf rotation, CallbackInfo ci) {
        if (ModConfig.hideFire && renderState instanceof LivingEntityRenderState livingEntityRenderState) {
            if (PlayerVisibility.shouldHideEntityRenderState(livingEntityRenderState)) {
                ci.cancel();
            }
        }
    }
}
