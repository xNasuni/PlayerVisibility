package win.transgirls.playervisibility.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.HideInfo;

@Mixin(value = LivingEntityRenderer.class, priority = 1001)
@VersionedMixin({">=1.21", "<=1.21.1"})
public class LivingEntityRendererMixinv121m1211<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow
    protected M model;

    protected LivingEntityRendererMixinv121m1211(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "method_4054(Lnet/minecraft/class_1309;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V", at = @At("HEAD"), cancellable = true)
    private void injectRender(T entity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(entity);
        if (info.hide && info.alpha == 0.0f) {
            ci.cancel();

            if (!ModConfig.hideNametags) {
                super.render(entity, f, g, matrices, vertexConsumers, light);
            }
        }
    }

    @ModifyArg(
            method = "method_4054(Lnet/minecraft/class_1309;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_583;method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V"
            ),
            index = 4
    )
    private int injectAlpha(int color, @Local(argsOnly = true) T entity) {
        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(entity);
        if (info.hide) {
            return PlayerVisibility.withAlphaFloat(color, info.alpha);
        }

        return color;
    }

    @Override
    public M getModel() {
        return model;
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}