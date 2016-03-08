package com.rwtema.denseores;

import com.rwtema.denseores.blocks.TileDepositLevel;
import com.rwtema.denseores.client.DenseModelGenerator;
import com.rwtema.denseores.commands.CommandFindDeposits;
import com.rwtema.denseores.debug.WorldGenAnalyser;
import com.rwtema.denseores.utils.LogHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

@Mod(modid = DenseOresMod.MODID, version = DenseOresMod.VERSION, dependencies = "after:*")
public class DenseOresMod {
    public static final String MODID = "denseores";
    public static final String VERSION = "1.0";

    @SidedProxy(serverSide = "com.rwtema.denseores.Proxy", clientSide = "com.rwtema.denseores.ProxyClient")
    public static Proxy proxy;

    private File config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = event.getSuggestedConfigurationFile();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // load the config in the init to make sure other mods have finished loading
        LogHelper.info("Ph'nglui mglw'nafh, y'uln Dense Ores shugg ch'agl");
        DenseOresConfig.instance.loadConfig(config);
        DenseOresRegistry.buildBlocks();
        DenseModelGenerator.register();

        GameRegistry.registerTileEntity(TileDepositLevel.class, "DenseOreDeposit");

        DenseOresRegistry.buildOreDictionary();
        //ModIntegration.addModIntegration();

        WorldGenOres worldGen = new WorldGenOres();
        GameRegistry.registerWorldGenerator(worldGen, 1000);
        MinecraftForge.EVENT_BUS.register(worldGen);

        if(LogHelper.isDeObf){
            GameRegistry.registerWorldGenerator(WorldGenAnalyser.INSTANCE,1000000);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        LogHelper.info("Dense Ores is fully loaded but sadly it cannot tell you the unlocalized name for dirt.");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        if(LogHelper.isDeObf) {
            WorldGenAnalyser.INSTANCE.reset();
            event.registerServerCommand(WorldGenAnalyser.INSTANCE);
        }


        event.registerServerCommand(new CommandFindDeposits());
    }
}
