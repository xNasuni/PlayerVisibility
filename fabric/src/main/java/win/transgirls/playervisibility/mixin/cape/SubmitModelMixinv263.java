package win.transgirls.playervisibility.mixin.cape;

import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import win.transgirls.crossfabric.annotation.VersionedMixin;
import win.transgirls.playervisibility.PlayerVisibility;

@Mixin(BatchingRenderCommandQueue.class)
@VersionedMixin({">=26.3"})
public class SubmitModelMixinv263 {
    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private int applyModelTint(int tint) {
        return tint == -1 ? PlayerVisibility.modelTint : tint;
    }
}
