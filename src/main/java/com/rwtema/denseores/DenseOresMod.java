package com.rwtema.denseores;

import com.google.common.collect.ImmutableSet;
import com.rwtema.denseores.client.ModelGen;
import com.rwtema.denseores.compat.Compat;
import com.rwtema.denseores.debug.WorldGenAnalyser;
import com.rwtema.denseores.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.io.File;
import java.util.function.Consumer;

@Mod(modid = DenseOresMod.MODID, version = DenseOresMod.VERSION, dependencies = "after:*")
public class DenseOresMod {
	public static final String MODID = "denseores";
	public static final String VERSION = "1.0";

	@SidedProxy(serverSide = "com.rwtema.denseores.Proxy", clientSide = "com.rwtema.denseores.ProxyClient")
	public static Proxy proxy;

	private File config;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		String version = Loader.instance().getMinecraftModContainer().getVersion();
		if (ImmutableSet.of("1.9", "1.9.1", "1.9.2", "1.9.3", "1.9.4", "1.10", "1.10.1", "1.10.2").contains(version)) {
			if (!ModAPIManager.INSTANCE.hasAPI("compatlayer")) {
				throw proxy.wrap(new EnhancedRuntimeException(String.format("Dense Ores requires CompatLayer to run in Minecraft %s", version)) {
					@Override
					protected void printStackTrace(WrappedPrintStream stream) {
						stream.println(String.format("Dense Ores Mod requires CompatLayer to run in Minecraft %s", version));
					}
				});
			}
		}


		config = event.getSuggestedConfigurationFile();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		// load the config in the init to make sure other mods have finished loading
		LogHelper.info("Ph'nglui mglw'nafh, y'uln Dense Ores shugg ch'agl");
		DenseOresConfig.instance.loadConfig(config);
		for (FMLInterModComms.IMCMessage message : FMLInterModComms.fetchRuntimeMessages(this)) {
			String key = message.key;
			try {
				if (key.startsWith("addDenseOre")) {
					Class<?> messageType = message.getMessageType();
					ResourceLocation location;

					int rendertype = 0;
					int metadata;
					String underlyingBlockTexture = "blocks/stone";
					switch (key.substring("addDenseOre".length())) {
						case "Stone":
							underlyingBlockTexture = "blocks/stone";
							break;
						case "Netherrack":
							underlyingBlockTexture = "blocks/netherrack";
							break;
						case "EndStone":
							underlyingBlockTexture = "blocks/end_stone";
							break;
						case "Obsidian":
							underlyingBlockTexture = "blocks/obsidian";
							break;
					}

					@Nullable
					String texture = null;

					if (messageType == ItemStack.class) {
						ItemStack stack = message.getItemStackValue();
						ItemBlock itemBlock = (ItemBlock) stack.getItem();
						location = Block.REGISTRY.getNameForObject(itemBlock.block);
						metadata = itemBlock.getMetadata(itemBlock.getDamage(stack));
					} else if (messageType == NBTTagCompound.class) {
						NBTTagCompound nbt = message.getNBTValue();
						location = new ResourceLocation(nbt.getString("baseBlock"));
						metadata = nbt.getInteger("baseBlockMeta");
						if (nbt.hasKey("baseBlockTexture", Constants.NBT.TAG_STRING)) {
							texture = nbt.getString("baseBlockTexture");
						}
						if (nbt.hasKey("underlyingBlockTexture", Constants.NBT.TAG_STRING)) {
							underlyingBlockTexture = nbt.getString("underlyingBlockTexture");
						}
						rendertype = nbt.getInteger("renderType");
					} else {
						continue;
					}
					DenseOresRegistry.registerOre(
							location, metadata, underlyingBlockTexture, texture, 0, rendertype
					);
				}
			} catch (Exception err) {
				throw new ReportedException(new CrashReport("Unabled to load IMC message from " + message.getSender(), err));
			}
		}

		DenseOresRegistry.buildBlocks();
		ModelGen.register();

		DenseOresRegistry.buildOreDictionary();
		ModIntegration.addModIntegration();

		WorldGenOres worldGen = new WorldGenOres();
		GameRegistry.registerWorldGenerator(worldGen, 1000);
		MinecraftForge.EVENT_BUS.register(worldGen);

		if (LogHelper.isDeObf && Compat.INSTANCE.isV11()) {
			//noinspection TrivialFunctionalExpressionUsage
			((Runnable) WorldGenAnalyser::registerWorldGen).run();
		}
	}


	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		LogHelper.info("Dense Ores is fully loaded but sadly it cannot tell you the unlocalized name for dirt.");
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		if (LogHelper.isDeObf) {
			// Roundabout way of preventing java from loading the class when it is not needed
			//noinspection TrivialFunctionalExpressionUsage
			((Consumer<FMLServerStartingEvent>) WorldGenAnalyser::register).accept(event);
		}
	}

}
