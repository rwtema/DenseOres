package com.rwtema.denseores;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

// Load the config file
public class DenseOresConfig {

	public final static DenseOresConfig instance = new DenseOresConfig();

	public final static String CATEGORY_BLOCK = "ores.";

	public void loadConfig(File file) {

		Configuration config = new Configuration(file);

		config.load();

		DenseOresRegistry.initVanillaOres();

		// 'get' the vanilla ore entries to ensure that they exist
		for (DenseOre ore : DenseOresRegistry.ores.values()) {

			String cat = CATEGORY_BLOCK + ore.unofficialName;

			config.get(cat, "baseBlock", ore.baseBlock.toString());
			config.get(cat, "baseBlockMeta", ore.metadata);
			if (ore.texture != null)
				config.get(cat, "baseBlockTexture", ore.texture);
			config.get(cat, "underlyingBlockTexture", ore.underlyingBlockTexture);
			if (ore.rendertype != 0)
				config.get(cat, "renderType", ore.rendertype);
//			config.get(cat, "requiredMod", "minecraft");
		}

		// go through all categories and add them to the registry if they match
		for (String cat : config.getCategoryNames()) {
			if (cat.startsWith(CATEGORY_BLOCK)) {
				String name = cat.substring(CATEGORY_BLOCK.length());

				String requiredMod;

				if (config.hasKey(cat, "requiredMod") && !(requiredMod = config.get(cat, "requiredMod", "").getString()).equals("") && !"minecraft".equals(requiredMod) && !Loader.isModLoaded(requiredMod))
					return;

				// register the block
				if (config.hasKey(cat, "baseBlock")) {
					DenseOresRegistry.registerOre(
							name, new ResourceLocation(config.get(cat, "baseBlock", "").getString().trim()),
							config.get(cat, "baseBlockMeta", 0).getInt(0),
							config.get(cat, "underlyingBlockTexture", "blocks/stone").getString().trim(),
							config.hasKey(cat, "baseBlockTexture") ? config.get(cat, "baseBlockTexture", "").getString().trim() : null,
							config.get(cat, "retroGenID", 0).getInt(),
							config.hasKey(cat, "renderType") ? config.get(cat, "renderType", 0).getInt(0) : 0);
				}
			}
		}

		config.save();
	}
}
