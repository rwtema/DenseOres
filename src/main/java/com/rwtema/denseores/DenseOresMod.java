package com.rwtema.denseores;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DenseOresMod.MODID, version = DenseOresMod.VERSION, dependencies = "after:*")
public class DenseOresMod {
    public static final String MODID = "denseores";
    public static final String VERSION = "1.0";

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

    @EventHandler
    public void checkMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping map : event.getAll()) {
            // check the missing mapping for any of the incorrect names
            if (map.name.startsWith("specialores:") // original mod id
                    || map.name.startsWith("denseores:")    // correct mod name but name may be duplicated
                    || map.name.startsWith("testificatedenseores:"))    // fake incorrect mapping for test purposes
            {
                // retrieve the block's number from the end of the string
                int k = -1;
                for (int i : DenseOresRegistry.blocks.keySet()) {
                    if (map.name.endsWith(Integer.toString(i)) && (k == -1 || Integer.toString(k).length() < Integer.toString(i).length())) {
                        k = i;
                    }
                }

                // remap the block
                if (k >= 0) {
                    if (map.type == GameRegistry.Type.BLOCK) {
                        map.remap(DenseOresRegistry.blocks.get(k));
                    } else if (map.type == GameRegistry.Type.ITEM) {
                        map.remap(Item.getItemFromBlock(DenseOresRegistry.blocks.get(k)));
                    }
                }
            }
        }
    }

}
