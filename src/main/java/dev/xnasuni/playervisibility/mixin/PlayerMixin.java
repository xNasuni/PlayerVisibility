package dev.xnasuni.playervisibility.mixin;

import dev.xnasuni.playervisibility.PlayerVisibility;
import dev.xnasuni.playervisibility.util.ArrayListUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerMixin {

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void InjectRender(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        boolean ShouldShowPlayer = PlayerVisibility.IsVisible();

        if (abstractClientPlayerEntity.getName().getString().equalsIgnoreCase(PlayerVisibility.Minecraft.player.getName().getString())) {
            ShouldShowPlayer = true;
        }

        if (ArrayListUtil.ContainsLowercase(PlayerVisibility.GetWhitelistedPlayers(), abstractClientPlayerEntity.getName().getString())) {
            ShouldShowPlayer = true;
        }

        if (!PlayerVisibility.IsVisible() && !ShouldShowPlayer) {
            ci.cancel();
        }
    }

}
