package com.rwtema.denseores.blocks;


import com.google.common.base.Throwables;
import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresMod;
import com.rwtema.denseores.Proxy;
import com.rwtema.denseores.blockaccess.BlockAccessSingleOverride;
import com.rwtema.denseores.blockstates.DenseOreBlockStateCreator;
import com.rwtema.denseores.blockstates.OreType;
import com.rwtema.denseores.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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

    public static final PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("Ore Type", OreType.class);
    public boolean init = false;

    DenseOre denseOre;
    // Ore Entry stuff
    IBlockState baseBlockState;
    private boolean isValid;
    private Block baseBlock;

    public BlockDenseOre(DenseOre denseOre) {
        this.denseOre = denseOre;
        setCreativeTab(CreativeTabs.tabBlock);
    }

    public static Block getBlock(String name) {
        return GameData.getBlockRegistry().getObject(new ResourceLocation(name));
    }

    public static Block getBlock(DenseOre ore) {
        return ore != null ? getBlock(ore.baseBlock) : null;
    }

    public Block getUnderlyingBlock(IBlockAccess world, BlockPos pos) {
        if("blocks/stone".equals(denseOre.underlyingBlockTexture)){
            return Blocks.stone;
        }
        if("blocks/netherrack".equals(denseOre.underlyingBlockTexture)){
            return Blocks.netherrack;
        }
        if("blocks/end_stone".equals(denseOre.underlyingBlockTexture)){
            return Blocks.end_stone;
        }

        return getNullOverride(world, pos);
    }

    public static Block getNullOverride(IBlockAccess world, BlockPos pos) {
        if (world == null)
            return Blocks.stone;

        BiomeGenBase biome = world.getBiomeGenForCoords(pos);
        if (biome == BiomeGenBase.hell)
            return Blocks.netherrack;

        if (biome == BiomeGenBase.sky)
            return Blocks.end_stone;

        return getNullOverride(world);
    }

    public static Block getNullOverride(IBlockAccess blockAccess) {
        if (!(blockAccess instanceof World))
            return Blocks.stone;

        World world = (World) blockAccess;

        return getBlock(world);
    }

    public static Block getBlock(World world) {
        if (world.provider == null)
            return Blocks.stone;

        if (world.provider.getDimensionId() == -1)
            return Blocks.netherrack;

        if (world.provider.getDimensionId() == 1)
            return Blocks.end_stone;

        return Blocks.stone;
    }

    public boolean isValid() {
        if (!init) init();
        return isValid;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ORE_TYPE, OreType.get(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(ORE_TYPE).ordinal();
    }

    public void init() {
        init = true;

        baseBlock = denseOre.getBaseBlock();
        baseBlockState = baseBlock.getStateFromMeta(denseOre.metadata);

        isValid = baseBlock != null && baseBlock != Blocks.air;
    }

    protected BlockState createBlockState() {

        return new DenseOreBlockStateCreator(this);
    }

    public Block getBlock() {
        if (!init) init();
        return baseBlock;
    }

    public IBlockState getBaseBlockState() {
        if (!init) init();
        return baseBlockState;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!isValid())
            return;

        try {
            world.setBlockState(pos, getBaseBlockState(), 0);
            for (int i = 0; i < 1 + rand.nextInt(3); i++)
                getBlock().randomDisplayTick(world, pos, getBaseBlockState(), rand);
        } finally {
            world.setBlockState(pos, state, 0);
        }
    }

    // add creative blocks
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {

        if (isValid()) {
            for (int i = 0; i < OreType.values().length; i++) {
                list.add(new ItemStack(item, 1, i));
            }
        }
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
        if (isValid()) {
            IBlockState base = getBaseBlockState();

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


    // get drops
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();

        OreType oreType = state.getValue(ORE_TYPE);

        if (isValid()) {
            Block base = getBlock();

            if (base == null)
                return list;

            IBlockState m = getBaseBlockState();

            BlockAccessSingleOverride delegate = new BlockAccessSingleOverride(world, m, pos);

            Random rand = world instanceof World ? ((World) world).rand : RANDOM;

            oreType.addDrops(list, denseOre, world, pos, base, m, delegate, fortune, rand);
        } else {
            Block block = getNullOverride(world, pos);
            BlockAccessSingleOverride delegate = new BlockAccessSingleOverride(world, block.getDefaultState(), pos);
            return block.getDrops(delegate, pos, block.getDefaultState(), fortune);
        }
        return list;
    }

    // get hardness
    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) return 1;



        if (!isValid())
            return 1;

        OreType type = state.getValue(ORE_TYPE);


        TileEntity tile = world.getTileEntity(pos);
        try {
            world.setBlockState(pos, getBaseBlockState(), 0);
            float blockHardness = getBlock().getBlockHardness(world, pos);
            world.setBlockState(pos, state, 0);
            if (tile != null) {
                NBTTagCompound tag = new NBTTagCompound() ;
                tile.writeToNBT(tag);
                TileEntity newTile = world.getTileEntity(pos);
                newTile.readFromNBT(tag);
            }

            return type.transfromHardness(denseOre, type, world, pos, tile, blockHardness);
        } catch (Throwable throwable) {
            world.setBlockState(pos, state, 0);
            if (tile != null) {
                NBTTagCompound tag = new NBTTagCompound() ;
                tile.writeToNBT(tag);
                TileEntity newTile = world.getTileEntity(pos);
                newTile.readFromNBT(tag);
            }

            throw Throwables.propagate(throwable);
        }
    }

    @Override
    public int getExpDrop(IBlockAccess iBlockAccess, BlockPos pos, int fortune) {
        if (!(iBlockAccess instanceof World) || !isValid()) return 0;

        World world = ((World) iBlockAccess);

        IBlockState state = world.getBlockState(pos);

        OreType oreType = state.getValue(ORE_TYPE);

        IBlockState baseState = getBaseBlockState();
        BlockAccessSingleOverride delegate = new BlockAccessSingleOverride(iBlockAccess, baseState, pos);
        return oreType.getExpDrops(denseOre, world, pos, fortune, baseState, delegate);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return state.getBlock() == this && isValid() && state.getValue(ORE_TYPE).canSilkHarvest(world, pos, player);
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        DenseOresMod.proxy.loadModel(this, state, worldIn, pos);
        return state;
    }

    @Override
    public int getDamageValue(World worldIn, BlockPos pos) {
        return this.damageDropped(worldIn.getBlockState(pos));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(ORE_TYPE).hasTile();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if(world.isRemote) return null;
        return state.getValue(ORE_TYPE).createTile(world);
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {


        return world.getBlockState(pos).getValue(ORE_TYPE).removedByPlayer(world, pos, this, player, willHarvest);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        return state.getValue(ORE_TYPE).onBlockActivated(denseOre, worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        IBlockState baseState = getBaseBlockState();
        int harvestLevel = baseState.getBlock().getHarvestLevel(baseState);

        return state.getValue(ORE_TYPE).transformHarvestLevel(harvestLevel, this, baseState);
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        IBlockState baseState = getBaseBlockState();
        return baseState.getBlock().getHarvestTool(baseState);
    }


    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        player.triggerAchievement(StatList.mineBlockStatArray[getIdFromBlock(this)]);
        player.addExhaustion(0.025F);

        if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getSilkTouchModifier(player))
        {
            java.util.ArrayList<ItemStack> items = new java.util.ArrayList<ItemStack>();
            ItemStack itemstack = this.createStackedBlock(state);

            if (itemstack != null)
            {
                items.add(itemstack);
            }

            net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, worldIn.getBlockState(pos), 0, 1.0f, true, player);
            for (ItemStack stack : items)
            {
                spawnAsEntity(worldIn, pos, stack);
            }
        }
        else
        {
            harvesters.set(player);
            int i = EnchantmentHelper.getFortuneModifier(player);
            this.dropBlockAsItem(worldIn, pos, state, i);
            harvesters.set(null);
        }
    }

}
