package win.transgirls.playervisibility.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
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
@VersionedMixin({">=1.21.2", "<=1.21.8"})
public class LivingEntityRendererMixinv1212m1218 {
    @Unique
    private MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Inject(method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V", at = @At("HEAD"), cancellable = true)
    private void injectRender(@Coerce Object state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci, @Share("state") LocalRef<Object> stateRef) throws Throwable {
        stateRef.set(state);

        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(state);
        if (info.hide && info.alpha == 0.0f) {
            ci.cancel();

            if (!ModConfig.hideNametags) {
                Class<?> entityRenderState = ClassUtils.forName("net.minecraft.class_10017");
                MethodHandle superRender = ClassUtils.unreflectFirstDeclaredMethodWithName(lookup, EntityRenderer.class, LivingEntityRenderer.class, List.of("method_3936"), entityRenderState, MatrixStack.class, VertexConsumerProvider.class, int.class);

                Object renderer = (Object) this;
                superRender.invoke(renderer, state, matrices, vertexConsumers, light);
            }
        }
    }

    @ModifyArg(
            method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_583;method_62100(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V"
            ),
            index = 4
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