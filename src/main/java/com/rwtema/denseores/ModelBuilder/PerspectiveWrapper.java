package com.rwtema.denseores.ModelBuilder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.List;

public class PerspectiveWrapper implements IPerspectiveAwareModel {
    final IFlexibleBakedModel model;
    final IPerspectiveAwareModel parentPerspective;

    public PerspectiveWrapper(IBakedModel model, IPerspectiveAwareModel parentPerspective) {
        if (model instanceof IFlexibleBakedModel) {
            this.model = (IFlexibleBakedModel) model;
        } else {
            this.model = new IFlexibleBakedModel.Wrapper(model, parentPerspective.getFormat());
        }
        this.parentPerspective = parentPerspective;
    }

    @Override
    public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IFlexibleBakedModel, Matrix4f> pair = parentPerspective.handlePerspective(cameraTransformType);
        return Pair.of(model, pair.getRight());
    }

    @Override
    public VertexFormat getFormat() {
        return parentPerspective.getFormat();
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
        return model.getFaceQuads(p_177551_1_);
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        return model.getGeneralQuads();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return model.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return model.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return model.getParticleTexture();
    }

    @Override
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return model.getItemCameraTransforms();
    }


}
