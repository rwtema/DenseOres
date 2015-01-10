package com.rwtema.denseores;

public class ModIntegration {
//    public static final String[] canonOres = {"oreIron", "oreGold", "oreCopper", "oreTin", "oreSilver", "oreLead", "oreNickel", "orePlatinum"};
//
//    public static boolean isCanonOre(String ore) {
//        for (String s : canonOres)
//            if (s.equals(ore))
//                return true;
//        return false;
//    }
//
//    public static String getSecondCanonOre(String ore) {
//        for (int i = 0; i < canonOres.length; i++)
//            if (canonOres[i].equals(ore))
//                return canonSecondaryOres[i];
//        return null;
//    }
//
//    public static final String[] canonSecondaryOres = {"oreNickel", null, "oreGold", "oreIron", "oreLead", "oreSilver", "orePlatinum", null};
//
//    public static interface ModInterface {
//        public void registerOre(DenseOre ore, ItemStack input, ItemStack output);
//    }
//
//    public static NBTTagCompound getItemStackNBT(ItemStack item, int newStackSize) {
//        NBTTagCompound tag = getItemStackNBT(item);
//        tag.setByte("Count", (byte) newStackSize);
//        return tag;
//    }
//
//    public static NBTTagCompound getItemStackNBT(ItemStack item) {
//        NBTTagCompound tag = new NBTTagCompound();
//        item.writeToNBT(tag);
//        return tag;
//    }
//
//    public static ItemStack cloneStack(ItemStack item, int newStackSize) {
//        ItemStack newitem = item.copy();
//        newitem.stackSize = newStackSize;
//        return newitem;
//    }
//
//    public static Random rand = new Random();
//
//
//    public static boolean isOreSmeltsToIngot(String oreDict) {
//        if ("".equals(oreDict))
//            return false;
//        String ingotName = "ingot" + oreDict.substring("ore".length());
//
//        return !OreDictionary.getOres(ingotName).isEmpty();
//    }
//
//    public static ItemStack getSmeltedIngot(String oreDict, String preferredModOwner) {
//        if ("".equals(oreDict))
//            return null;
//        String ingotName = "ingot" + oreDict.substring("ore".length());
//
//        ItemStack out = null;
//
//        for (ItemStack ingot : OreDictionary.getOres(ingotName)) {
//            out = ingot;
//
//            String s = GameData.getItemRegistry().getNameForObject(ingot.getItem());
//            if (preferredModOwner != null && preferredModOwner.equals(s.substring(0, s.indexOf(58)))) {
//                return out;
//            }
//        }
//
//        return out;
//    }
//
//
//    public static ItemStack getFurnace(DenseOre toSmelt, float multiplier) {
//        ItemStack out = FurnaceRecipes.smelting().getSmeltingResult(toSmelt.newStack(1));
//
//        if (out == null && isOreSmeltsToIngot(toSmelt.baseOreDictionary)) {
//            out = getSmeltedIngot(toSmelt.baseOreDictionary, toSmelt.modOwner);
//        }
//
//        if (out != null) {
//            out = out.copy();
//
//            if ("minecraft:lapis_ore".equals(toSmelt.baseBlock))
//                out.stackSize = 6;
//            else if ("minecraft:redstone_ore".equals(toSmelt.baseBlock))
//                out.stackSize = 4;
//
//            out.stackSize = (int) (out.stackSize * multiplier);
//            if (out.stackSize > 64)
//                out.stackSize = 64;
//            else if (out.stackSize < 1)
//                out.stackSize = 1;
//        }
//        return out;
//    }
//
//    public static class VanillaFurnace implements ModInterface {
//
//        @Override
//        public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
//            ItemStack out = getFurnace(ore, 3F);
//            if (out != null) {
//                GameRegistry.addSmelting(input, out, 1.0F);
//            }
//        }
//    }
//
//    public static class TE4Integration implements ModInterface {
//        @Override
//        public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
//            addPulverizer(input, output);
//
//            if (isCanonOre(ore.baseOreDictionary) && getFurnace(ore, 1) != null) {
//                ItemStack slag = GameRegistry.findItemStack("ThermalExpansion", "slagRich", 1);
//                String s = getSecondCanonOre(ore.baseOreDictionary);
//                if (s != null && getSmeltedIngot(s, ore.modOwner) != null)
//                    slag = cloneStack(getSmeltedIngot(s, ore.modOwner), 3);
//
//                addSmelter(input, new ItemStack(Blocks.sand, 4), getFurnace(ore, 8.0F), slag, 25);
//                addSmelter(input, GameRegistry.findItemStack("ThermalFoundation", "dustPyrotheum", 2), getFurnace(ore, 8.0F), slag, 75);
//                addSmelter(input, GameRegistry.findItemStack("ThermalFoundation", "crystalCinnabar", 2), getFurnace(ore, 16.0F), slag, 100);
//            }
//        }
//
//        private void addSmelter(ItemStack a, ItemStack b, ItemStack output, ItemStack altOutput, int prob) {
//            if (a == null || b == null || output == null)
//                return;
//
//            NBTTagCompound tag = new NBTTagCompound();
//            tag.setInteger("energy", 16000);
//            tag.setTag("primaryInput", getItemStackNBT(a));
//            tag.setTag("secondaryInput", getItemStackNBT(b));
//            tag.setTag("primaryOutput", getItemStackNBT(output));
//            if (altOutput != null) {
//                tag.setTag("secondaryOutput", getItemStackNBT(altOutput));
//                tag.setInteger("secondaryChance", prob);
//            }
//            FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", tag);
//        }
//
//        private void addPulverizer(ItemStack input, ItemStack output) {
//            NBTTagCompound tag = new NBTTagCompound();
//            tag.setInteger("energy", 8000);
//            tag.setTag("input", getItemStackNBT(input));
//            tag.setTag("primaryOutput", getItemStackNBT(output, 4));
//            FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", tag);
//        }
//    }
//
//    public static ModInterface[] mods = {new VanillaFurnace(), new TE4Integration()};
//
//    public static void addModIntegration() {
//        for (DenseOre ore : DenseOresRegistry.ores.values()) {
//            int bId = ore.id / 16;
//            int meta = ore.id % 16;
//            final BlockDenseOre blockDenseOre = DenseOresRegistry.blocks.get(bId);
//            if (blockDenseOre.isValid(meta)) {
//                ItemStack output = new ItemStack(blockDenseOre.getBlock(meta), 1, ore.metadata);
//                ItemStack input = new ItemStack(blockDenseOre, 1, meta);
//
//                for (ModInterface mod : mods) {
//                    mod.registerOre(ore, input, output);
//                }
//            }
//        }
//    }
}
