package com.rwtema.denseores;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockDenseOre extends ItemBlock {
    BlockDenseOre oreBlock;

    // construct an itemblock for the given block. (Note: no itemid!)
    public ItemBlockDenseOre(Block block) {
        super(block);
        // oreBlock should always be a BlockDenseOre
        oreBlock = (BlockDenseOre) block;
        this.setHasSubtypes(true);
    }

    public int getMetadata(int par1) {
        return par1;
    }

    // Adds the 'dense' qualifier to the base blocks name
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        int m = par1ItemStack.getItemDamage() & 15;
        if (!oreBlock.isValid(m))
            return "Invalid Ore";
        else {
            ItemStack temp = new ItemStack(oreBlock.getBlock(m), 1, oreBlock.entry[m].metadata);

            String p = ("" + StatCollector.translateToLocal("denseores.prefix")).trim();
            return p.replaceFirst("ORENAME",temp.getDisplayName());
        }
    }
}
