package io.github.yamporg.darkness;

import net.minecraftforge.common.config.Config;

@Config(modid = ModContainer.MOD_ID, category = "main")
public final class ModConfig {
    public static boolean disableConfigGui = false;

    public static boolean darkOverworld = true;
    public static boolean darkNether = true;
    public static boolean darkEnd = true;
    public static boolean darkDefault = true;
    public static boolean darkSkyless = true;

    public static boolean darkNetherFog = true;
    public static boolean darkEndFog = true;

    public static boolean hardcore = false;
    public static float[] moonPhaseFactors = {};

    public static boolean invertBlacklist = false;
    public static int[] blacklistByID = {};
    public static String[] blacklistByName = {};
}
