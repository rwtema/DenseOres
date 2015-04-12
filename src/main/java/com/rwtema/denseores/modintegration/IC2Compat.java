package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.DenseOre;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class IC2Compat implements ModInterface {

    private static boolean ic2APIPresent;

    static {
        try {
            Class.forName("ic2.api.recipe.Recipes");
            ic2APIPresent = true;
        } catch (ClassNotFoundException e) {
            ic2APIPresent = false;
        }
    }

    @Override
    public void registerOre(DenseOre ore, ItemStack denseOre, ItemStack originalOre) {
        if (ic2APIPresent)
            registerOre_do(ore, denseOre, originalOre);
    }

    private static void registerOre_do(DenseOre ore, ItemStack denseOre, ItemStack originalOre) {
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
