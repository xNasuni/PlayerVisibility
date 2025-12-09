package win.transgirls.playervisibility.mixin.entity;

/*import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.TransparentVertexConsumerProvider;
import static win.transgirls.playervisibility.PlayerVisibility.transparency;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.15", "<=1.21.1"})*/
public abstract class EntityMixinv115m1211 {
    /*@Shadow public abstract double getSquaredDistanceToCamera(Entity entity);

    @WrapMethod(method = "(Lnet/minecraft/class_1297;DDDFFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V")
    private <E extends Entity> void wrapRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
        transparency.put(entity, -1);

        boolean shouldHide = (entity instanceof PlayerEntity && ModConfig.hidePlayers) || (!(entity instanceof PlayerEntity) && ModConfig.hideEntities);

        if (shouldHide && PlayerVisibility.shouldHideEntity(entity)) {
            if (ModConfig.comfortZone) {
                double sqDst = this.getSquaredDistanceToCamera(entity);
                double distance = Math.sqrt(sqDst);

                if (distance <= ModConfig.comfortDistance + ModConfig.comfortFalloff) {
                    double transitionStart = ModConfig.comfortDistance; // 🏳️‍⚧️ :3
                    double transitionEnd = ModConfig.comfortDistance + ModConfig.comfortFalloff;
                    double falloffAmount = Math.min(Math.max((distance - transitionStart) / (transitionEnd - transitionStart), 0), 1);

                    int falloff = (int) (falloffAmount * 255);
                    transparency.put(entity, falloff);
                }

                if (transparency.get(entity) <= 0 && transparency.get(entity) != -1) {
                } else {
                    original.call(entity, x, y, z, yaw, tickDelta, matrices, new TransparentVertexConsumerProvider<E>(vertexConsumers, entity), light);
                }
            }
            return;
        }

        original.call(entity, x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
    }*/
}