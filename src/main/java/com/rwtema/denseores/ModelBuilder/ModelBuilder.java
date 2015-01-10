package com.rwtema.denseores.ModelBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@SuppressWarnings("unchecked")
public class ModelBuilder {
    // create a blank baked model with the default values
    public static SimpleBakedModel newBlankModel(TextureAtlasSprite texture) {
        return new SimpleBakedModel(new LinkedList(), newBlankFacingLists(), true, true, texture, ItemCameraTransforms.DEFAULT);
    }

    // create a copy of a quad
    public static BakedQuad copyQuad(BakedQuad quad) {
        return new BakedQuad(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), quad.getFace());
    }

    // copy a quad with a different texture overlayed on it
    public static BakedQuad changeTexture(BakedQuad quad, TextureAtlasSprite tex) {
        quad = copyQuad(quad);

        // 4 vertexes on each quad
        for (int i = 0; i < 4; ++i) {
            int j = 7 * i;
            // get the x,y,z coordinates
            float x = Float.intBitsToFloat(quad.getVertexData()[j]);
            float y = Float.intBitsToFloat(quad.getVertexData()[j + 1]);
            float z = Float.intBitsToFloat(quad.getVertexData()[j + 2]);
            float u = 0.0F;
            float v = 0.0F;

            // move x,y,z in boundary if they are outside
            if (x < 0 || x > 1) x = (x + 1) % 1;
            if (y < 0 || y > 1) y = (y + 1) % 1;
            if (z < 0 || z > 1) z = (z + 1) % 1;


            // calculate the UVs based on the x,y,z and the 'face' of the quad
            switch (quad.getFace().ordinal()) {
                case 0:
                    u = x * 16.0F;
                    v = (1.0F - z) * 16.0F;
                    break;
                case 1:
                    u = x * 16.0F;
                    v = z * 16.0F;
                    break;
                case 2:
                    u = (1.0F - x) * 16.0F;
                    v = (1.0F - y) * 16.0F;
                    break;
                case 3:
                    u = x * 16.0F;
                    v = (1.0F - y) * 16.0F;
                    break;
                case 4:
                    u = z * 16.0F;
                    v = (1.0F - y) * 16.0F;
                    break;
                case 5:
                    u = (1.0F - z) * 16.0F;
                    v = (1.0F - y) * 16.0F;
            }

            // set the new texture uvs
            quad.getVertexData()[j + 4] = Float.floatToRawIntBits(tex.getInterpolatedU((double) u));
            quad.getVertexData()[j + 4 + 1] = Float.floatToRawIntBits(tex.getInterpolatedV((double) v));
        }

        return quad;
    }

    // Creates a copy of the baked model with a given texture overlayed on the sides
    public static SimpleBakedModel changeIcon(IBakedModel model, TextureAtlasSprite texture) {
        SimpleBakedModel bakedModel = new SimpleBakedModel(new LinkedList(), newBlankFacingLists(), model.isGui3d(), model.isAmbientOcclusion(), texture, model.getItemCameraTransforms());

        for (Object o : model.getGeneralQuads()) {
            bakedModel.getGeneralQuads().add(changeTexture((BakedQuad) o, texture));
        }

        for (EnumFacing facing : EnumFacing.values()) {
            for (Object o : model.getFaceQuads(facing)) {
                bakedModel.getFaceQuads(facing).add(changeTexture((BakedQuad) o, texture));
            }
        }

        return bakedModel;
    }

    // creates blank lists
    public static List newBlankFacingLists() {
        Object[] list = new Object[EnumFacing.values().length];
        for (int i = 0; i < EnumFacing.values().length; ++i) {
            list[i] = Lists.newLinkedList();
        }

        return ImmutableList.copyOf(list);
    }


    // Join several baked models together (so they will be rendered on top of each other)
    @SuppressWarnings("unchecked")
    public static SimpleBakedModel join(IBakedModel... models) {
        if (models.length == 0) throw new IllegalArgumentException("Number of models must be > 0");

        IBakedModel m = models[0];
        SimpleBakedModel simpleBakedModel = new SimpleBakedModel(new LinkedList(), newBlankFacingLists(), m.isGui3d(), m.isAmbientOcclusion(), m.getTexture(), m.getItemCameraTransforms());

        for (IBakedModel model : models) {
            simpleBakedModel.getGeneralQuads().addAll(model.getGeneralQuads());
            for (EnumFacing enumFacing : EnumFacing.values()) {
                simpleBakedModel.getFaceQuads(enumFacing).addAll(model.getFaceQuads(enumFacing));
            }
        }

        return simpleBakedModel;
    }

    // Get the default model resource location for a block state
    // Used to put an entry into the model registry
    public static ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation((ResourceLocation) Block.blockRegistry.getNameForObject(state.getBlock()), (new DefaultStateMapper()).getPropertyString(state.getProperties()));
    }
}
