package io.github.yamporg.darkness.mixins;

import io.github.yamporg.darkness.DarknessConfig;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProviderEnd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProviderEnd.class)
public class MixinWorldProviderEnd {
    @Inject(method = "getFogColor", at = @At(value = "HEAD"), cancellable = true)
    private void getFogColor(float angle, float partialTicks, CallbackInfoReturnable<Vec3d> cir) {
        if (!DarknessConfig.darkEndFog) {
            return;
        }
        cir.setReturnValue(Vec3d.ZERO);
    }
}
