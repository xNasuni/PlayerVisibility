package win.transgirls.playervisibility.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.HideInfo;

@Mixin(value = LivingEntityRenderer.class, priority = 1001)
@VersionedMixin({">=26.1", "<=26.2"})
public abstract class LivingEntityRendererMixinv261m262<T extends LivingEntity, S extends LivingEntityRenderState> extends EntityRenderer<T, S> {
    protected LivingEntityRendererMixinv261m262(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void injectRender(@Coerce Object state, MatrixStack matrices, OrderedRenderCommandQueue renderQueue, CameraRenderState cameraState, CallbackInfo ci, @Share("state") LocalRef<Object> stateRef) throws Throwable {
        stateRef.set(state);

        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);
        if (info.hide && info.alpha == 0.0f) {
            ci.cancel();

            if (!ModConfig.hideNametags) {
                super.render((S) state, matrices, renderQueue, cameraState);
            }
        }
    }

    @Shadow
    public abstract Identifier getTexture(LivingEntityRenderState state);

    @ModifyReturnValue(method = "getRenderLayer", at = @At("RETURN"))
    private RenderLayer fadeableLayer(RenderLayer original, LivingEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (original == null || !showBody || translucent) {
            return original;
        }

        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);
        return info.hide && info.alpha < 1.0f ? RenderLayers.entityTranslucent(getTexture(state)) : original;
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"), index = 6)
    private int injectAlpha(int color, @Share("state") LocalRef<Object> stateRef) {
        Object state = stateRef.get();

        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);
        if (info.hide) {
            return PlayerVisibility.withAlphaFloat(color, info.alpha);
        }

        return color;
    }
}