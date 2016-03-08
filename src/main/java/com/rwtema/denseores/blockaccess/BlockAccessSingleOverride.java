package com.rwtema.denseores.blockaccess;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockAccessSingleOverride extends BlockAccessDelegate {
    public IBlockState overrideState = Blocks.air.getDefaultState();
    public BlockPos overridePos = BlockPos.ORIGIN;


    public BlockAccessSingleOverride(IBlockAccess world, IBlockState overrideState, BlockPos overridePos) {
        super(world);
        this.overrideState = overrideState;
        this.overridePos = overridePos;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        if (overridePos.equals(pos))
            return null;

        return super.getTileEntity(pos);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (overridePos.equals(pos))
            return overrideState;

        return super.getBlockState(pos);
    }
}
