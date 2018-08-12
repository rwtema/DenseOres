package com.rwtema.denseores;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blocks.ItemBlockDenseOre;
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

import javax.annotation.Nullable;

/*
 * Dense ore entry
 * 
 * holds data for when we need it
 */
public class DenseOre {
	public final String unofficialName;
	public final ResourceLocation name;

	public int rendertype;
	public ResourceLocation baseBlock;
	public int metadata;

	public String underlyingBlockTexture;
	@Nullable
	public String texture;

	public int retroGenId;


	public BlockDenseOre block;
	public ItemBlockDenseOre itemBlock;
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite sprite;
	public String baseOreDictionaryEntry;
	boolean initSmelt = false;
	private ItemStack smelt;
	public String oreDictionary;


	public DenseOre(String unofficialName, ResourceLocation name, ResourceLocation baseBlock, int metadata, String underlyingBlock, @Nullable String texture, int retroGenId, int renderType) {
		this.unofficialName = unofficialName;
		this.name = name;
		this.baseBlock = new ResourceLocation(baseBlock.toString().toLowerCase());
		this.metadata = metadata;
		this.underlyingBlockTexture = underlyingBlock.toString().toLowerCase();
		this.texture = texture.toString().toLowerCase();
		this.retroGenId = retroGenId;
		this.rendertype = renderType;
		this.block = new BlockDenseOre(this);
		this.block.setRegistryName(this.name);
		this.block.setTranslationKey(this.name.toString());
		this.itemBlock = new ItemBlockDenseOre(this.block);
		this.itemBlock.setRegistryName(this.name);
		this.itemBlock.setTranslationKey(this.name.toString());
	}

	public Block getBaseBlock() {
		if (Block.REGISTRY.containsKey(baseBlock))
			return Block.REGISTRY.getObject(baseBlock);

		return Blocks.AIR;
	}

	public ItemStack newStack(int stacksize) {
		return new ItemStack(getBaseBlock(), stacksize, metadata);
	}

	@Override
	public String toString() {
		return "DenseOre{" +
				"unofficialName='" + unofficialName + '\'' +
				", name=" + name +
				", rendertype=" + rendertype +
				", baseBlock=" + baseBlock +
				", metadata=" + metadata +
				", underlyingBlockTexture='" + underlyingBlockTexture + '\'' +
				", texture='" + texture + '\'' +
				", retroGenId=" + retroGenId +
				'}';
	}

	public ItemStack getSmeltingRecipe() {
		if (initSmelt)
			return smelt;

		initSmelt = true;
		ItemStack out = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(getBaseBlock(), 1, metadata));

		if (out.isEmpty()) {
			out = out.copy();
			out.setCount(Math.min(3, out.getMaxStackSize()));
		}

		smelt = out;

		return out;
	}

	public IBlockState getBaseState() {
		return block.getBaseBlockState();
	}
}
