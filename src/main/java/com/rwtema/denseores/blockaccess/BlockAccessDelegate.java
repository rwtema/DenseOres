package com.rwtema.denseores.blockaccess;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAccessDelegate implements IBlockAccess {
    public IBlockAccess world;

    public BlockAccessDelegate(IBlockAccess world) {
        this.world = world;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return world.getTileEntity(pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return world.getCombinedLight(pos, lightValue);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return world.getBlockState(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).getBlock().isAir(this, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
        return world.getBiomeGenForCoords(pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean extendedLevelsInChunkCache() {
        return world.extendedLevelsInChunkCache();
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return world.getStrongPower(pos, direction);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public WorldType getWorldType() {
        return world.getWorldType();
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        Block block = getBlockState(pos).getBlock();
        return block != Blocks.air && block.isSideSolid(this, pos, side);
    }
}
