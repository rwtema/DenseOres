package com.rwtema.denseores;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

// Load the config file
public class DenseOresConfig {

    public final static DenseOresConfig instance = new DenseOresConfig();

    public final static String CATEGORY_BLOCK = "ores.block_";

    public void loadConfig(File file) {

        Configuration config = new Configuration(file);

        config.load();

        DenseOresRegistry.initVanillaOres();

        // 'get' the vanilla ore entries to ensure that they exist
        for (DenseOre ore : DenseOresRegistry.ores.values()) {

            String cat = CATEGORY_BLOCK + ore.name;

            config.get(cat, "baseBlock", ore.baseBlock);
            config.get(cat, "baseBlockMeta", ore.metadata);
            config.get(cat, "baseBlockTexture", ore.texture);
            config.get(cat, "underlyingBlockTexture", ore.underlyingBlockTexture);
            config.get(cat, "retroGenId", 0);
            if (ore.rendertype != 0)
                config.get(cat, "renderType", ore.rendertype);
        }

        // go through all categories and add them to the registry if they match
        for (String cat : config.getCategoryNames()) {
            if (cat.startsWith(CATEGORY_BLOCK)) {
                // get the text after ores.block_ and try to convert it to
                // an integer
                String name = cat.substring(CATEGORY_BLOCK.length());

                if (config.hasKey(cat, "requiredMod") && !config.get(cat, "requiredMod", "").getString().equals("") && !Loader.isModLoaded(config.get(cat, "requiredMod", "").getString()))
                    return;

                // register the block
                if (!DenseOresRegistry.hasEntry(name) && config.hasKey(cat, "baseBlock") && config.hasKey(cat, "baseBlockTexture")) {
                    DenseOresRegistry.registerOre(name,
                            config.get(cat, "baseBlock", "").getString().trim(),
                            config.get(cat, "baseBlockMeta", 0).getInt(0),
                            config.get(cat, "underlyingBlockTexture", "blocks/stone").getString().trim(),
                            config.get(cat, "baseBlockTexture", "").getString().trim(),
                            config.get(cat, "retroGenID", 0).getInt(),
                            config.hasKey(cat, "renderType") ? config.get(cat, "renderType", 0).getInt(0) : 0);
                }
            }
        }

        config.save();

    }
}
