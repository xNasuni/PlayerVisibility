package win.transgirls.playervisibility.mixin.fire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

@Mixin(value = EntityRenderManager.class, priority = 1001)
@VersionedMixin({">=26.1"})
public class FireMixinv261 {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitFire(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityRenderState;Lorg/joml/Quaternionf;)V"))
    private void wrapRenderInvokeSubmitFire(OrderedRenderCommandQueue instance, MatrixStack matrixStack, EntityRenderState state, Quaternionf quaternionf, Operation<Void> original) {
        if (ModConfig.hideFire && PlayerVisibility.computeHideInfoForEntityOrState(state).hide) {
            return;
        }

        original.call(instance, matrixStack, state, quaternionf);
    }
}