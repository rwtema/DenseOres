package com.rwtema.denseores;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class WorldGenOres implements IWorldGenerator {

    private static final String DENSEORES = "DenseOres";

    // generates blocks in the world
    private static Random rand = new Random();

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        BlockDenseOre dense_ore_blocks = DenseOresMod.block;
        for (int id = 0; id < dense_ore_blocks.maxMetdata; id++) {
            if (dense_ore_blocks.isValid(id)) {
                genChunk(chunk, random, dense_ore_blocks, id);
            }

        }
    }

    public void genChunk(Chunk chunk, Random random, BlockDenseOre dense_ore_blocks, int id) {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(dense_ore_blocks.maxMetdata);
            int y = 1 + random.nextInt(80);
            int z = random.nextInt(dense_ore_blocks.maxMetdata);

            Block block = chunk.getBlock(x, y, z);

            if (block == dense_ore_blocks.getBlock(id) && chunk.getBlockMetadata(new BlockPos(x, y, z)) == dense_ore_blocks.entry[id].metadata)
                overrideChunkBlock(chunk, x, y, z, dense_ore_blocks, id);
        }
    }

    // it seems that if an ore has a tile entity it crashes during retrogen
    // so here's a custom method that doesn't cause issues with overwriting blocks.
    // I wouldn't recommend copying this method though.
    public boolean overrideChunkBlock(Chunk chunk, int x, int y, int z, BlockDenseOre dense_ore_blocks, int id) {
        int i1 = z << 4 | x;

        Block block1 = chunk.getBlock(x, y, z);
        int k1 = chunk.getBlockMetadata(new BlockPos(x, y, z));

        if (block1 == dense_ore_blocks && k1 == id) {
            return false;
        } else {
            ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[y >> 4];

            if (extendedblockstorage == null)
                return false;   //should never happen as we are replacing an existing block

//            extendedblockstorage.func_150818_a(x, y & 15, z, dense_ore_blocks);
//            extendedblockstorage.setExtBlockMetadata(x, y & 15, z, id);

//            if (block1.hasTileEntity(k1)) {
//                TileEntity te = chunk.getTileEntityUnsafe(x & 0x0F, y, z & 0x0F);
//                if (te != null) {
//                    ChunkPosition chunkposition = new ChunkPosition(x & 0x0F, y, z & 0x0F);
//                    te = (TileEntity) chunk.chunkTileEntityMap.remove(chunkposition);
//                    te.invalidate(); //urk hopefully this doesn't explode anything
//                }
//            }

            return extendedblockstorage.getBlockByExtId(x, y & 15, z) == dense_ore_blocks;
        }
    }

    @SubscribeEvent
    public void retroGen(ChunkDataEvent.Load event) {
        NBTTagCompound chunkData = event.getData();
        NBTTagCompound rgen = chunkData.getCompoundTag(DENSEORES);
        boolean regen = false;

        long worldSeed = event.world.getSeed();

        rand.setSeed(worldSeed);
        long xSeed = rand.nextLong() >> 2 + 1L;
        long zSeed = rand.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * event.getChunk().xPosition + zSeed * event.getChunk().zPosition) ^ worldSeed;
        rand.setSeed(chunkSeed);

        BlockDenseOre dense_ore_blocks = DenseOresMod.block;
        NBTTagCompound b = rgen.getCompoundTag(dense_ore_blocks.getUnlocalizedName());
        for (int id = 0; id < dense_ore_blocks.maxMetdata; id++) {
            if (dense_ore_blocks.isValid(id)) {
                if (dense_ore_blocks.getEntry(id).retroGenId != b.getInteger(id + "")) {
                    genChunk(event.getChunk(), rand, dense_ore_blocks, id);
                    regen = true;
                }
            }
        }

        if (regen)
            event.getChunk().setChunkModified();

    }

    @SubscribeEvent
    public void retroGenSave(ChunkDataEvent.Save event) {
        NBTTagCompound chunkData = event.getData();
        NBTTagCompound rgen = chunkData.getCompoundTag(DENSEORES);

        BlockDenseOre dense_ore_blocks = DenseOresMod.block;
        NBTTagCompound b = rgen.getCompoundTag(dense_ore_blocks.getUnlocalizedName());
        for (int id = 0; id < dense_ore_blocks.maxMetdata; id++) {
            if (dense_ore_blocks.isValid(id)) {
                b.setInteger(id + "", dense_ore_blocks.getEntry(id).retroGenId);
            }
        }
        rgen.setTag(dense_ore_blocks.getUnlocalizedName(), b);

        chunkData.setTag(DENSEORES, rgen);
    }
}
