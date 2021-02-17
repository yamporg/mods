package io.github.yamporg.darkness;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DarknessMod.MOD_ID)
public class ConfigHandler {
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        String modID = event.getModID();
        if (!DarknessMod.MOD_ID.equals(modID)) {
            return;
        }
        ConfigManager.sync(modID, Config.Type.INSTANCE);
    }
}
