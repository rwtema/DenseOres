package com.rwtema.denseores;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blocks.ItemBlockDenseOre;
import com.rwtema.denseores.compat.Compat;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DenseOresRegistry {

	public static Map<ResourceLocation, DenseOre> ores = new HashMap<>();
	public static String blockPrefix = DenseOresMod.MODID;

	// add vanilla entries (TODO: add a way to disable vanilla ores)
	public static void initVanillaOres() {
		registerOre("Vanilla Iron Ore", new ResourceLocation("iron_ore"), 0, "blocks/stone", "blocks/iron_ore", 0, 0);
		registerOre("Vanilla Gold Ore", new ResourceLocation("gold_ore"), 0, "blocks/stone", "blocks/gold_ore", 0, 0);
		registerOre("Vanilla Lapis Ore", new ResourceLocation("lapis_ore"), 0, "blocks/stone", "blocks/lapis_ore", 0, 0);
		registerOre("Vanilla Diamond Ore", new ResourceLocation("diamond_ore"), 0, "blocks/stone", "blocks/diamond_ore", 0, 0);
		registerOre("Vanilla Emerald Ore", new ResourceLocation("emerald_ore"), 0, "blocks/stone", "blocks/emerald_ore", 0, 0);
		registerOre("Vanilla Redstone Ore", new ResourceLocation("redstone_ore"), 0, "blocks/stone", "blocks/redstone_ore", 0, 0);
		registerOre("Vanilla Coal Ore", new ResourceLocation("coal_ore"), 0, "blocks/stone", "blocks/coal_ore", 0, 0);
		registerOre("Vanilla Quartz Ore", new ResourceLocation("quartz_ore"), 0, "blocks/netherrack", "blocks/quartz_ore", 0, 0);
	}

	@SubscribeEvent
	public static void registerBlock(final RegistryEvent.Register<Block> event) {
		for (DenseOre ore : ores.values()) {
			event.getRegistry().register(ore.block);
		}
	}
	
	@SubscribeEvent
	public static void registerItem(final RegistryEvent.Register<Item> event) {
		for (DenseOre ore : ores.values()) {
			event.getRegistry().register(ore.itemBlock);
		}
	}
		

	public static DenseOre registerOre(@Nullable String unofficialName, ResourceLocation baseBlock, int metadata, String underlyingBlock, @Nullable String texture, int retroGenId, int renderType) {
		if ("".equals(baseBlock.toString()) || "minecraft:air".equals(baseBlock.toString()))
			return null;

		String resourceDomain = baseBlock.getNamespace();

		if (!"minecraft".equals(resourceDomain) && !Loader.isModLoaded(Compat.INSTANCE.makeLowercase(resourceDomain))) {
			return null;
		}

		ResourceLocation name = new ResourceLocation("denseores", (resourceDomain + "_" + baseBlock.getPath() + "_" + metadata).toLowerCase(Locale.ENGLISH));

		if ("".equals(texture)) texture = null;

		if (unofficialName == null) {
			unofficialName = name.toString();
		}

		DenseOre ore = new DenseOre(unofficialName, name, baseBlock, metadata, underlyingBlock, texture, retroGenId, renderType);
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
						String newOreName = "dense" + oreName;
						ore.oreDictionary = newOreName;
						OreDictionary.registerOre(newOreName, new ItemStack(ore.block));
					}
				}
			}
		}
	}


}