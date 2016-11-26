package com.rwtema.denseores.blockaccess;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public class SingleBlockWorldAccess implements IBlockAccess {
	public static BlockPos CENTER_POS = new BlockPos(0, 128, 0);
	final IBlockState state;

	public SingleBlockWorldAccess(IBlockState state) {
		this.state = state;
	}

	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		return null;
	}

	@Override
	public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
		return 15 << 20 | 15 << 4;
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(@Nonnull BlockPos pos) {
		if (CENTER_POS.equals(pos))
			return state;

		return Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isAirBlock(@Nonnull BlockPos pos) {
		IBlockState state = getBlockState(pos);
		return state.getBlock().isAir(state, this, pos);
	}

	@Nonnull
	@Override
	public Biome getBiome(@Nonnull BlockPos pos) {
		return Biomes.PLAINS;
	}


	@Override
	public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
		return 0;
	}

	@Nonnull
	@Override
	public WorldType getWorldType() {
		return WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
		return getBlockState(pos).isSideSolid(this, pos, side);
	}


}
