package com.rwtema.denseores;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBlockDenseOre extends ItemBlock {
    public static ItemBlockDenseOre INSTANCE;
    BlockDenseOre oreBlock;

    // construct an itemblock for the given block. (Note: no itemid!)
    public ItemBlockDenseOre(Block block) {
        super(block);
        INSTANCE = this;
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
            return p.replaceFirst("ORENAME", temp.getDisplayName());
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return !oreBlock.isValid(stack.getItemDamage());
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityItem(world, location.posX, location.posY, location.posZ,
                new ItemStack(oreBlock.getNullOverride(world, new BlockPos(location)), itemstack.stackSize));
    }
}
