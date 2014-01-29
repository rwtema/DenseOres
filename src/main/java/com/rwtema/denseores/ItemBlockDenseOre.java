package com.rwtema.denseores;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockDenseOre extends ItemBlock {

	BlockDenseOre b;

	public ItemBlockDenseOre(Block p_i45328_1_) {
		super(p_i45328_1_);
		b = (BlockDenseOre) p_i45328_1_;
	}

	public int getMetadata(int par1) {
		return par1;
	}

	public String getItemStackDisplayName(ItemStack par1ItemStack) {
		int m = par1ItemStack.getItemDamage() & 15;
		if (!b.isValid(m))
			return "Invalid Ore";
		else {
			ItemStack temp = new ItemStack(b.getBlock(m), 1, b.entry[m].metadata);
			return "Dense " + temp.getDisplayName();
		}
	}

}
