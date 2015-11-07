package com.rwtema.denseores;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Load the config file
public class DenseOresConfig {

    public final static DenseOresConfig instance = new DenseOresConfig();

    public final static String CATEGORY_BLOCK = "ores.block_";
    public final static String CATEGORY_WORLD_GENERATION = "world_generation";

    public Boolean WORLD_GENERATION_ENABLED;

    public void loadConfig(File file) {

        Configuration config = new Configuration(file);

        config.load();

        this.WORLD_GENERATION_ENABLED = config.get(CATEGORY_WORLD_GENERATION, "enabled", true).getBoolean(true);

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
            config.get(cat, "renderType", ore.rendertype);
        }

        // go through all categories and add them to the registry if they match
        for (String cat : config.getCategoryNames()) {
            if (cat.startsWith(CATEGORY_BLOCK)) {
                try {
                    // get the text after ores.block_ and try to convert it to
                    // an integer
                    int id = Integer.parseInt(cat.substring(CATEGORY_BLOCK.length()));

                    if (config.hasKey(cat, "requiredMod") && !config.get(cat, "requiredMod", "").getString().equals("") && !Loader.isModLoaded(config.get(cat, "requiredMod", "").getString()))
                        return;

                    // register the block
                    if (!DenseOresRegistry.hasEntry(id) && config.hasKey(cat, "baseBlock") && config.hasKey(cat, "baseBlockTexture")) {


                        DenseOre denseOre = DenseOresRegistry.registerOre(id,
                                config.get(cat, "baseBlock", "").getString().trim(),
                                config.get(cat, "baseBlockMeta", 0).getInt(0),
                                config.get(cat, "denseOreProbability", 1).getDouble(1),
                                config.get(cat, "underlyingBlock", "stone").getString().trim(),
                                config.get(cat, "baseBlockTexture", "").getString().trim(),
                                config.get(cat, "retroGenID", 0).getInt(),
                                config.get(cat, "renderType", 0).getInt(0));
                        if (denseOre != null) {
                            if (config.hasKey(cat, "underlyingBlockTexture")) {
                                denseOre.underlyingBlocktexture = config.get(cat, "underlyingBlockTexture", denseOre.baseBlock).getString();
                            }

//                            if (config.hasKey(cat, "baseBlockTextureOverlay_0")) {
//                                List<String> overlayList = new ArrayList<String>();
//                                for (int i = 0; config.hasKey(cat, "baseBlockTextureOverlay_" + i); i++) {
//                                    overlayList.add(config.get(cat, "baseBlockTextureOverlay_" + i, "").getString());
//                                }
//                                String[] overlays = overlayList.toArray(new String[overlayList.size()]);
//                            }
                        }

                    }
                } catch (NumberFormatException e) { // text after ore.block_ was
                    // not an integer
                }
            }
        }

        config.save();

    }
}
