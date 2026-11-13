package win.transgirls.playervisibility.mixin.cape;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.types.HideInfo;

@Mixin(value = CapeFeatureRenderer.class, priority = 1001)
@VersionedMixin({">=1.21.11", "<=1.21.11"})
public class CapeMixinv12111m12111 {
    @WrapOperation(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"))
    private <S> void fadeCape(OrderedRenderCommandQueue instance, Model<? super S> model, S modelState, MatrixStack matrices, RenderLayer layer, int light, int overlay, int outlineColor, ModelCommandRenderer.CrumblingOverlayCommand crumbling, Operation<Void> original, @Local(argsOnly = true) PlayerEntityRenderState state) {
        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);

        if (!info.hide || info.alpha >= 1.0f) {
            original.call(instance, model, modelState, matrices, layer, light, overlay, outlineColor, crumbling);
            return;
        }

        RenderLayer translucent = RenderLayers.entityTranslucent(state.skinTextures.cape().texturePath());
        instance.submitModel(model, modelState, matrices, translucent, light, overlay, PlayerVisibility.withAlphaFloat(-1, info.alpha), null, outlineColor, crumbling);
    }
}
