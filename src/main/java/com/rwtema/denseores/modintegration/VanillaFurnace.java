package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.DenseOre;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class VanillaFurnace implements ModInterface {

    @Override
    public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
        ItemStack out = ModIntegration.getFurnace(ore, 3F);
        if (out != null) {
            GameRegistry.addSmelting(input, out, 1.0F);
        }
    }
}
