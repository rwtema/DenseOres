package com.rwtema.denseores;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Random;

public class WorldGenOres implements IWorldGenerator {

	private static final String DENSEORES = "DenseOres";
	// generates blocks in the world
	private static Random rand = new Random();

	public static boolean overrideChunkBlock(Chunk chunk, BlockPos pos, IBlockState state, boolean retroGen) {
		if (!retroGen) {
			return chunk.getWorld().setBlockState(pos, state);
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		IBlockState oldState = chunk.getBlockState(pos);
		if (oldState == state || y < 0) {
			return false;
		} else {
			ExtendedBlockStorage[] storageArray = chunk.getBlockStorageArray();
			ExtendedBlockStorage extendedblockstorage = storageArray[y >> 4];

			if (extendedblockstorage == null) {
				extendedblockstorage = storageArray[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, !chunk.getWorld().provider.hasNoSky());
			}

			extendedblockstorage.set(x & 15, y & 15, z & 15, state);

			if (oldState.getBlock().hasTileEntity(oldState)) {
				Map<BlockPos, TileEntity> map = chunk.getTileEntityMap();
				TileEntity te = map.remove(pos);
				if (te != null) {
					te.invalidate(); //urk hopefully this doesn't explode anything
				}
			}


			return extendedblockstorage.get(x & 15, y & 15, z & 15) == state;
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
			genChunk(chunk, random, denseOre, false);
		}
	}

	public void genChunk(Chunk chunk, Random random, DenseOre denseOre, boolean retroGen) {
		IBlockState replaceState = denseOre.block.getDefaultState();
		for (int i = 0; i < 1000; i++) {
			int x = (chunk.xPosition << 4) | random.nextInt(16);
			int y = 1 + random.nextInt(80);
			int z = (chunk.zPosition << 4) | random.nextInt(16);

			BlockPos pos = new BlockPos(x, y, z);

			IBlockState state = chunk.getBlockState(pos);

			if (state == denseOre.getBaseState())
				overrideChunkBlock(chunk, pos, replaceState, retroGen);
		}

	}

	@SubscribeEvent
	public void retroGen(ChunkDataEvent.Load event) {
		NBTTagCompound chunkData = event.getData();
		NBTTagCompound rgen = chunkData.getCompoundTag(DENSEORES);
		boolean regen = false;

		long worldSeed = event.getWorld().getSeed();

		rand.setSeed(worldSeed);
		long xSeed = rand.nextLong() >> 2 + 1L;
		long zSeed = rand.nextLong() >> 2 + 1L;
		long chunkSeed = (xSeed * event.getChunk().xPosition + zSeed * event.getChunk().zPosition) ^ worldSeed;
		rand.setSeed(chunkSeed);

		for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
			if (!rgen.hasKey(denseOre.name.toString()) || denseOre.retroGenId != rgen.getInteger(denseOre.name.toString())) {
				genChunk(event.getChunk(), rand, denseOre, true);
				regen = true;
			}
		}

		if (regen)
			event.getChunk().setChunkModified();

	}

	@SubscribeEvent
	public void retroGenSave(ChunkDataEvent.Save event) {
		NBTTagCompound chunkData = event.getData();
		NBTTagCompound rgen = chunkData.getCompoundTag(DENSEORES);

		for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
			rgen.setInteger(denseOre.name.toString(), denseOre.retroGenId);
		}

		chunkData.setTag(DENSEORES, rgen);
	}


}
