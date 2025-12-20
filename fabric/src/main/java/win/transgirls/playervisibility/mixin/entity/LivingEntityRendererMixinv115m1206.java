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

import java.lang.reflect.InvocationTargetException;

@Mixin(value = LivingEntityRenderer.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.20.6"})
public class LivingEntityRendererMixinv115m1206<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow
    protected M model;

    protected LivingEntityRendererMixinv115m1206(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void injectRender(T entity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(entity);
        if (info.hide && info.alpha == 0.0f) {
            ci.cancel();

            if (!ModConfig.hideNametags) {
                super.render(entity, f, g, matrices, vertexConsumers, light);
            }
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 7
    )
    private float injectAlpha(float alpha, @Local(argsOnly = true) T entity) {
        HideInfo info = PlayerVisibility.computeHideInfoForEntityOrState(entity);
        if (info.hide) {
            return info.alpha;
        }

        return alpha;
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