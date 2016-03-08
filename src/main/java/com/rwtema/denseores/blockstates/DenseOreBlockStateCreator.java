package com.rwtema.denseores.blockstates;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.rwtema.denseores.blocks.BlockDenseOre;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class DenseOreBlockStateCreator extends BlockState {
    public DenseOreBlockStateCreator(Block blockIn) {
        super(blockIn, BlockDenseOre.ORE_TYPE);
    }

    @Override
    protected StateImplementation createState(Block block, ImmutableMap<IProperty, Comparable> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
        return new DenseOreBlockState(block, properties);
    }
}
