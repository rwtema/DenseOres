package com.rwtema.denseores.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;

import java.util.List;

public class EmptyBakedModel extends SimpleBakedModel {
    protected static final List<BakedQuad> EMPTY_QUADS = ImmutableList.of();
    protected static final boolean EMPTY_AMBIENTOCCLUSION = false;
    protected static final boolean EMPTY_GUI3D = false;
    @SuppressWarnings("deprecation")
    protected static final ItemCameraTransforms EMPTY_CAMERATRANSFORMS = ItemCameraTransforms.DEFAULT;
    protected static final List<List<BakedQuad>> EMPTY_FACE_QUADS = ImmutableList.<List<BakedQuad>>of(ImmutableList.<BakedQuad>of(), ImmutableList.<BakedQuad>of(), ImmutableList.<BakedQuad>of(), ImmutableList.<BakedQuad>of(), ImmutableList.<BakedQuad>of(), ImmutableList.<BakedQuad>of());

    public EmptyBakedModel() {
        super(EMPTY_QUADS, EMPTY_FACE_QUADS, EMPTY_AMBIENTOCCLUSION, EMPTY_GUI3D, Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(), EMPTY_CAMERATRANSFORMS);
    }
}