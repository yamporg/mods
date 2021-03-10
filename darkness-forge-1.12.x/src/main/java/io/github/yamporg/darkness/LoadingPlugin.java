package io.github.yamporg.darkness;

import io.github.yamporg.darkness.asm.EntityRendererTransformer;
import io.github.yamporg.darkness.asm.WorldProviderTransformer;
import java.io.File;
import java.util.Map;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(ModContainer.MOD_ID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(LoadingPlugin.AFTER_DEOBF)
public final class LoadingPlugin implements IFMLLoadingPlugin {
    /**
     * Load after net.minecraftforge.fml.common.launcher.FMLDeobfTweaker with sorting index 1000.
     * https://github.com/MinecraftForge/MinecraftForge/blob/a8b9abcb17e28007ed5f5e110997be8e499575e5/src/main/java/net/minecraftforge/fml/relauncher/CoreModManager.java#L633
     */
    public static final int AFTER_DEOBF = 1001;

    public static Boolean RUNTIME_DEOBF;
    public static File FILE_LOCATION;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            WorldProviderTransformer.class.getName(), EntityRendererTransformer.class.getName(),
        };
    }

    @Override
    public String getModContainerClass() {
        return ModContainer.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        RUNTIME_DEOBF = (Boolean) data.get("runtimeDeobfuscationEnabled");
        FILE_LOCATION = (File) data.get("coremodLocation");

        // Yet another hack for dev environment.
        if (FILE_LOCATION == null) {
            FILE_LOCATION =
                    new File(
                            getClass()
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .getPath());
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
