package com.rwtema.denseores;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class DenseOresConfig {

	public final static DenseOresConfig instance = new DenseOresConfig();

	public final static String CATEGORY_BLOCK = "ores.block_";

	public void loadConfig(File file) {

		Configuration config = new Configuration(file);

		config.load();

		DenseOresRegistry.initVanillaOres();

		for (DenseOre ore : DenseOresRegistry.ores.values()) {

			String cat = CATEGORY_BLOCK + ore.id;

			config.get(cat, "baseBlock", ore.baseBlock);
			config.get(cat, "baseBlockMeta", ore.metadata);
			config.get(cat, "baseBlockTexture", ore.texture);
			config.get(cat, "denseOreProbability", ore.prob);
			config.get(cat, "underlyingBlock", ore.underlyingBlock);

		}

		for (String cat : config.getCategoryNames()) {
			if (cat.startsWith(CATEGORY_BLOCK)) {
				try {
					int id = Integer.parseInt(cat.substring(CATEGORY_BLOCK.length()));

					if (!DenseOresRegistry.hasEntry(id) && config.hasKey(cat, "baseBlock") && config.hasKey(cat, "baseBlockTexture")) {
						DenseOresRegistry.registerOre(id, config.get(cat, "baseBlock", "").getString(), config.get(cat, "baseBlockMeta", 0).getInt(0), config.get(cat, "denseOreProbability", 1)
								.getDouble(1), config.get(cat, "underlyingBlock", "stone").getString(), config.get(cat, "baseBlockTexture", "").getString());

					}
				} catch (NumberFormatException e) {

				}
			}
		}

		config.save();

	}
}
