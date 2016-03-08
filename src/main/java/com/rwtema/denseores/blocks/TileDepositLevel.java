package com.rwtema.denseores.blocks;

import com.rwtema.denseores.utils.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileDepositLevel extends TileEntity {
    public static Random random = new Random();
    public int num = random.nextInt(1 + random.nextInt(32)) + 8;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Num", num);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        num = compound.getInteger("Num");
    }

    public boolean dec() {
        LogHelper.debug(num);
        num--;
        markDirty();
        return num > 0;
    }
}
