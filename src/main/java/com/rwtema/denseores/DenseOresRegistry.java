package com.rwtema.denseores;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class DenseOresRegistry {

    public static Map<Integer, BlockDenseOre> blocks = new HashMap<Integer, BlockDenseOre>();

    public static Map<Integer, DenseOre> ores = new HashMap<Integer, DenseOre>();

    // add vanilla entries (TODO: add a way to disable vanilla ores)
    public static void initVanillaOres() {
        registerOre(0, "minecraft:iron_ore", 0, 1, "stone", "iron_ore", 0,  0);
        registerOre(1, "minecraft:gold_ore", 0, 1, "stone", "gold_ore", 0,  0);
        registerOre(2, "minecraft:lapis_ore", 0, 1, "stone", "lapis_ore", 0,  0);
        registerOre(3, "minecraft:diamond_ore", 0, 1, "stone", "diamond_ore", 0,  0);
        registerOre(4, "minecraft:emerald_ore", 0, 1, "stone", "emerald_ore", 0,  0);
        registerOre(5, "minecraft:redstone_ore", 0, 1, "stone", "redstone_ore", 0,  0);
        registerOre(6, "minecraft:coal_ore", 0, 1, "stone", "coal_ore", 0,  0);
        registerOre(7, "minecraft:quartz_ore", 0, 1, "netherrack", "quartz_ore", 0,  0);
    }

    public static String blockPrefix = DenseOresMod.MODID;

    // create the blocks needed
    public static void buildBlocks() {
        for (DenseOre ore : ores.values()) {
            int bId = ore.id / 16;
            BlockDenseOre newBlock = blocks.get(bId);

            if (newBlock == null) {
                newBlock = (BlockDenseOre) ((new BlockDenseOre()).setBlockName(blockPrefix + ":block" + bId).setHardness(3));
                blocks.put(bId, newBlock);
                GameRegistry.registerBlock(newBlock, ItemBlockDenseOre.class, "block" + bId);
            }

            newBlock.setEntry(ore.id % 16, ore);
            ore.setBlock(newBlock);
        }
    }

    public static DenseOre registerOre(int id, String baseBlock, int metadata, double prob, String underlyingBlock, String texture, int retroGenId,  int renderType) {

        if ("".equals(baseBlock) || "minecraft:air".equals(baseBlock))
            return null;


        int ind = baseBlock.indexOf(58);

        if (ind > 1) {
            String modname = baseBlock.substring(0, ind);
            if (!"minecraft".equals(modname) && !Loader.isModLoaded(modname))
                return null;
        } else {
            throw new RuntimeException("Block " + id + " is not formatted correctly. Must be in the form mod:block");
        }

        final DenseOre ore = new DenseOre(id, baseBlock, metadata, prob, underlyingBlock, texture, retroGenId, renderType);
        ores.put(id, ore);
        return ore;
    }

    public static boolean hasEntry(int id) {
        return ores.containsKey(id);
    }

    //Look for valid ore dictionary references and add new ones
    public static void buildOreDictionary() {
        for (DenseOre ore : ores.values()) {
            int bId = ore.id / 16;
            int meta = ore.id % 16;
            if (blocks.get(bId).isValid(meta)) {
                Block b = blocks.get(bId).getBlock(meta);
                for (int oreid : OreDictionary.getOreIDs(new ItemStack(b, 1, ore.metadata))) {
                    String k = OreDictionary.getOreName(oreid);
                    if (k.startsWith("ore")) {
                        ore.baseOreDictionary = k;
                        k = "dense" + k;
                        ore.oreDictionary = k;
                        OreDictionary.registerOre(k, new ItemStack(blocks.get(bId), 1, meta));
                    }
                }
            }
        }
    }
}