package com.rwtema.denseores;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blocks.ItemBlockDenseOre;
import com.rwtema.denseores.blockstates.OreType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;

/*
 * Dense ore entry
 * 
 * holds data for when we need it
 */
public class DenseOre {
    public final String name;

    public int rendertype;
    public String baseBlock;
    public int metadata;
    public String underlyingBlockTexture;
    public String texture;

    public int retroGenId;


    public BlockDenseOre block;
    public ItemBlockDenseOre itemBlock;

    boolean initSmelt = false;
    private ItemStack smelt;

    @SideOnly(Side.CLIENT)
    public EnumMap<OreType, TextureAtlasSprite> sprites;
    public String baseOreDictionaryEntry;


    public DenseOre(String name, String baseBlock, int metadata, String underlyingBlock, String texture, int retroGenId, int renderType) {
        this.name = name;
        this.baseBlock = baseBlock;
        this.metadata = metadata;
        this.underlyingBlockTexture = underlyingBlock;
        this.texture = texture;
        this.retroGenId = retroGenId;

        this.rendertype = renderType;
    }

    public void setBlock(BlockDenseOre block) {
        this.block = block;
        itemBlock = (ItemBlockDenseOre) Item.getItemFromBlock(block);
    }

    public Block getBaseBlock() {
        if (Block.blockRegistry.containsKey(new ResourceLocation(baseBlock)))
            return Block.getBlockFromName(baseBlock);
        return Blocks.air;
    }

    public ItemStack newStack(int stacksize) {
        return new ItemStack(getBaseBlock(), stacksize, metadata);
    }

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

    public IBlockState getBaseState() {
        return block.getBaseBlockState();
    }
}
