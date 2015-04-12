package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.DenseOre;
import net.minecraft.item.ItemStack;

public interface ModInterface {
    public void registerOre(DenseOre ore, ItemStack denseOre, ItemStack originalOre);
}
