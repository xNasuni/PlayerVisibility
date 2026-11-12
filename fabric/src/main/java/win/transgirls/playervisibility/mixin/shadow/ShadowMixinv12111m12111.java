package win.transgirls.playervisibility.mixin.shadow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

import java.util.List;

@Mixin(value = EntityRenderManager.class, priority = 1001)
@VersionedMixin({">=1.21.11", "<=1.21.11"})
public class ShadowMixinv12111m12111 {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitShadowPieces(Lnet/minecraft/client/util/math/MatrixStack;FLjava/util/List;)V"))
    private <S extends EntityRenderState> void wrapRenderInvokeSubmitShadow(OrderedRenderCommandQueue instance, MatrixStack matrices, float v, List<EntityRenderState.ShadowPiece> list, Operation<Void> original, @Local S state) {
        if (ModConfig.hideShadows && PlayerVisibility.computeHideInfoForEntityOrState(state).hide) {
            return;
        }

        original.call(instance, matrices, v, list);
    }
}