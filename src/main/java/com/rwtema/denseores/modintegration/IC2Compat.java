package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.DenseOre;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class IC2Compat implements ModInterface {


    @Override
    public void registerOre(DenseOre ore, ItemStack denseOre, ItemStack originalOre) {
        if (Loader.isModLoaded("IC2"))
            registerOre_do(ore, denseOre, originalOre);
    }

    @Method(modid = "IC2")
    private void registerOre_do(DenseOre ore, ItemStack denseOre, ItemStack originalOre) {
        if (Recipes.scrapboxDrops == null || Recipes.macerator == null)
            return;

        Map<ItemStack, Float> drops = Recipes.scrapboxDrops.getDrops();

        for (Map.Entry<ItemStack, Float> itemStackFloatEntry : drops.entrySet()) {
            if (ModIntegration.simpleItemStackMatch(itemStackFloatEntry.getKey(), originalOre)) {
                Recipes.scrapboxDrops.addDrop(denseOre.copy(), itemStackFloatEntry.getValue() / 10.0F);
                break;
            }
        }

        ItemStack result = originalOre.copy();
        result.stackSize = 4;
        Recipes.macerator.addRecipe(new RecipeInputItemStack(denseOre), null, result);
    }
}
