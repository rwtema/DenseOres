package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.DenseOre;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TE4Integration implements ModInterface {
    public static NBTTagCompound getItemStackNBT(ItemStack item, int newStackSize) {
        NBTTagCompound tag = ModIntegration.getItemStackNBT(item);
        tag.setByte("Count", (byte) newStackSize);
        return tag;
    }

    @Override
    public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
        addPulverizer(input, output);

        if (ModIntegration.isCanonOre(ore.baseOreDictionary) && ModIntegration.getFurnace(ore, 1) != null) {
            ItemStack slag = GameRegistry.findItemStack("ThermalExpansion", "slagRich", 1);
            String s = ModIntegration.getSecondCanonOre(ore.baseOreDictionary);
            if (s != null && ModIntegration.getSmeltedIngot(s, ore.modOwner) != null)
                slag = ModIntegration.cloneStack(ModIntegration.getSmeltedIngot(s, ore.modOwner), 3);

            addSmelter(input, new ItemStack(Blocks.sand, 4), ModIntegration.getFurnace(ore, 8.0F), slag, 25);
            addSmelter(input, GameRegistry.findItemStack("ThermalFoundation", "dustPyrotheum", 2), ModIntegration.getFurnace(ore, 8.0F), slag, 75);
            addSmelter(input, GameRegistry.findItemStack("ThermalFoundation", "crystalCinnabar", 2), ModIntegration.getFurnace(ore, 16.0F), slag, 100);
        }
    }

    private void addSmelter(ItemStack a, ItemStack b, ItemStack output, ItemStack altOutput, int prob) {
        if (a == null || b == null || output == null)
            return;

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("energy", 16000);
        tag.setTag("primaryInput", ModIntegration.getItemStackNBT(a));
        tag.setTag("secondaryInput", ModIntegration.getItemStackNBT(b));
        tag.setTag("primaryOutput", ModIntegration.getItemStackNBT(output));
        if (altOutput != null) {
            tag.setTag("secondaryOutput", ModIntegration.getItemStackNBT(altOutput));
            tag.setInteger("secondaryChance", prob);
        }
        FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", tag);
    }

    private void addPulverizer(ItemStack input, ItemStack output) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("energy", 8000);
        tag.setTag("input", ModIntegration.getItemStackNBT(input));
        tag.setTag("primaryOutput", getItemStackNBT(output, 4));
        FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", tag);
    }
}
