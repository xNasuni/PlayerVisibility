package win.transgirls.playervisibility.mixin.entity;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.crossfabric.tools.ClassUtils;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.HideInfo;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Mixin(value = LivingEntityRenderer.class, priority = 1001)
@VersionedMixin({">=1.21.11", "<=1.21.11"})
public class LivingEntityRendererMixinv12111m12111 {
    @Unique
    private MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void injectRender(@Coerce Object state, MatrixStack matrices, OrderedRenderCommandQueue renderQueue, CameraRenderState cameraState, CallbackInfo ci, @Share("state") LocalRef<Object> stateRef) throws Throwable {
        stateRef.set(state);

        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);
        if (info.hide && info.alpha == 0.0f) {
            ci.cancel();

            if (!ModConfig.hideNametags) {
                MethodHandle superRender = ClassUtils.unreflectFirstDeclaredMethodWithName(lookup, EntityRenderer.class, LivingEntityRenderer.class, List.of("method_3936"), EntityRenderState.class, MatrixStack.class, OrderedRenderCommandQueue.class, CameraRenderState.class);

                superRender.invoke(this, state, matrices, renderQueue, cameraState);
            }
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
            ),
            index = 6
    )
    private int injectAlpha(int color, @Share("state") LocalRef<Object> stateRef) {
        Object state = stateRef.get();

        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);
        if (info.hide) {
            return PlayerVisibility.withAlphaFloat(color, info.alpha);
        }

        return color;
    }
}