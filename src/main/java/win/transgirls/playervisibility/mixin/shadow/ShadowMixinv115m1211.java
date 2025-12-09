package win.transgirls.playervisibility.mixin.shadow;

/*import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.21.1"})*/
public class ShadowMixinv115m1211 {
    /*@Inject(method = "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_1297;FFLnet/minecraft/class_4538;F)V", at = @At("HEAD"), cancellable = true)
    private static void injectShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        if (ModConfig.hideShadows && PlayerVisibility.shouldHideEntity(entity)) {
            ci.cancel();
        }
    }*/
}