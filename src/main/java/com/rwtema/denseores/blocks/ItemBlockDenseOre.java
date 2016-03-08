package com.rwtema.denseores.blocks;

import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blockstates.OreType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBlockDenseOre extends ItemBlock {
    BlockDenseOre oreBlock;

    public ItemBlockDenseOre(Block block) {
        super(block);
        oreBlock = (BlockDenseOre) block;
        this.setHasSubtypes(true);
    }

    public int getMetadata(int par1) {
        return par1;
    }

    // Adds the 'dense' qualifier to the base blocks name
    public String getItemStackDisplayName(ItemStack stack) {
        OreType oreType = OreType.get(stack.getItemDamage());
        if (!oreBlock.isValid())
            return "Invalid Ore";
        else {
            ItemStack temp = new ItemStack(oreBlock.getBlock(), 1, oreBlock.denseOre.metadata);

            String p = ("" + StatCollector.translateToLocal("denseores." + oreType.getName() + ".prefix")).trim();
            return p.replaceFirst("ORENAME", temp.getDisplayName());
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return !oreBlock.isValid();
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityItem(world, location.posX, location.posY, location.posZ,
                new ItemStack(oreBlock.getNullOverride(world, new BlockPos(location)), itemstack.stackSize));
    }
}
