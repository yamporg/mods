package io.github.yamporg.ifbhfix;

import com.buuz135.industrial.tile.block.BlackHoleUnitBlock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockStorageItem extends BlackHoleUnitBlock.BlockStorageItem {
    public BlockStorageItem(Block block) {
        ((BlackHoleUnitBlock) block).super(block);
    }

    @Nullable
    @Override
    public String getCreatorModId(@Nonnull ItemStack itemStack) {
        return IFBHFixMod.MOD_ID;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable NBTTagCompound nbt) {
        return ((BlackHoleUnitBlock) block).new StorageItemHandler(itemStack) {
            @Nullable
            @Override
            public <T> T getCapability(
                    @Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                Capability<IItemHandler> itemHandlerCapability =
                        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
                if (capability != itemHandlerCapability) {
                    return null;
                }
                IItemHandler itemHandler = super.getCapability(itemHandlerCapability, facing);
                IItemHandler wrapper = new ItemHandlerWrapper(itemStack, itemHandler);
                return itemHandlerCapability.cast(wrapper);
            }
        };
    }
}
