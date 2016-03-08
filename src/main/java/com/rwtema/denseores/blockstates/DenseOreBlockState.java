package com.rwtema.denseores.blockstates;

import com.google.common.collect.ImmutableMap;
import com.rwtema.denseores.client.DynamicBlockModel;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DenseOreBlockState extends BlockState.StateImplementation {
    @SideOnly(Side.CLIENT)
    public DynamicBlockModel model;

    public DenseOreBlockState(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
        super(blockIn, propertiesIn);
    }

}
