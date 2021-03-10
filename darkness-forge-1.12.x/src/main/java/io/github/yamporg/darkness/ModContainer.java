package io.github.yamporg.darkness;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModContainer extends DummyModContainer {
    public static final String MOD_ID = "darkness";
    public static final String MOD_NAME = "Darkness";

    public ModContainer() {
        super(Workarounds.parseMetadata());
    }

    @Override
    public File getSource() {
        return LoadingPlugin.FILE_LOCATION;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        if (getSource().isDirectory()) {
            return FMLFolderResourcePack.class;
        } else {
            return FMLFileResourcePack.class;
        }
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
    public void onModConstruction(FMLConstructionEvent event) {
        Workarounds.injectConfig();
        ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!MOD_ID.equals(event.getModID())) {
            return;
        }
        ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
    }
}
