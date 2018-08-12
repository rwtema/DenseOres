package com.rwtema.denseores;

import com.google.common.collect.ImmutableSet;
import com.rwtema.denseores.client.ModelGen;
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
import java.util.Objects;
import java.util.function.Consumer;
@Mod(modid = DenseOresMod.MODID, name = "Dense Ores", version = DenseOresMod.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class DenseOresMod {
	public static final String MODID = "denseores";
	public static final String VERSION = "1.0";

	@SidedProxy(serverSide = "com.rwtema.denseores.Proxy", clientSide = "com.rwtema.denseores.ProxyClient")
	public static Proxy proxy;

	private File config;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = event.getSuggestedConfigurationFile();
		LogHelper.info("Loading the config.");
		DenseOresConfig.instance.loadConfig(config);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		LogHelper.info("Ph'nglui mglw'nafh, y'uln Dense Ores shugg ch'agl");
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

					String unofficialName = null;

					if (messageType == ItemStack.class) {
						ItemStack stack = message.getItemStackValue();
						ItemBlock itemBlock = (ItemBlock) stack.getItem();
						location = Block.REGISTRY.getNameForObject(itemBlock.getBlock());
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
						unofficialName = nbt.getString("config_entry");
					} else {
						throw new IllegalArgumentException("Unable to process IMC type: " + messageType);
					}

					if(unofficialName == null || "".equals(unofficialName)){
						unofficialName = null;
					}
					DenseOresRegistry.registerOre(
							unofficialName, location, metadata, underlyingBlockTexture, texture, 0, rendertype
					);
				}
			} catch (Exception err) {
				throw new ReportedException(new CrashReport("Unabled to load IMC message from " + message.getSender(), err));
			}
		}

		ModelGen.register();
		ModIntegration.addModIntegration();
		LogHelper.info("Building the ore dictionary.");
		DenseOresRegistry denseore_registry = new DenseOresRegistry();
		MinecraftForge.EVENT_BUS.register(denseore_registry);
		denseore_registry.buildOreDictionary();
		LogHelper.info("Registering the world generator.");
		WorldGenOres worldGen = new WorldGenOres();
		GameRegistry.registerWorldGenerator(worldGen, 1000);
		MinecraftForge.EVENT_BUS.register(worldGen);
	}


	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		LogHelper.info("Ores are fully densified.");
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
	}

}
