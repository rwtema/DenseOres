package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.DenseOre;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("unchecked")
public class MFRCompat implements ModInterface {

    private static boolean success;
    private static Method getLaserOres;
    private static Method registerLaserOre;
    private static Method getStack;
    private static Method getLaserPreferredOres;
    private static Method addLaserPreferredOre;

    static {
        try {
            String MFRRegistry = "powercrystals.minefactoryreloaded.MFRRegistry";

            getLaserOres = Reflections.getMethodFromClass(MFRRegistry, "getLasterOres");
            registerLaserOre = Reflections.getMethodFromClass(MFRRegistry, "registerLaserOre", int.class, ItemStack.class);
            getLaserPreferredOres = Reflections.getMethodFromClass(MFRRegistry, "getLaserPreferredOres", int.class);
            addLaserPreferredOre = Reflections.getMethodFromClass(MFRRegistry, "addLaserPreferredOre", int.class, ItemStack.class);
            getStack = Reflections.getMethodFromClass("cofh.lib.util.WeightedRandomItemStack", "getStack");

            success = true;
        } catch (ReflectionHelper.UnableToFindMethodException error) {
            success = false;
        }
    }

    @Override
    public void registerOre(DenseOre ore, ItemStack denseOre, ItemStack originalOre) {
        if (success) {
            if (Loader.isModLoaded("MineFactoryReloaded"))
                registerOre_do(ore, denseOre, originalOre);
            else
                success = false;
        }
    }

    private void registerOre_do(DenseOre ore, ItemStack denseOre, ItemStack originalOre) {
        try {
            int weight = -1;
            List<WeightedRandom.Item> items = (List<WeightedRandom.Item>) getLaserOres.invoke(null);
            for (WeightedRandom.Item item : items) {
                ItemStack stack = (ItemStack) getStack.invoke(item);
                if (ModIntegration.simpleItemStackMatch(originalOre, stack)) {
                    weight = item.itemWeight;
                }

                if (ModIntegration.simpleItemStackMatch(denseOre, stack)) {
                    return;
                }
            }

            if (weight == -1)
                return;

            weight = Math.max(1, weight / 10);

            ItemStack denseStack = denseOre.copy();

            registerLaserOre.invoke(null, weight, denseStack);

            for (int i = 0; i < 16; i++) {
                List<ItemStack> itemStacks = ((List<ItemStack>) getLaserPreferredOres.invoke(null, i));
                if (itemStacks == null)
                    continue;

                for (ItemStack itemStack : itemStacks) {
                    if (ModIntegration.simpleItemStackMatch(originalOre, itemStack)) {
                        addLaserPreferredOre.invoke(null, i, denseStack);
                        return;
                    }
                }
            }
        } catch (IllegalAccessException ignored) { // These exceptions should never fire
            success = false;
        } catch (InvocationTargetException ignored) {
            success = false;
        }
    }

}
