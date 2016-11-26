package com.rwtema.denseores.blockaccess;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

public class BlockAccessSingleOverride extends BlockAccessDelegate {
	public IBlockState overrideState = Blocks.AIR.getDefaultState();
	public BlockPos overridePos = BlockPos.ORIGIN;

	public BlockAccessSingleOverride(IBlockAccess world, IBlockState overrideState, BlockPos overridePos) {
		super(world);
		this.overrideState = overrideState;
		this.overridePos = overridePos;
	}

	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		if (overridePos.equals(pos))
			return null;

		return super.getTileEntity(pos);
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(@Nonnull BlockPos pos) {
		if (overridePos.equals(pos))
			return overrideState;

		return super.getBlockState(pos);
	}
}
