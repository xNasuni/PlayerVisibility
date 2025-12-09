package win.transgirls.playervisibility.mixin.entity;

/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;
import win.transgirls.playervisibility.config.ModConfig;
import win.transgirls.playervisibility.types.TransparentVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static win.transgirls.playervisibility.PlayerVisibility.transparency;

@Mixin(value = EntityRenderDispatcher.class, priority = 1001)
@VersionedMixin({">=1.21.5"})*/
public abstract class EntityMixinv1215 {
    /*@Shadow public abstract double getSquaredDistanceToCamera(Entity entity);

    @WrapMethod(method = "(Lnet/minecraft/class_1297;DDDFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_897;)V")
    private <E extends Entity> void wrapRender(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<?> renderer, Operation<Void> original) {
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
                    original.call(entity, x, y, z, tickProgress, matrices, new TransparentVertexConsumerProvider<E>(vertexConsumers, entity), light, renderer);
                }
            }
            return;
        }
        original.call(entity, x, y, z, tickProgress, matrices, vertexConsumers, light, renderer);
    }*/
}
