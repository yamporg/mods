package io.github.yamporg.darkness.asm;

import io.github.yamporg.darkness.ModConfig;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;

public final class WorldProviderHooks {
    public static Vec3d onGetFogColor(WorldProvider provider, float angle, float partialTicks) {
        if (!ModConfig.darkEndFog && provider instanceof WorldProviderEnd) {
            return null;
        }
        if (!ModConfig.darkNetherFog && provider instanceof WorldProviderHell) {
            return null;
        }
        return Vec3d.ZERO;
    }
}
