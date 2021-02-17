package io.github.yamporg.ifbhfix;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class FluidHandler extends FluidHandlerItemStack {
    public FluidHandler(@Nonnull ItemStack container) {
        super(container, Integer.MAX_VALUE);
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
    }

    @Override
    protected void setFluid(@Nonnull FluidStack fluid) {
        container.setTagCompound(fluid.writeToNBT(new NBTTagCompound()));
    }

    @Override
    protected void setContainerToEmpty() {
        container.setTagCompound(null);
    }
}
