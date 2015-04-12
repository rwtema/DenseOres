package com.rwtema.denseores.modintegration;

import com.rwtema.denseores.BlockDenseOre;
import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

public class ModIntegration {
    public static final String[] canonOres = {"oreIron", "oreGold", "oreCopper", "oreTin", "oreSilver", "oreLead", "oreNickel", "orePlatinum"};
    public static final String[] canonSecondaryOres = {"oreNickel", null, "oreGold", "oreIron", "oreLead", "oreSilver", "orePlatinum", null};
    public static final Random rand = new Random();
    public static ModInterface[] mods = {new VanillaFurnace(), new TE4Integration()};
    public static ModInterface[] lastMinutemods = {new ThaumcraftCompat(), new MFRCompat(), new IC2Compat()};

    public static boolean isCanonOre(String ore) {
        for (String s : canonOres)
            if (s.equals(ore))
                return true;
        return false;
    }

    public static String getSecondCanonOre(String ore) {
        for (int i = 0; i < canonOres.length; i++)
            if (canonOres[i].equals(ore))
                return canonSecondaryOres[i];
        return null;
    }

    public static NBTTagCompound getItemStackNBT(ItemStack item) {
        NBTTagCompound tag = new NBTTagCompound();
        item.writeToNBT(tag);
        return tag;
    }

    public static ItemStack cloneStack(ItemStack item, int newStackSize) {
        ItemStack newitem = item.copy();
        newitem.stackSize = newStackSize;
        return newitem;
    }

    public static boolean isOreSmeltsToIngot(String oreDict) {
        if ("".equals(oreDict))
            return false;
        String ingotName = "ingot" + oreDict.substring("ore".length());

        return !OreDictionary.getOres(ingotName).isEmpty();
    }

    public static ItemStack getSmeltedIngot(String oreDict, String preferredModOwner) {
        if ("".equals(oreDict))
            return null;
        String ingotName = "ingot" + oreDict.substring("ore".length());

        ItemStack out = null;

        for (ItemStack ingot : OreDictionary.getOres(ingotName)) {
            out = ingot;

            String s = GameData.getItemRegistry().getNameForObject(ingot.getItem());
            if (preferredModOwner != null && preferredModOwner.equals(s.substring(0, s.indexOf(58)))) {
                return out;
            }
        }

        return out;
    }

    public static ItemStack getFurnace(DenseOre toSmelt, float multiplier) {
        ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(toSmelt.newStack(1));

        if (out == null && isOreSmeltsToIngot(toSmelt.baseOreDictionary)) {
            out = getSmeltedIngot(toSmelt.baseOreDictionary, toSmelt.modOwner);
        }

        if (out != null) {
            out = out.copy();

            if ("minecraft:lapis_ore".equals(toSmelt.baseBlock))
                out.stackSize = 6;
            else if ("minecraft:redstone_ore".equals(toSmelt.baseBlock))
                out.stackSize = 4;

            out.stackSize = (int) (out.stackSize * multiplier);
            if (out.stackSize > 64)
                out.stackSize = 64;
            else if (out.stackSize < 1)
                out.stackSize = 1;
        }
        return out;
    }

    public static void addModIntegration() {
        addModIntegration(mods);
    }

    public static void addModIntegration(ModInterface[] mods) {
        for (DenseOre ore : DenseOresRegistry.ores.values()) {
            int bId = ore.id / 16;
            int meta = ore.id % 16;
            final BlockDenseOre blockDenseOre = DenseOresRegistry.blocks.get(bId);
            if (blockDenseOre.isValid(meta)) {
                ItemStack output = new ItemStack(blockDenseOre.getBlock(meta), 1, ore.metadata);
                ItemStack input = new ItemStack(blockDenseOre, 1, meta);

                for (ModInterface mod : mods) {
                    mod.registerOre(ore, input, output);
                }
            }
        }
    }

    public static void addLateModIntegration() {
        addModIntegration(lastMinutemods);
    }

    public static boolean simpleItemStackMatch(ItemStack originalOre, ItemStack stack) {
        return stack.getItem() == originalOre.getItem()
                && stack.getItemDamage() == originalOre.getItemDamage();
    }
}
