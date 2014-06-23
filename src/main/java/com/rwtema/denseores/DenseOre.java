package com.rwtema.denseores;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/*
 * Dense ore entry
 * 
 * holds data for when we need it
 */
public class DenseOre {
    String baseBlock;
    int metadata;
    double prob;
    String underlyingBlock;
    String texture;
    int id;
    int retroGenId;

    public DenseOre(int id, String baseBlock, int metadata, double prob, String underlyingBlock, String texture, int retroGenId) {
        this.id = id;
        this.baseBlock = baseBlock;
        this.metadata = metadata;
        this.prob = prob;
        this.underlyingBlock = underlyingBlock;
        this.texture = texture;
        this.retroGenId = retroGenId;
    }

    public BlockDenseOre getBlock() {
        return block;
    }

    public void setBlock(BlockDenseOre block) {
        this.block = block;
    }

    BlockDenseOre block;

    public Block getBaseBlock() {
        if (Block.blockRegistry.containsKey(baseBlock))
            return Block.getBlockFromName(baseBlock);
        return null;
    }

    public ItemStack newStack(int stacksize) {
        return new ItemStack(getBaseBlock(), stacksize, metadata);
    }

}