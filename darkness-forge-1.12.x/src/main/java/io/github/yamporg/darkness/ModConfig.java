package io.github.yamporg.darkness;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

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

    /**
     * Inject ModConfig into classes known to ConfigManager. This is a terrible hack. We need this
     * because I couldn’t find a better way to use @Config annotation with coremod that injects mod
     * container. Note that we intentionally use reflection instead of public loadData API because
     * for some reason Forge thinks that it’s OK to behave differently in development environment.
     * That is, ModConfig already exists in (and only in) development environment, and we don’t want
     * to insert it twice, otherwise horrible things may happen (and I don’t want to debug them).
     */
    public static void inject() {
        final Map<String, Multimap<Config.Type, ASMDataTable.ASMData>> asmData;
        try {
            Field f = ConfigManager.class.getDeclaredField("asm_data");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            final Map<String, Multimap<Config.Type, ASMDataTable.ASMData>> v =
                    (Map<String, Multimap<Config.Type, ASMDataTable.ASMData>>) f.get(null);
            asmData = v;
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return;
        }
        asmData.computeIfAbsent(
                ModContainer.MOD_ID,
                k -> {
                    Multimap<Config.Type, ASMDataTable.ASMData> map = ArrayListMultimap.create();
                    map.put(
                            Config.Type.INSTANCE,
                            new ASMDataTable.ASMData(
                                    null,
                                    null,
                                    ModConfig.class.getName(),
                                    null,
                                    new HashMap<String, Object>() {
                                        {
                                            put("category", "main");
                                        }
                                    }));
                    return map;
                });
    }
}
