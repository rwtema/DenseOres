package com.rwtema.denseores;


import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*  I'm using the MAX_METADATA metadata values to store each ore block.
 *  (We don't really need to worry about block ids in 1.7
 *   but that's no reason to be wasteful)
 */

public class BlockDenseOre extends BlockOre {

    public static int maxMetdata;
    public PropertyInteger METADATA;
    public IBakedModel[] models;
    public IBakedModel[] invmodels;
    // Ore Entry stuff
    public DenseOre[] entry;
    public Block[] baseBlocks;
    public boolean[] valid;
    public boolean init = false;
    public BlockDenseOre() {
        super();

        this.setDefaultState(this.blockState.getBaseState().withProperty(METADATA, 0));
        entry = new DenseOre[maxMetdata];
        baseBlocks = new Block[maxMetdata];
        valid = new boolean[maxMetdata];
    }

    public static Block getBlock(String name) {
        return GameData.getBlockRegistry().getObject(new ResourceLocation(name));
    }

    public static Block getBlock(DenseOre ore) {
        return ore != null ? getBlock(ore.baseBlock) : null;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(METADATA, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer) state.getValue(METADATA);
    }

    public void init() {
        init = true;

        for (int i = 0; i < maxMetdata; i++) {
            baseBlocks[i] = getBlock(entry[i]);
            valid[i] = baseBlocks[i] != null && baseBlocks[i] != Blocks.air;
        }
    }


    protected BlockState createBlockState() {
        METADATA = PropertyInteger.create("Type", 0, maxMetdata - 1);
        return new BlockState(this, METADATA);
    }

    public Block getBlock(int id) {
        if (!init)
            init();

        return baseBlocks[id];
    }

    public IBlockState getBaseBlockState(int id) {
        return entry[id].getBaseBlock().getStateFromMeta(entry[id].metadata);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int id = getMetaFromState(world.getBlockState(pos));

        if (!isValid(id))
            return;

        try {
            world.setBlockState(pos, getBaseBlockState(id), 0);
            for (int i = 0; i < 1 + rand.nextInt(3); i++)
                getBlock(id).randomDisplayTick(world, pos, getBaseBlockState(id), rand);
        } finally {
            world.setBlockState(pos, state, 0);
        }
    }

    public boolean isValid(IBlockState id) {
        return isValid(getMetaFromState(id));
    }

    public boolean isValid(int id) {
        if (!init)
            init();

        if (id < 0 || id >= maxMetdata)
            return false;

        return valid[id];
    }

    public void setEntry(int id, DenseOre ore) {
        this.entry[id] = ore;
    }

    public DenseOre getEntry(int id) {
        return entry[id];
    }

    // add creative blocks
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < maxMetdata; i++)
            if (isValid(i))
                list.add(new ItemStack(item, 1, i));

    }

    public Block getNullOverride(IBlockAccess world, BlockPos pos) {
        if (world == null)
            return Blocks.stone;

        BiomeGenBase biome = world.getBiomeGenForCoords(pos);
        if (biome == BiomeGenBase.hell)
            return Blocks.netherrack;

        if (biome == BiomeGenBase.sky)
            return Blocks.end_stone;

        return getNullOverride(world);
    }

    public Block getNullOverride(IBlockAccess blockAccess) {
        if (!(blockAccess instanceof World))
            return Blocks.stone;

        World world = (World) blockAccess;

        if (world.provider == null)
            return Blocks.stone;

        if (world.provider.getDimensionId() == -1)
            return Blocks.netherrack;

        if (world.provider.getDimensionId() == 1)
            return Blocks.end_stone;

        return Blocks.stone;
    }

    // register icons
    public void registerBlockIcons(TextureMap register) {


    }

    // drop the block with a predefined chance
    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (worldIn.isRemote || worldIn.restoringBlockSnapshots)// do not drop items while restoring blockstates, prevents item dupe
            return;

        List<ItemStack> items = getDrops(worldIn, pos, state, fortune);
        chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get());

        if (chance == 0) return;

        // now call the forge events to see if our base ore block should be dropped
        if (isValid(state)) {
            IBlockState base = getBaseBlockState(getMetaFromState(state));

            if (base != null) {
                chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, base, fortune, chance, false, harvesters.get());
            }
        }

        if (chance == 0) return;

        for (ItemStack item : items) {
            if (worldIn.rand.nextFloat() <= chance) {
                spawnAsEntity(worldIn, pos, item);
            }
        }
    }

    public Block getBlock(IBlockState state) {
        return getBlock(getMetaFromState(state));
    }


    // get drops
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        int meta = getMetaFromState(state);

        if (isValid(meta)) {
            Block base = getBlock(meta);

            if (base == null)
                return ret;

            IBlockState m = getBaseBlockState(meta);

            // get base drops 3 times
            for (int j = 0; j < 3; j++) {
                ret.addAll(base.getDrops(world, pos, m, fortune));
            }
        } else {
            Block block = getNullOverride(world, pos);
            return block.getDrops(world, pos, block.getDefaultState(), fortune);
        }
        return ret;
    }

    // get hardness
    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) return 1;
        int id = getMetaFromState(state);

        if (!isValid(id))
            return 1;

        try {
            world.setBlockState(pos, getBaseBlockState(id), 0);
            return getBlock(id).getBlockHardness(world, pos);
        } finally {
            world.setBlockState(pos, state, 0);
        }
    }

    @Override
    public int getExpDrop(IBlockAccess iBlockAccess, BlockPos pos, int fortune) {
        if (!(iBlockAccess instanceof World))
            return super.getExpDrop(iBlockAccess, pos, fortune);

        World world = ((World) iBlockAccess);

        IBlockState state = world.getBlockState(pos);
        int id = getMetaFromState(state);

        if (!isValid(id))
            return super.getExpDrop(iBlockAccess, pos, fortune);

        try {
            world.setBlockState(pos, getBaseBlockState(id), 0);
            return getBlock(id).getExpDrop(world, pos, fortune) * 3;
        } finally {
            world.setBlockState(pos, state, 0);
        }

    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
}
