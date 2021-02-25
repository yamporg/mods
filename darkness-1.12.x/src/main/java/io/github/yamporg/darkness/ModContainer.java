package io.github.yamporg.darkness;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.InputStream;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModContainer extends DummyModContainer {
    public static final String MOD_ID = "darkness";
    public static final String MOD_NAME = "Darkness";
    public static final ModMetadata MOD_METADATA;

    static {
        InputStream is = ModContainer.class.getResourceAsStream("/mcmod.info");
        MetadataCollection mc = MetadataCollection.from(is, MOD_ID);
        MOD_METADATA = mc.getMetadataForId(MOD_ID, null);
    }

    public ModContainer() {
        super(MOD_METADATA);
    }

    @Override
    public String getGuiClassName() {
        return GuiFactory.class.getName();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent event) {
        ConfigManager.sync(getModId(), Config.Type.INSTANCE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        String modID = event.getModID();
        if (!MOD_ID.equals(modID)) {
            return;
        }
        ConfigManager.sync(modID, Config.Type.INSTANCE);
    }
}
