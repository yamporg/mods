package io.github.yamporg.darkness.mixins;

import io.github.yamporg.darkness.DarknessConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow private Minecraft mc;
    @Shadow private boolean lightmapUpdateNeeded;
    @Shadow private float torchFlickerX;
    @Shadow private float bossColorModifier;
    @Shadow private float bossColorModifierPrev;
    @Shadow private int[] lightmapColors;

    private static final String UPDATE_DYNAMIC_TEXTURE =
            "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V";

    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = UPDATE_DYNAMIC_TEXTURE))
    private void onUpdateLightmap(float partialTicks, CallbackInfo ci) {
        if (!lightmapUpdateNeeded) {
            return;
        }
        World world = mc.world;
        if (world == null) {
            return;
        }

        // Use vanilla lightmap for night vision effect.
        if (mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
            return;
        }
        // Same for the lightning.
        if (world.getLastLightningBolt() > 0) {
            return;
        }
        // Check against dimension blacklist.
        if (blacklistDim(world.provider)) {
            return;
        }

        this.updateLuminance(partialTicks, world);
    }

    private static boolean blacklistDim(WorldProvider dim) {
        // Yet another special case for End.
        // Blacklist if darkness is disabled.
        DimensionType dimType = dim.getDimensionType();
        if (dimType == DimensionType.THE_END && !DarknessConfig.darkEnd) {
            return true;
        }
        return blacklistContains(dim, dimType) ^ DarknessConfig.invertBlacklist;
    }

    private static boolean blacklistContains(WorldProvider dim, DimensionType dimType) {
        String dimName = dimType.getName();
        for (String blacklistName : DarknessConfig.blacklistByName) {
            if (blacklistName != dimName) {
                continue;
            }
            return true;
        }

        int dimID = dim.getDimension();
        for (int blacklistID : DarknessConfig.blacklistByID) {
            if (dimID != blacklistID) {
                continue;
            }
            return true;
        }

        return false;
    }

    private static boolean isDark(WorldProvider dim, DimensionType dimType) {
        if (dimType == DimensionType.OVERWORLD) {
            return DarknessConfig.darkOverworld;
        } else if (dimType == DimensionType.NETHER) {
            return DarknessConfig.darkNether;
        } else if (dimType == DimensionType.THE_END) {
            return DarknessConfig.darkEnd;
        } else if (dim.hasSkyLight()) {
            return DarknessConfig.darkDefault;
        } else {
            return DarknessConfig.darkSkyless;
        }
    }

    private static float getMoonBrightness(float partialTicks, World world) {
        WorldProvider dim = world.provider;
        DimensionType dimType = dim.getDimensionType();

        if (!isDark(dim, dimType)) {
            return 1f;
        }

        if (!dim.hasSkyLight()) {
            return 0f;
        }

        final float angle = world.getCelestialAngle(partialTicks);
        if (angle <= 0.25f || 0.75f <= angle) {
            return 1f;
        }

        final float moon;
        if (!DarknessConfig.hardcore) {
            final int moonPhase = dim.getMoonPhase(world.getWorldTime());
            final float[] phaseFactors = DarknessConfig.moonPhaseFactors;
            if (moonPhase < phaseFactors.length) {
                moon = phaseFactors[moonPhase];
            } else {
                moon = world.getCurrentMoonPhaseFactor();
            }
        } else {
            // Hardcore pitch-black nights.
            moon = 0f;
        }

        final float w;
        if (angle <= 0.3f || 0.7f <= angle) {
            w = 20 * (Math.abs(angle - 0.5f) - 0.2f);
        } else {
            w = 0;
        }
        return lerp(w * w, moon, 1f);
    }

    private void updateLuminance(float partialTicks, World world) {
        WorldProvider dim = world.provider;
        DimensionType dimType = dim.getDimensionType();

        // Light to brightness float[16] conversion table.
        float[] brightnessTable = dim.getLightBrightnessTable();

        boolean dimDark = isDark(dim, dimType);

        float sunBrightness = world.getSunBrightness(1.0F);
        float moonBrightness = getMoonBrightness(partialTicks, world);

        for (int i = 0; i < 256; ++i) {
            int skyIndex = i / 16;
            int blockIndex = i % 16;

            float skyFactor = 1f - skyIndex / 15f;
            skyFactor = 1 - skyFactor * skyFactor * skyFactor * skyFactor;
            skyFactor *= moonBrightness;

            float min = skyFactor * 0.05f;
            final float rawAmbient = sunBrightness * skyFactor;
            final float minAmbient = rawAmbient * (1 - min) + min;
            final float skyBase = brightnessTable[skyIndex] * minAmbient;

            min = 0.35f * skyFactor;
            float skyRed = skyBase * (rawAmbient * (1 - min) + min);
            float skyGreen = skyBase * (rawAmbient * (1 - min) + min);
            float skyBlue = skyBase;

            if (this.bossColorModifier > 0.0F) {
                float d = this.bossColorModifier - this.bossColorModifierPrev;
                float m = this.bossColorModifierPrev + partialTicks * d;
                skyRed = skyRed * (1.0F - m) + skyRed * 0.7F * m;
                skyGreen = skyGreen * (1.0F - m) + skyGreen * 0.6F * m;
                skyBlue = skyBlue * (1.0F - m) + skyBlue * 0.6F * m;
            }

            float blockFactor = 1f;
            if (dimDark) {
                blockFactor = 1f - blockIndex / 15f;
                blockFactor = 1 - blockFactor * blockFactor * blockFactor * blockFactor;
            }

            final float flicker = this.torchFlickerX * 0.1F + 1.5F;
            final float blockBase = blockFactor * brightnessTable[blockIndex] * flicker;
            min = 0.4f * blockFactor;

            final float blockGreen = blockBase * ((blockBase * (1 - min) + min) * (1 - min) + min);
            final float blockBlue = blockBase * (blockBase * blockBase * (1 - min) + min);

            float red = skyRed + blockBase;
            float green = skyGreen + blockGreen;
            float blue = skyBlue + blockBlue;

            final float f = Math.max(skyFactor, blockFactor);
            min = 0.03f * f;
            red = red * (0.99F - min) + min;
            green = green * (0.99F - min) + min;
            blue = blue * (0.99F - min) + min;

            if (dimType == DimensionType.THE_END) {
                red = skyFactor * 0.22F + blockBase * 0.75f;
                green = skyFactor * 0.28F + blockGreen * 0.75f;
                blue = skyFactor * 0.25F + blockBlue * 0.75f;
            }

            red = MathHelper.clamp(red, 0f, 1f);
            green = MathHelper.clamp(green, 0f, 1f);
            blue = MathHelper.clamp(blue, 0f, 1f);

            final float gamma = mc.gameSettings.gammaSetting * f;
            float invRed = 1.0F - red;
            float invGreen = 1.0F - green;
            float invBlue = 1.0F - blue;
            invRed = 1.0F - invRed * invRed * invRed * invRed;
            invGreen = 1.0F - invGreen * invGreen * invGreen * invGreen;
            invBlue = 1.0F - invBlue * invBlue * invBlue * invBlue;
            red = red * (1.0F - gamma) + invRed * gamma;
            green = green * (1.0F - gamma) + invGreen * gamma;
            blue = blue * (1.0F - gamma) + invBlue * gamma;

            min = 0.03f * f;
            red = red * (0.99F - min) + min;
            green = green * (0.99F - min) + min;
            blue = blue * (0.99F - min) + min;

            red = MathHelper.clamp(red, 0f, 1f);
            green = MathHelper.clamp(green, 0f, 1f);
            blue = MathHelper.clamp(blue, 0f, 1f);

            float lTarget = luminance(red, green, blue);
            int c = this.lightmapColors[i];
            this.lightmapColors[i] = darken(c, lTarget);
        }
    }

    private static int darken(int c, float lTarget) {
        final float r = (c & 0xFF) / 255f;
        final float g = ((c >> 8) & 0xFF) / 255f;
        final float b = ((c >> 16) & 0xFF) / 255f;
        final float l = luminance(r, g, b);
        if (l <= 0f) {
            return c;
        }
        if (lTarget >= l) {
            return c;
        }
        final float f = lTarget / l;
        c = 0xFF000000;
        c |= Math.round(f * r * 255);
        c |= Math.round(f * g * 255) << 8;
        c |= Math.round(f * b * 255) << 16;
        return c;
    }

    private static float luminance(float r, float g, float b) {
        // See https://en.wikipedia.org/wiki/Relative_luminance
        return r * 0.2126f + g * 0.7152f + b * 0.0722f;
    }

    private static float lerp(float f, float g, float h) {
        return g + f * (h - g);
    }
}
