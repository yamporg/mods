package io.github.yamporg.noprecipebook;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.RecipeBookServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ModContainer.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
@Mod.EventBusSubscriber
public final class ModContainer {
    public static final String MOD_ID = "noprecipebook";

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent ev) {
        Entity e = ev.getEntity();
        if (!(e instanceof EntityPlayerMP)) {
            return;
        }
        ((EntityPlayerMP) e).recipeBook = RECIPE_BOOK_INSTANCE;
    }

    private static final RecipeBookServer RECIPE_BOOK_INSTANCE =
            new RecipeBookServer() {
                @Override
                public void init(@Nullable EntityPlayerMP player) {}

                @Override
                public void copyFrom(@Nullable RecipeBook that) {}

                @Override
                public void add(
                        @Nullable List<IRecipe> recipesIn, @Nullable EntityPlayerMP player) {}

                @Override
                public void remove(
                        @Nullable List<IRecipe> recipes, @Nullable EntityPlayerMP player) {}

                @Override
                public void read(@Nullable NBTTagCompound tag) {}

                @Override
                @Nonnull
                public NBTTagCompound write() {
                    return new NBTTagCompound();
                }

                @Override
                public void markSeen(@Nullable IRecipe recipe) {}

                @Override
                public void markNew(@Nullable IRecipe recipe) {}

                @Override
                public boolean isNew(@Nullable IRecipe recipe) {
                    return false;
                }

                @Override
                public void setGuiOpen(boolean open) {}

                @Override
                public boolean isGuiOpen() {
                    return false;
                }

                @Override
                public void unlock(@Nullable IRecipe recipe) {}

                @Override
                public void lock(@Nullable IRecipe recipe) {}

                @Override
                public boolean isUnlocked(@Nullable IRecipe recipe) {
                    return true;
                }

                @Override
                public void setFilteringCraftable(boolean shouldFilter) {}

                @Override
                public boolean isFilteringCraftable() {
                    return false;
                }
            };
}
