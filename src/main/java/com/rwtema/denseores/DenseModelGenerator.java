package com.rwtema.denseores;

import com.rwtema.denseores.ModelBuilder.ModelBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DenseModelGenerator {
    public static DenseModelGenerator INSTANCE = new DenseModelGenerator();
    public static TextureAtlasSprite[] baseicons;
    public static TextureAtlasSprite[] undericons;
    public static TextureAtlasSprite[] icons;
    public TextureMap textureMap;

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static ResourceLocation affixDir(ResourceLocation location)
    {
        return new ResourceLocation(location.getResourceDomain(), "blocks/" + location.getResourcePath());
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event)
    {
        icons = new TextureAtlasSprite[BlockDenseOre.maxMetdata];
        baseicons = new TextureAtlasSprite[BlockDenseOre.maxMetdata];
        undericons = new TextureAtlasSprite[BlockDenseOre.maxMetdata];

        textureMap = event.map;

        for (DenseOre entry : DenseOresRegistry.ores.values())
        {
            int i = entry.id;



            // name of custom icon ( must equal getIconName() )
            String name = TextureOre.getDerivedName(entry.texture);
            // see if there's already an icon of that name
            TextureAtlasSprite texture = textureMap.getTextureExtry(name);
            if (texture == null)
            {
                // if not create one and put it in the register
                texture = new TextureOre(entry);
                textureMap.setTextureEntry(name, texture);
            }

            icons[i] = textureMap.getTextureExtry(name);
        }

    }

    public class HackMapper extends DefaultStateMapper {
        @Override
        public ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_)
        {
            return super.getModelResourceLocation(p_178132_1_);
        }
    }

    @SubscribeEvent
    public void bakeModels(ModelBakeEvent event)
    {
        HackMapper defaultStateMapper = new HackMapper();

        DenseOresMod.block.models = new IBakedModel[BlockDenseOre.maxMetdata];
        DenseOresMod.block.invmodels = new IBakedModel[BlockDenseOre.maxMetdata];

        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (DenseOre denseOre : DenseOresRegistry.ores.values())
        {

            int meta = denseOre.id;

            ModelResourceLocation modelResourceLocation = defaultStateMapper.getModelResourceLocation(DenseOresMod.block.getStateFromMeta(meta));
            IBakedModel baseModel = event.modelManager.getBlockModelShapes().getModelForState(DenseOresMod.block.getBaseBlockState(meta));

            DenseOresMod.block.models[meta] = ModelBuilder.changePrimaryIcon(baseModel, icons[meta]);
            event.modelRegistry.putObject(modelResourceLocation, DenseOresMod.block.models[meta]);

            IBakedModel itemModel = itemModelMesher.getItemModel(denseOre.newStack(1));

            DenseOresMod.block.invmodels[meta] =  ModelBuilder.changePrimaryIcon(itemModel, icons[meta]);

            ModelResourceLocation inventory = new ModelResourceLocation(modelResourceLocation, "inventory");
            event.modelRegistry.putObject(inventory, DenseOresMod.block.invmodels[meta]);
            itemModelMesher.register(Item.getItemFromBlock(DenseOresMod.block), meta, inventory);
        }
    }
}
