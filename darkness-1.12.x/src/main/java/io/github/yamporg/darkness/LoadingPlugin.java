package io.github.yamporg.darkness;

import io.github.yamporg.darkness.asm.EntityRendererTransformer;
import io.github.yamporg.darkness.asm.WorldProviderTransformer;
import java.util.Map;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(ModContainer.MOD_ID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public final class LoadingPlugin implements IFMLLoadingPlugin {
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
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
