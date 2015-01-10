package com.rwtema.denseores;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;

/*
 * Dense ore entry
 * 
 * holds data for when we need it
 */
public class DenseOre {
    public int rendertype;
    public String baseBlock;
    public String modOwner;
    public int metadata;
    public double prob;
    public String underlyingBlock;
    public String underlyingBlocktexture;
    public String texture;
    public String[] textureOverlays;
    public int id;
    public int retroGenId;
    public String baseOreDictionary = "";
    public String oreDictionary = "";

    public DenseOre(int id, String baseBlock, int metadata, double prob, String underlyingBlock, String texture, int retroGenId, int renderType) {
        this.id = id;
        this.baseBlock = baseBlock;
        this.metadata = metadata;
        this.prob = prob;
        this.underlyingBlock = underlyingBlock;
        this.underlyingBlocktexture = underlyingBlock;
        this.texture = texture;
        this.retroGenId = retroGenId;
        this.modOwner = baseBlock.substring(0, baseBlock.indexOf(58));
        this.textureOverlays = textureOverlays;
        this.rendertype = renderType;
    }

    public BlockDenseOre getBlock() {
        return block;
    }

    public void setBlock(BlockDenseOre block) {
        this.block = block;
    }

    BlockDenseOre block;

    public Block getBaseBlock() {
        if (Block.blockRegistry.containsKey(new ResourceLocation(baseBlock)))
            return Block.getBlockFromName(baseBlock);
        return null;
    }

    public ItemStack newStack(int stacksize) {
        return new ItemStack(getBaseBlock(), stacksize, metadata);
    }


    private ItemStack smelt;
    boolean initSmelt = false;


    public ItemStack getSmeltingRecipe() {
        if (initSmelt)
            return smelt;

        initSmelt = true;
        ItemStack out = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(getBaseBlock(), 1, metadata));

        if (out != null) {
            out = out.copy();
            out.stackSize = (int) (out.stackSize * 3);
            if (out.stackSize > 64)
                out.stackSize = 64;
            else if (out.stackSize < 1)
                out.stackSize = 1;
        }
        
        smelt = out;
        
        return out;
    }
}
