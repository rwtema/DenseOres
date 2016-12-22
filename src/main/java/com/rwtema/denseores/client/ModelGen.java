package com.rwtema.denseores.client;

import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresRegistry;
import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.utils.ModelBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public class ModelGen {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(ModelGen.class);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public static void loadTextures(TextureStitchEvent.Pre event) {
		ModelManager manager = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "modelManager", "field_175617_aL","field_178090_d","field_178128_c");

		BlockModelShapes shapes = manager.getBlockModelShapes();
		BlockStateMapper mapper = shapes.getBlockStateMapper();
		for (DenseOre ore : DenseOresRegistry.ores.values()) {
			if (ore.texture != null) {
				continue;
			}
			IBlockState state = ore.getBaseBlock().getStateFromMeta(ore.metadata);
			Map<IBlockState, ModelResourceLocation> map = mapper.getVariants(ore.getBaseBlock());
			ModelResourceLocation modelResourceLocation = map.get(state);

			IModel model = null;
			try {
				model = ModelLoaderRegistry.getModel(modelResourceLocation);
			} catch (Exception e) {
				continue;
			}

			Collection<ResourceLocation> textures = model.getTextures();

			for (ResourceLocation texture : textures) {
				if (!texture.equals(new ResourceLocation(ore.underlyingBlockTexture))) {
					ore.texture = texture.toString();
					break;
				}
			}

		}

		for (DenseOre ore : DenseOresRegistry.ores.values()) {
			if (ore.texture == null || "".equals(ore.texture)) {
				ore.sprite = event.getMap().getMissingSprite();
			} else {
				TextureOre textureOre = new TextureOre(ore);
				event.getMap().setTextureEntry(textureOre);
				ore.sprite = event.getMap().getTextureExtry(textureOre.getIconName());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public static void addModels(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		ModelManager manager = event.getModelManager();
		BlockModelShapes shapes = manager.getBlockModelShapes();
		BlockStateMapper mapper = shapes.getBlockStateMapper();

		for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
			BlockDenseOre block = denseOre.block;
			Item item = Item.getItemFromBlock(block);
			modelRegistry.putObject(new ModelResourceLocation(Item.REGISTRY.getNameForObject(item), "inventory"), new EmptyBakedModel());

			Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(block);
			final ModelResourceLocation[] invModels = new ModelResourceLocation[1];
			for (IBlockState iBlockState : block.getBlockState().getValidStates()) {
				ModelResourceLocation blockLocation = locations.get(iBlockState);
				ModelResourceLocation inventoryLocation = new ModelResourceLocation(Item.REGISTRY.getNameForObject(item) + "_" + "dense", "inventory");

				ModelResourceLocation location = mapper.getVariants(denseOre.getBaseBlock()).get(denseOre.getBaseState());
				IBakedModel parentModel = null;
				if (location != null) {
					parentModel = modelRegistry.getObject(location);
				}

				if (parentModel == null) {
					parentModel = modelRegistry.getObject(mapper.getVariants(Blocks.STONE).get(Blocks.STONE.getDefaultState()));
				}

				IBakedModel iBakedModel = ModelBuilder.changeIcon(denseOre.getBaseState(), parentModel, denseOre.sprite);

				modelRegistry.putObject(blockLocation, iBakedModel);
				modelRegistry.putObject(inventoryLocation, iBakedModel);

				mesher.register(item, 0, inventoryLocation);
				invModels[0] = inventoryLocation;
			}

			mesher.register(item, new ItemMeshDefinition() {
				@Nonnull
				@Override
				public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {

					return invModels[0];
				}
			});
		}
	}
}
