package com.rwtema.denseores;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blocks.ItemBlockDenseOre;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DenseOresRegistry {

	public static Map<ResourceLocation, DenseOre> ores = new HashMap<>();
	public static String blockPrefix = DenseOresMod.MODID;

	// add vanilla entries (TODO: add a way to disable vanilla ores)
	public static void initVanillaOres() {
		registerOre(new ResourceLocation("iron_ore"), 0, "blocks/stone", "blocks/iron_ore", 0, 0);
		registerOre(new ResourceLocation("gold_ore"), 0, "blocks/stone", "blocks/gold_ore", 0, 0);
		registerOre(new ResourceLocation("lapis_ore"), 0, "blocks/stone", "blocks/lapis_ore", 0, 0);
		registerOre(new ResourceLocation("diamond_ore"), 0, "blocks/stone", "blocks/diamond_ore", 0, 0);
		registerOre(new ResourceLocation("emerald_ore"), 0, "blocks/stone", "blocks/emerald_ore", 0, 0);
		registerOre(new ResourceLocation("redstone_ore"), 0, "blocks/stone", "blocks/redstone_ore", 0, 0);
		registerOre(new ResourceLocation("coal_ore"), 0, "blocks/stone", "blocks/coal_ore", 0, 0);
		registerOre(new ResourceLocation("quartz_ore"), 0, "blocks/netherrack", "blocks/quartz_ore", 0, 0);
	}

	// create the blocks needed
	public static void buildBlocks() {
		for (DenseOre ore : ores.values()) {
			BlockDenseOre block = new BlockDenseOre(ore);
			ItemBlockDenseOre itemBlockDenseOre = new ItemBlockDenseOre(block);
			block.setRegistryName(ore.name);
			block.setUnlocalizedName(ore.name.toString());
			itemBlockDenseOre.setRegistryName(ore.name);
			itemBlockDenseOre.setUnlocalizedName(ore.name.toString());
			GameRegistry.register(block);
			GameRegistry.register(itemBlockDenseOre);
			ore.setBlock(block);
		}
	}

	public static DenseOre registerOre(ResourceLocation baseBlock, int metadata, String underlyingBlock, @Nullable String texture, int retroGenId, int renderType) {
		if ("".equals(baseBlock.toString()) || "minecraft:air".equals(baseBlock.toString()))
			return null;

		ResourceLocation name = new ResourceLocation("denseores", baseBlock.getResourceDomain() + "_" + baseBlock.getResourcePath());

		if ("".equals(texture)) texture = null;

		DenseOre ore = new DenseOre(name, baseBlock, metadata, underlyingBlock, texture, retroGenId, renderType);
		ores.put(name, ore);
		return ore;
	}

	//Look for valid ore dictionary references and add new ones
	public static void buildOreDictionary() {
		for (DenseOre ore : ores.values()) {

			if (ore.block.isValid()) {
				for (int oreid : OreDictionary.getOreIDs(new ItemStack(ore.block.getBlock(), 1, ore.metadata))) {
					String oreName = OreDictionary.getOreName(oreid);

					if (oreName.length() > 3 && oreName.startsWith("ore") && Character.isUpperCase(oreName.charAt(3))) {
						ore.baseOreDictionaryEntry = oreName;
					}
				}
			}
		}
	}


}