package win.transgirls.playervisibility.mixin.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.transgirls.playervisibility.PlayerVisibility;

import java.util.Objects;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "shouldRenderFeatures(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Z", at = @At("RETURN"), cancellable = true)
    private void injectFeatures(PlayerEntityRenderState state, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerVisibility.shouldHideEntityRenderState(state)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("RETURN"))
    private <AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> void injectPlayerName(AvatarlikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (Objects.equals(playerLikeEntity.getName().getString(), MinecraftClient.getInstance().getSession().getUsername())) {
            playerEntityRenderState.playerName = Text.of(MinecraftClient.getInstance().getSession().getUsername());
        }
    }
}
