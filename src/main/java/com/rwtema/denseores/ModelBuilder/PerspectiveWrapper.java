package com.rwtema.denseores.ModelBuilder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class PerspectiveWrapper implements IPerspectiveAwareModel {
	final IBakedModel model;
	final IPerspectiveAwareModel parentPerspective;

	public PerspectiveWrapper(IBakedModel model, IPerspectiveAwareModel parentPerspective) {
		this.model = model;
		this.parentPerspective = parentPerspective;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> pair = parentPerspective.handlePerspective(cameraTransformType);
		return Pair.of(model, pair.getRight());
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
		return model.getQuads(state, side, rand);
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

	@Nonnull
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return model.getParticleTexture();
	}

	@Nonnull
	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {
		return model.getItemCameraTransforms();
	}

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return model.getOverrides();
	}
}
