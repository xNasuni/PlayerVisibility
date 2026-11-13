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
import win.transgirls.playervisibility.types.HideInfo;

import java.util.List;

@Mixin(value = EntityRenderManager.class, priority = 1001)
@VersionedMixin({">=1.21.11", "<=1.21.11"})
public class ShadowMixinv12111m12111 {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitShadowPieces(Lnet/minecraft/client/util/math/MatrixStack;FLjava/util/List;)V"))
    private <S extends EntityRenderState> void wrapRenderInvokeSubmitShadow(OrderedRenderCommandQueue instance, MatrixStack matrices, float v, List<EntityRenderState.ShadowPiece> list, Operation<Void> original, @Local S state) {
        if (ModConfig.hideShadows) {
            HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);

            if (info.hide) {
                if (info.alpha <= 0.0f) {
                    return;
                }

                list = list.stream().map(piece -> new EntityRenderState.ShadowPiece(piece.relativeX(), piece.relativeY(), piece.relativeZ(), piece.shapeBelow(), piece.alpha() * info.alpha)).toList();
            }
        }

        original.call(instance, matrices, v, list);
    }
}