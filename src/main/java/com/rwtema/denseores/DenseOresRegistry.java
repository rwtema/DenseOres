package com.rwtema.denseores;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blocks.ItemBlockDenseOre;
import com.rwtema.denseores.blockstates.OreType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class DenseOresRegistry {

    public static Map<String, DenseOre> ores = new HashMap<String, DenseOre>();
    public static String blockPrefix = DenseOresMod.MODID;

    // add vanilla entries (TODO: add a way to disable vanilla ores)
    public static void initVanillaOres() {
        registerOre("mc_iron", "minecraft:iron_ore", 0, "blocks/stone", "blocks/iron_ore", 0, 0);
        registerOre("mc_gold", "minecraft:gold_ore", 0, "blocks/stone", "blocks/gold_ore", 0, 0);
        registerOre("mc_lapis", "minecraft:lapis_ore", 0, "blocks/stone", "blocks/lapis_ore", 0, 0);
        registerOre("mc_diamond", "minecraft:diamond_ore", 0, "blocks/stone", "blocks/diamond_ore", 0, 0);
        registerOre("mc_emerald", "minecraft:emerald_ore", 0, "blocks/stone", "blocks/emerald_ore", 0, 0);
        registerOre("mc_redstone", "minecraft:redstone_ore", 0, "blocks/stone", "blocks/redstone_ore", 0, 0);
        registerOre("mc_coal", "minecraft:coal_ore", 0, "blocks/stone", "blocks/coal_ore", 0, 0);
        registerOre("mc_quartz", "minecraft:quartz_ore", 0, "blocks/netherrack", "blocks/quartz_ore", 0, 0);
    }

    // create the blocks needed
    public static void buildBlocks() {
        for (DenseOre ore : ores.values()) {
            BlockDenseOre block = new BlockDenseOre(ore);
            GameRegistry.registerBlock(block, ItemBlockDenseOre.class, ore.name);
            ore.setBlock(block);
        }
    }

    public static DenseOre registerOre(String name, String baseBlock, int metadata, String underlyingBlock, String texture, int retroGenId, int renderType) {

        if ("".equals(baseBlock) || "minecraft:air".equals(baseBlock))
            return null;

        int ind = baseBlock.indexOf(58);

        if (ind > 1) {
            String modname = baseBlock.substring(0, ind);
            if (!"minecraft".equals(modname) && !Loader.isModLoaded(modname))
                return null;
        } else {
            throw new RuntimeException("Block " + name + " is not formatted correctly. Must be in the form mod:block");
        }

        DenseOre ore = new DenseOre(name, baseBlock, metadata, underlyingBlock, texture, retroGenId, renderType);
        ores.put(name, ore);
        return ore;
    }

    public static boolean hasEntry(String id) {
        return ores.containsKey(id);
    }

    //Look for valid ore dictionary references and add new ones
    public static void buildOreDictionary() {
        for (DenseOre ore : ores.values()) {

            if (ore.block.isValid()) {
                for (int oreid : OreDictionary.getOreIDs(new ItemStack(ore.block.getBlock(), 1, ore.metadata))) {
                    String oreName = OreDictionary.getOreName(oreid);

                    if (oreName.length() > 3 && oreName.startsWith("ore") && Character.isUpperCase(oreName.charAt(3))) {
                        ore.baseOreDictionaryEntry = oreName;
                        for (OreType type : OreType.values()) {
                            type.registerOre(ore, oreName);
                        }

                    }
                }
            }
        }
    }

}