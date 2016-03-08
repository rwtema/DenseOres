package com.rwtema.denseores.client;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresRegistry;
import com.rwtema.denseores.blockstates.OreType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;
import java.util.Map;

public class DenseModelGenerator {
    public static DenseModelGenerator INSTANCE = new DenseModelGenerator();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureStitch(TextureStitchEvent.Pre event) {
        TextureMap textureMap = event.map;

        for (DenseOre entry : DenseOresRegistry.ores.values()) {
            entry.sprites = new EnumMap<OreType, TextureAtlasSprite>(OreType.class);
            for (OreType oreType : OreType.values()) {
                // name of custom icon ( must equal getIconName() )
                String name = TextureOre.getDerivedName(entry.texture, oreType);
                // see if there's already an icon of that name
                TextureAtlasSprite texture = textureMap.getTextureExtry(name);
                if (texture == null) {
                    // if not create one and put it in the register
                    texture = new TextureOre(entry, oreType);
                    textureMap.setTextureEntry(name, texture);
                }
                entry.sprites.put(oreType, textureMap.getTextureExtry(name));
            }
        }
    }

    @SubscribeEvent()
    @SideOnly(Side.CLIENT)
    public void bakeModels(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.modelRegistry;

        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
            BlockDenseOre block = denseOre.block;
            Item item = Item.getItemFromBlock(block);
            modelRegistry.putObject(new ModelResourceLocation(Item.itemRegistry.getNameForObject(item), "inventory"), new EmptyBakedModel());

            Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(block);
            final ModelResourceLocation[] invModels = new ModelResourceLocation[OreType.values().length];

            for (IBlockState iBlockState : block.getBlockState().getValidStates()) {
                OreType oreType = iBlockState.getValue(BlockDenseOre.ORE_TYPE);
                ModelResourceLocation blockLocation = locations.get(iBlockState);
                ModelResourceLocation inventoryLocation = new ModelResourceLocation(Item.itemRegistry.getNameForObject(item) + "_" + oreType.getName(), "inventory");

                DynamicBlockModel model = new DynamicBlockModel(denseOre, iBlockState, oreType);

                modelRegistry.putObject(blockLocation, model);
                modelRegistry.putObject(inventoryLocation, model);

                mesher.register(item, oreType.ordinal(), inventoryLocation);
                invModels[oreType.ordinal()] = inventoryLocation;
            }


            mesher.register(item, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    int itemDamage = stack.getItemDamage();
                    if (itemDamage < 0 || itemDamage >= OreType.values().length) itemDamage = 0;
                    return invModels[itemDamage];
                }
            });

        }
    }
}
