package com.rwtema.denseores.blockstates;

import com.google.common.collect.Maps;
import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.blocks.TileDepositLevel;
import com.rwtema.denseores.WorldGenOres;
import com.rwtema.denseores.blockaccess.BlockAccessSingleOverride;
import com.rwtema.denseores.utils.ColorHelper;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public enum OreType implements IStringSerializable {
    DENSE {
        public void addDrops(ArrayList<ItemStack> list, DenseOre denseOre, IBlockAccess world, BlockPos pos, Block base, IBlockState blockState, BlockAccessSingleOverride delegate, int fortune, Random rand) {
            // get base drops 3 times
            for (int j = 0; j < 3; j++) {
                list.addAll(base.getDrops(delegate, pos, blockState, fortune));
            }
        }

        public int getExpDrops(DenseOre denseOre, World world, BlockPos pos, int fortune, IBlockState baseState, BlockAccessSingleOverride delegate) {
            return getBaseExp(pos, fortune, baseState, delegate) * 3;
        }

        @Override
        public void registerOre(DenseOre ore, String oreName) {
            OreDictionary.registerOre("dense" + oreName, new ItemStack(ore.block, 1, ordinal()));
        }

        @SideOnly(Side.CLIENT)
        public void assignOreTexData(int w, int[] ore_data, int[] stone_data, int renderType, int[] new_data, boolean[] same) {
            int[] dx;
            int[] dy;

            //allows for different convolution filters
            Offset offset = Offset.getOffset(renderType);
            dx = offset.dx;
            dy = offset.dy;

            // where the magic happens
            for (int i = 0; i < ore_data.length; i += 1) {
                int x = (i % w);
                int y = (i - x) / w;

                // if the pixel an ore pixel, we don't need to do anything so continue
                if (!same[i])
                    continue;

                // use our convolution filter to see if we can find an ore pixel nearby
                for (int j = 0; j < dx.length; j++) {
                    final int new_x = x + dx[j];
                    final int new_y = y + dy[j];

                    if (new_x >= 0 && new_x < w && new_y >= 0 && new_y < w) // is valid pixel location
                        if (!same[new_x + new_y * w]) { // is it an ore pixel?
                            new_data[i] = ore_data[new_x + new_y * w];
                            break;
                        }
                }
            }
        }

        public void generate(Chunk chunk, Random random, DenseOre denseOre, boolean retroGen) {
            IBlockState replaceState = denseOre.block.getDefaultState().withProperty(BlockDenseOre.ORE_TYPE, this);
            for (int i = 0; i < 1000; i++) {
                int x = (chunk.xPosition << 4) | random.nextInt(16);
                int y = 1 + random.nextInt(80);
                int z = (chunk.zPosition << 4) | random.nextInt(16);

                BlockPos pos = new BlockPos(x, y, z);

                IBlockState state = chunk.getBlockState(pos);

                if (state == denseOre.getBaseState())
                    WorldGenOres.overrideChunkBlock(chunk, pos, replaceState, retroGen);
            }
        }
    },
    SPARSE {
        @Override
        public void assignOreTexData(int w, int[] ore_data, int[] stone_data, int renderType, int[] new_data, boolean[] same) {
            Random rnd = new Random(0);

            boolean[] backBlock = new boolean[ore_data.length];
            int r = (int) Math.ceil(w / 16.0F);

            TIntHashSet borderValues = new TIntHashSet();
            for (int i = 0; i < ore_data.length; i++) {
                if (!same[i]) continue;

                backBlock[i] = true;
                int x = (i % w);
                int y = (i - x) / w;
                for (int dx = -r; dx <= r; dx++) {
                    for (int dy = -r; dy <= r; dy++) {
                        int x2 = x + dx;
                        int y2 = y + dy;
                        if (x2 >= 0 && x2 < w && y2 >= 0 && y2 < w) {
                            int j = y2 * w + x2;
                            if (!same[j]) {
                                borderValues.add(j);
                            }
                        }
                    }
                }
            }

            int[] border = borderValues.toArray();

            for (int i = border.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                int a = border[index];
                border[index] = border[i];
                border[i] = a;
            }

            int[] dx = new int[]{-1, 1};
            int[] dy = new int[]{1, 0};

            for (int i : border) {
                int x = (i % w);
                int y = (i - x) / w;
                boolean alone = true;
                for (int j = 0; j < dx.length; j++) {
                    int x2 = x + dx[j];
                    int y2 = y + dy[j];
                    if (x2 >= 0 && x2 < w && y2 >= 0 && y2 < w) {
                        int k = y2 * w + x2;
                        if (!backBlock[k]) {
                            alone = false;
                        }
                    }

                }
                if (!alone) {
                    backBlock[i] = true;
                }
            }

            for (int i = 0; i < ore_data.length; i++) {
                if (backBlock[i])
                    new_data[i] = stone_data[i];
                else
                    new_data[i] = ore_data[i];
            }
        }

        @Override
        public void addDrops(ArrayList<ItemStack> list, DenseOre denseOre, IBlockAccess world, BlockPos pos, Block base, IBlockState blockState, BlockAccessSingleOverride delegate, int fortune, Random rand) {
            List<ItemStack> drops = base.getDrops(delegate, pos, blockState, fortune);
            if (!drops.isEmpty()) {
                for (ItemStack drop : drops) {
                    if (drop == null || drop.stackSize == 0) continue;
                    int i = rand.nextInt(1 + drop.stackSize);
                    if (i == 0) continue;
                    i = rand.nextInt(1 + i);
                    if (i == 0) continue;


                    drop = drop.copy();
                    drop.stackSize = i;
                    list.add(drop);
                }
            }
            List<ItemStack> nuggets = this.nuggets.get(denseOre);
            if (!nuggets.isEmpty()) {
                ItemStack itemStack = nuggets.get(0);
                list.add(itemStack.copy());
            }

            if (list.isEmpty()) {
                Block block = denseOre.block.getUnderlyingBlock(world, pos);
                IBlockState state = block.getDefaultState();
                list.addAll(block.getDrops(new BlockAccessSingleOverride(world, state, pos), pos, state, fortune));
            }
        }

        HashMap<DenseOre, List<ItemStack>> nuggets = Maps.newHashMap();

        @Override
        public void registerOre(DenseOre ore, String oreName) {
            String s = "nugget" + oreName.substring(3);
            nuggets.put(ore, OreDictionary.getOres(s));
        }

        @Override
        public int getExpDrops(DenseOre denseOre, World world, BlockPos pos, int fortune, IBlockState baseState, BlockAccessSingleOverride delegate) {
            return getBaseExp(pos, fortune, baseState, delegate) >> 2;
        }

        @Override
        public float transfromHardness(DenseOre denseOre, OreType type, World world, BlockPos pos, TileEntity tile, float blockHardness) {
            return Math.max(1F, blockHardness / 2);
        }

        @Override
        public void generate(Chunk chunk, Random random, DenseOre denseOre, boolean retroGen) {
            IBlockState replaceState = denseOre.block.getDefaultState().withProperty(BlockDenseOre.ORE_TYPE, this);

            for (int i = 0; i < 15; i++) {
                int x = (chunk.xPosition << 4) | (3 + random.nextInt(16-3));
                int y = 4 + random.nextInt(80);
                int z = (chunk.zPosition << 4) | (3 + random.nextInt(16-3));

                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = chunk.getBlockState(pos);

                if (state == denseOre.getBaseState()) {
                    for (BlockPos.MutableBlockPos offsetPos : BlockPos.getAllInBoxMutable(pos.add(-3, -3, -3), pos.add(3, 3, 3))) {
                        if (random.nextInt(2) == 0) continue;
                        Block block = chunk.getBlock(offsetPos);
                        if (block == denseOre.block.getUnderlyingBlock(chunk.getWorld(), pos)) {
                            WorldGenOres.overrideChunkBlock(chunk, offsetPos, replaceState, retroGen);
                        }
                    }
                }
            }
        }
    },
    DEPOSIT {
        @Override
        public float transfromHardness(DenseOre denseOre, OreType type, World world, BlockPos pos, TileEntity tile, float blockHardness) {
            if(tile instanceof TileDepositLevel){
                return blockHardness * MathHelper.sqrt_float(((TileDepositLevel) tile).num);
            }
            return blockHardness * 8;
        }

        @Override
        public void assignOreTexData(int w, int[] ore_data, int[] stone_data, int renderType, int[] new_data, boolean[] same) {

            Offset offset = Offset.getOffset(renderType);
            int[] tx = offset.dx;
            int[] ty = offset.dy;
            Random rand = new Random();

            int r = w >> 3;

            TIntArrayList diffColors = new TIntArrayList();
            mainLoop:
            for (int i = 0; i < ore_data.length; i++) {
                if (!same[i]) {
                    int x = i % w;
                    for (int dx = -r; dx <= r; dx++) {
                        if ((x + dx) <= 0 || (x + dx) > w)
                            continue;
                        for (int dy = -(r * w); dy <= (r * w); dy += w) {
                            int k = i + dx + dy;

                            if (k < 0 || k >= stone_data.length)
                                continue;

                            if (ColorHelper.areColorsClose(ore_data[i], stone_data[k])) {
                                same[i] = true;
                                continue mainLoop;
                            }
                        }
                    }

                    diffColors.add(ore_data[i]);
                }
            }

            if (diffColors.isEmpty()) return;

            mainLoop:
            for (int i = 0; i < ore_data.length; i++) {
                if (!same[i]) {
                    continue;
                }

                int x = (i % w);
                int y = (i - x) / w;

                for (int j = 0; j < tx.length; j++) {
                    int x2 = x + tx[j];
                    int y2 = y + ty[j];

                    if (x2 >= 0 && x2 < w && y2 >= 0 && y2 < w) {
                        int k = x2 + y2 * w;
                        if (!same[k]) {
                            new_data[i] = ColorHelper.multiply(ore_data[k], 0.975F);
                            continue mainLoop;
                        }
                    }
                }

                Random rnd = new Random(0);

                for (int r2 = 1; r2 < w; r2++) {
                    for (int j = 0; j < r2; j++) {
                        int x2 = x + rnd.nextInt(r2 * 2 + 1) - (r2);
                        int y2 = y + rnd.nextInt(r2 * 2 + 1) - (r2);
                        if (x2 >= 0 && x2 < w && y2 >= 0 && y2 < w) {
                            int k = y2 * w + x2;
                            if (!same[k]) {
                                new_data[i] = ColorHelper.multiply(ore_data[k], 0.95F);
                                continue mainLoop;
                            }
                        }
                    }
                }


                for (int j = 0; j < same.length; j++) {
                    int k = (i + j) % ore_data.length;
                    if (!same[k]) {
                        new_data[i] = ColorHelper.multiply(ore_data[k], 0.95F);
                        continue mainLoop;
                    }
                }

//                new_data[i] = ore_data[0];
            }

        }

        @Override
        public void addDrops(ArrayList<ItemStack> list, DenseOre denseOre, IBlockAccess world, BlockPos pos, Block base, IBlockState blockState, BlockAccessSingleOverride delegate, int fortune, Random rand) {
            list.addAll(base.getDrops(delegate, pos, blockState, fortune));
        }

        @Override
        public int getExpDrops(DenseOre denseOre, World world, BlockPos pos, int fortune, IBlockState baseState, BlockAccessSingleOverride delegate) {
            return getBaseExp(pos, fortune, baseState, delegate);
        }



        @Override
        public void generate(Chunk chunk, Random random, DenseOre denseOre, boolean retroGen) {
            if(random.nextInt(32) != 0) return;
            for (int dv = 0; dv < 1; dv++) {
                IBlockState oreState = denseOre.getBaseState();
                int x = (chunk.xPosition << 4) | (random.nextInt(16));
                int y = 1 + random.nextInt(256);
                int z = (chunk.zPosition << 4) | (random.nextInt(16));

                BlockPos pos = new BlockPos(x, y, z);

                IBlockState state = chunk.getBlockState(pos);

                if (state != oreState) continue;

                IBlockState depositState = denseOre.block.getDefaultState().withProperty(BlockDenseOre.ORE_TYPE, this);
                IBlockState rawState = BlockDenseOre.getNullOverride(chunk.getWorld()).getDefaultState();
                Block rawBlock = rawState.getBlock();
                IBlockState denseState = denseOre.block.getDefaultState().withProperty(BlockDenseOre.ORE_TYPE, DENSE);

                for (int v = 0; v < 10; v++) {
                    x = (chunk.xPosition << 4) | (4 + random.nextInt(8));
                    z = (chunk.zPosition << 4) | (4 + random.nextInt(8));

                    for (int j = 1; j < 20; j++) {
                        if (chunk.getWorld().provider.getHasNoSky())
                            y = random.nextInt(256);
                        else
                            y = chunk.getHeightValue(x & 15, z & 15) - j;

                        Block block = chunk.getBlock(x, y, z);
                        if (block != Blocks.grass && block != Blocks.stone && block != Blocks.dirt && block != rawBlock) {
                            continue;
                        }

                        TObjectIntHashMap<BlockPos> vals = new TObjectIntHashMap<BlockPos>(10, 0.5F, 0);
                        BlockPos p = new BlockPos(x, y, z);
                        WorldGenOres.overrideChunkBlock(chunk, p, depositState, retroGen);
                        vals.put(p, 4 + random.nextInt(5));
                        LinkedList<BlockPos> toProcess = new LinkedList<BlockPos>();

                        synchronized (deposit_positions) {
                            if (deposit_positions.size() > 10) {
                                deposit_positions.removeFirst();
                            }
                            deposit_positions.addLast(p);
                        }

                        toProcess.add(p);
                        BlockPos t;
                        while ((t = toProcess.poll()) != null) {
                            int i = vals.get(t);

                            for (EnumFacing facing : EnumFacing.values()) {
                                int i2 = vals.get(t.offset(facing)) - 1;
                                if (i2 > i)
                                    i = i2;
                            }

                            for (EnumFacing facing : EnumFacing.values()) {
                                BlockPos offset = t.offset(facing);
                                if (vals.containsKey(offset))
                                    continue;


                                Block b = chunk.getBlock(offset);
                                if (b != Blocks.grass &&
                                        b != Blocks.stone &&
                                        b != Blocks.dirt &&
                                        b != Blocks.gravel &&
                                        b != Blocks.air &&
                                        b != rawBlock
                                        ) {
                                    vals.put(offset, 0);
                                    continue;
                                }

                                int dx = offset.getX() & 15;
                                if (dx == 0 || dx == 15) {
                                    vals.put(offset, 0);
                                    if (b != Blocks.air)
                                        WorldGenOres.overrideChunkBlock(chunk, offset, rawState, retroGen);
                                    continue;
                                }
                                int dz = offset.getZ() & 15;
                                if (dz == 0 || dz == 15) {
                                    vals.put(offset, 0);
                                    if (b != Blocks.air)
                                        WorldGenOres.overrideChunkBlock(chunk, offset, rawState, retroGen);
                                    continue;
                                }

                                int i_temp = i;
                                i_temp -= 1 + random.nextInt(2);

                                if (facing.ordinal() <= 2) {
                                    i_temp -= random.nextInt(2);
                                }

                                if (i_temp < 0) i_temp = 0;

                                vals.put(offset, i_temp);
                                if (i_temp > 2) {
                                    toProcess.add(offset);
                                    WorldGenOres.overrideChunkBlock(chunk, offset, depositState, retroGen);
                                } else if (b != Blocks.air) {
                                    if (i_temp == 2) {
                                        toProcess.add(offset);
                                        WorldGenOres.overrideChunkBlock(chunk, offset, denseState, retroGen);
                                    } else if (i_temp == 1) {
                                        toProcess.add(offset);
                                        WorldGenOres.overrideChunkBlock(chunk, offset, oreState, retroGen);
                                    } else {
                                        WorldGenOres.overrideChunkBlock(chunk, offset, rawState, retroGen);
                                    }
                                }
                            }
                        }
                        return;
                    }
                }
            }
        }

        @Override
        public boolean hasTile() {
            return true;
        }

        @Override
        public int transformHarvestLevel(int harvestLevel, BlockDenseOre blockDenseOre, IBlockState baseState) {
            return 1 + harvestLevel;
        }

        @Override
        public TileEntity createTile(World world) {
            if (world.isRemote) return null;
            return new TileDepositLevel();
        }

        @Override
        public boolean canSilkHarvest(World world, BlockPos pos, EntityPlayer player) {
            return false;
        }

        @Override
        public boolean removedByPlayer(World world, BlockPos pos, BlockDenseOre blockDenseOre, EntityPlayer player, boolean willHarvest) {
            if (player != null && player.capabilities.isCreativeMode)
                return super.removedByPlayer(world, pos, blockDenseOre, player, willHarvest);

            if (world.isRemote) {
                return false;
            }


            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileDepositLevel) {
                if (((TileDepositLevel) tileEntity).dec()) {
                    if (willHarvest)
                        blockDenseOre.harvestBlock(world, player, pos, world.getBlockState(pos), tileEntity);
                    return false;
                }
            }

            return super.removedByPlayer(world, pos, blockDenseOre, player, willHarvest);
        }

        @Override
        public boolean onBlockActivated(DenseOre denseOre, World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
            if (worldIn.isRemote) return true;
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDepositLevel) {
                playerIn.addChatComponentMessage(new ChatComponentTranslation("denseores.deposit.num", ((TileDepositLevel) tile).num));
            }
            return true;
        }
    };

    public final String name = name().toLowerCase();
    public boolean generate;

    public static OreType get(int meta) {
        for (OreType oreType : values()) {
            if (oreType.ordinal() == meta) return oreType;
        }
        return DENSE;
    }

    public void registerOre(DenseOre ore, String oreName) {

    }

    @SideOnly(Side.CLIENT)
    public abstract void assignOreTexData(int w, int[] ore_data, int[] stone_data, int renderType, int[] new_data, boolean[] same);

    @Override
    public String getName() {
        return name;
    }

    public abstract void addDrops(ArrayList<ItemStack> list, DenseOre denseOre, IBlockAccess world, BlockPos pos, Block base, IBlockState blockState, BlockAccessSingleOverride delegate, int fortune, Random rand);

    public abstract int getExpDrops(DenseOre denseOre, World world, BlockPos pos, int fortune, IBlockState baseState, BlockAccessSingleOverride delegate);

    protected int getBaseExp(BlockPos pos, int fortune, IBlockState baseState, BlockAccessSingleOverride delegate) {
        return baseState.getBlock().getExpDrop(
                delegate,
                pos, fortune);
    }

    public boolean canSilkHarvest(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public abstract void generate(Chunk chunk, Random random, DenseOre denseOre, boolean retroGen);

    public boolean hasTile() {
        return false;
    }

    public TileEntity createTile(World world) {
        return null;
    }

    public boolean removedByPlayer(World world, BlockPos pos, BlockDenseOre blockDenseOre, EntityPlayer player, boolean willHarvest) {
        return world.setBlockToAir(pos);
    }

    public boolean onBlockActivated(DenseOre denseOre, World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public float transfromHardness(DenseOre denseOre, OreType type, World world, BlockPos pos, TileEntity tile, float blockHardness) {
        return blockHardness;
    }

    public int transformHarvestLevel(int harvestLevel, BlockDenseOre blockDenseOre, IBlockState baseState) {
        return harvestLevel;
    }

    private static class Offset {
        public int[] dx;
        public int[] dy;

        public static Offset getOffset(int renderType) {
            Offset offset = new Offset();
            switch (renderType) {
                default:
                case 0:
                    offset.dx = new int[]{-1, 2, 3};
                    offset.dy = new int[]{-1, 0, 1};
                    break;
                case 1:
                    offset.dx = new int[]{-1, 1, 0, 0, -1, -1, 1, 1, -2, 2, 0, 0};
                    offset.dy = new int[]{0, 0, -1, 1, -1, 1, -1, 1, 0, 0, -2, 2};
                    break;
                case 2:
                    offset.dx = new int[]{-1, 0, 1};
                    offset.dy = new int[]{-1, 0, 1};
                    break;
                case 3:
                    offset.dx = new int[]{-2, 2, 1, 1};
                    offset.dy = new int[]{1, 1, -2, 2};
                case 4:
                    offset.dx = new int[]{-6, -3, 3, 6};
                    offset.dy = new int[]{0, 0, 0, 0};
                    break;
                case 5:
                    offset.dx = new int[]{-5, -5, 5, 5};
                    offset.dy = new int[]{-5, 5, -5, 5};
                    break;
                case 6:
                    offset.dx = new int[]{0, 1, 2, 3};
                    offset.dy = new int[]{0, -3, 2, -1};
                    break;
                case 7:
                    offset.dx = new int[]{-1, 1, 0, 0};
                    offset.dy = new int[]{0, 0, -1, 1};
                    break;
            }
            return offset;
        }
    }

    public static final LinkedList<BlockPos> deposit_positions = new LinkedList<BlockPos>();

    public boolean enabled;
}
