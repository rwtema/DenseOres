package com.rwtema.denseores;

import com.rwtema.denseores.ModelBuilder.ModelBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DenseModelGenerator {
    public static DenseModelGenerator INSTANCE = new DenseModelGenerator();
    public static TextureAtlasSprite[] icons;
    public TextureMap textureMap;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    // Allows us to add entries for our icons
    public void textureStitch(TextureStitchEvent.Pre event) {
        icons = new TextureAtlasSprite[BlockDenseOre.maxMetdata];

        textureMap = event.map;

        for (DenseOre entry : DenseOresRegistry.ores.values()) {
            int i = entry.id;

            // Note: Normally you would simply use map.registerSprite(), this method
            // is only required for custom texture classes.

            // name of custom icon ( must equal getIconName() )
            String name = TextureOre.getDerivedName(entry.texture);
            // see if there's already an icon of that name
            TextureAtlasSprite texture = textureMap.getTextureExtry(name);
            if (texture == null) {
                // if not create one and put it in the register
                texture = new TextureOre(entry);
                textureMap.setTextureEntry(name, texture);
            }

            icons[i] = textureMap.getTextureExtry(name);
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // This allows us to create and add Baked Models to the registry
    public void bakeModels(ModelBakeEvent event) {
        DenseOresMod.block.models = new IBakedModel[BlockDenseOre.maxMetdata];
        DenseOresMod.block.invmodels = new IBakedModel[BlockDenseOre.maxMetdata];

        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (DenseOre denseOre : DenseOresRegistry.ores.values()) {

            int meta = denseOre.id;

            /// * Block model *
            // get the model registries entry for the current Dense Ore block state
            ModelResourceLocation modelResourceLocation = ModelBuilder.getModelResourceLocation(DenseOresMod.block.getStateFromMeta(meta));

            // get the baked model for the base block state
            IBakedModel baseModel = event.modelManager.getBlockModelShapes().getModelForState(DenseOresMod.block.getBaseBlockState(meta));

            // generate the new dense ores baked model
            DenseOresMod.block.models[meta] = ModelBuilder.changeIcon(baseModel, icons[meta]);

            // add to the registry
            event.modelRegistry.putObject(modelResourceLocation, DenseOresMod.block.models[meta]);

            /// * Item model *
            // get the item model for the base blocks itemstack
            IBakedModel itemModel = itemModelMesher.getItemModel(denseOre.newStack(1));

            // generate the item model for the Dense ore block
            DenseOresMod.block.invmodels[meta] = ModelBuilder.changeIcon(itemModel, icons[meta]);

            // this creates the entry for the inventory block
            ModelResourceLocation inventory = new ModelResourceLocation(modelResourceLocation, "inventory");

            // add to registry
            event.modelRegistry.putObject(inventory, DenseOresMod.block.invmodels[meta]);

            // register with the itemModelMesher
            itemModelMesher.register(Item.getItemFromBlock(DenseOresMod.block), meta, inventory);
        }
    }
}
