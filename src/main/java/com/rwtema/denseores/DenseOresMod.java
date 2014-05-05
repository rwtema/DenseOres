package com.rwtema.denseores;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = DenseOresMod.MODID, version = DenseOresMod.VERSION, dependencies = "after:*")
public class DenseOresMod {
	public static final String MODID = "denseores";
	public static final String VERSION = "1.0";

	public static final String CATEGORY_BLOCK = "ores";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		DenseOresConfig.instance.loadConfig(event.getSuggestedConfigurationFile());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
        DenseOresRegistry.buildBlocks();
        DenseOresRegistry.buildOreDictionary();

		WorldGenOres worldGen = new WorldGenOres();
		GameRegistry.registerWorldGenerator(worldGen, 1000);
		MinecraftForge.EVENT_BUS.register(worldGen);
	}

}
