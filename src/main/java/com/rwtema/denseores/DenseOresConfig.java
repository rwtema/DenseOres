package com.rwtema.denseores;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

// Load the config file
public class DenseOresConfig {

	public final static DenseOresConfig instance = new DenseOresConfig();

	public final static String CATEGORY_BLOCK = "ores.block_";

	public void loadConfig(File file) {

		Configuration config = new Configuration(file);

		config.load();

		if (config.get("General", "LegacyBlockNames", false).getBoolean(false))
			DenseOresRegistry.blockPrefix = "specialores:block";

		DenseOresRegistry.initVanillaOres();

		// 'get' the vanilla ore entries to ensure that they exist
		for (DenseOre ore : DenseOresRegistry.ores.values()) {

			String cat = CATEGORY_BLOCK + ore.id;

			config.get(cat, "baseBlock", ore.baseBlock);
			config.get(cat, "baseBlockMeta", ore.metadata);
			config.get(cat, "baseBlockTexture", ore.texture);
			config.get(cat, "denseOreProbability", ore.prob);
			config.get(cat, "underlyingBlock", ore.underlyingBlock);
			config.get(cat, "retroGenId", 0);
		}

		// go through all categories and add them to the registry if they match
		for (String cat : config.getCategoryNames()) {
			if (cat.startsWith(CATEGORY_BLOCK)) {
				try {
					// get the text after ores.block_ and try to convert it to
					// an integer
					int id = Integer.parseInt(cat.substring(CATEGORY_BLOCK.length()));

					// register the block
					if (!DenseOresRegistry.hasEntry(id) && config.hasKey(cat, "baseBlock") && config.hasKey(cat, "baseBlockTexture")) {
						DenseOresRegistry.registerOre(id, config.get(cat, "baseBlock", "").getString(), config.get(cat, "baseBlockMeta", 0).getInt(0), config.get(cat, "denseOreProbability", 1)
								.getDouble(1), config.get(cat, "underlyingBlock", "stone").getString(), config.get(cat, "baseBlockTexture", "").getString(), config.get(cat, "retroGenID", 0).getInt());

					}
				} catch (NumberFormatException e) { // text after ore.block_ was
													// not an integer

				}
			}
		}

		config.save();

	}
}
