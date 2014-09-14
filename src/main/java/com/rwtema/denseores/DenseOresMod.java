package com.rwtema.denseores;

import java.io.File;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DenseOresMod.MODID, version = DenseOresMod.VERSION, dependencies = "after:*")
public class DenseOresMod {
    public static final String MODID = "denseores";
    public static final String VERSION = "1.0";

    @SidedProxy(serverSide = "com.rwtema.denseores.Proxy", clientSide = "com.rwtema.denseores.ProxyClient")
    public static Proxy proxy;
    
    private File config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	config = event.getSuggestedConfigurationFile();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	// load the config in the init to make sure other mods have finished loading
    	LogHelper.info("Ph'nglui mglw'nafh, y'uln Dense Ores shugg ch'agl");
        DenseOresConfig.instance.loadConfig(config);
        DenseOresRegistry.buildBlocks();
    	
        DenseOresRegistry.buildOreDictionary();
        ModIntegration.addModIntegration();

        WorldGenOres worldGen = new WorldGenOres();
        GameRegistry.registerWorldGenerator(worldGen, 1000);
        MinecraftForge.EVENT_BUS.register(worldGen);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        LogHelper.info("Dense Ores is fully loaded but sadly it cannot tell you the unlocalized name for dirt.");
    }

    @EventHandler
    public void checkMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping map : event.getAll()) {
            // check the missing mapping for any of the incorrect names
            if (map.name.startsWith("specialores:") // original mod id
                    || map.name.startsWith("denseores:"))    // correct mod name but name may be duplicated
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
