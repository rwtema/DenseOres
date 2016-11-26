package com.rwtema.denseores.blockaccess;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BlockAccessDelegate implements IBlockAccess {
	public IBlockAccess world;

	public BlockAccessDelegate(IBlockAccess world) {
		this.world = world;
	}

	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		return world.getTileEntity(pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
		return world.getCombinedLight(pos, lightValue);
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(@Nonnull BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(@Nonnull BlockPos pos) {
		IBlockState state = this.getBlockState(pos);
		return state.getBlock().isAir(state, this, pos);
	}

	@Nonnull
	@Override
	public Biome getBiome(@Nonnull BlockPos pos) {
		return world.getBiome(pos);
	}

	@Override
	public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
		return world.getStrongPower(pos, direction);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public WorldType getWorldType() {
		return world.getWorldType();
	}

	@Override
	public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
		IBlockState state = getBlockState(pos);
		Block block = state.getBlock();
		return block != Blocks.AIR && state.isSideSolid(this, pos, side);
	}
}
