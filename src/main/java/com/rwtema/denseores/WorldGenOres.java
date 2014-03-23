package com.rwtema.denseores;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.world.ChunkDataEvent;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldGenOres implements IWorldGenerator {

	private static final String DENSEORES = "DenseOres";

	// generates blocks in the world
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		for (BlockDenseOre dense_ore_blocks : DenseOresRegistry.blocks.values()) {
			for (int id = 0; id < 16; id++) {
				if (dense_ore_blocks.isValid(id)) {
					genChunk(chunk, random, dense_ore_blocks, id);
				}
			}
		}
	}

	public void genChunk(Chunk chunk, Random random, BlockDenseOre dense_ore_blocks, int id) {
		for (int i = 0; i < 1000; i++) {
			int x = random.nextInt(16);
			int y = 1 + random.nextInt(80);
			int z = random.nextInt(16);

			Block block = chunk.getBlock(x, y, z);

			if (block == dense_ore_blocks.getBlock(id) && chunk.getBlockMetadata(x, y, z) == dense_ore_blocks.entry[id].metadata)
				chunk.func_150807_a(x, y, z, dense_ore_blocks, id);

		}
	}

	private static Random rand = new Random();

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

		for (BlockDenseOre dense_ore_blocks : DenseOresRegistry.blocks.values()) {
			NBTTagCompound b = rgen.getCompoundTag(dense_ore_blocks.getUnlocalizedName());
			for (int id = 0; id < 16; id++) {
				if (dense_ore_blocks.isValid(id)) {
					if (dense_ore_blocks.getEntry(id).retroGenId != b.getInteger(id + "")) {
						genChunk(event.getChunk(), rand, dense_ore_blocks, id);
						regen = true;
					}
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

		for (BlockDenseOre dense_ore_blocks : DenseOresRegistry.blocks.values()) {
			NBTTagCompound b = rgen.getCompoundTag(dense_ore_blocks.getUnlocalizedName());
			for (int id = 0; id < 16; id++) {
				if (dense_ore_blocks.isValid(id)) {
					b.setInteger(id + "", dense_ore_blocks.getEntry(id).retroGenId);
				}
			}
			rgen.setTag(dense_ore_blocks.getUnlocalizedName(), b);
		}
		chunkData.setTag(DENSEORES, rgen);
	}
}
