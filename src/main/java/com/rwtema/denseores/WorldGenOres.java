package com.rwtema.denseores;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenOres implements IWorldGenerator {

	public static int n = 0;
	public static int xn = 0;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		for (int i = 0; i < 800; i++) {
			int x = random.nextInt(16);
			int y = 1 + random.nextInt(80);
			int z = random.nextInt(16);

			Block block = chunk.func_150810_a(x, y, z);

			for (BlockDenseOre block1 : DenseOresRegistry.blocks.values()) {
				for (int id = 0; id < 16; id++) {
					if (block1.isValid(id)) {
						if (block == block1.getBlock(id) && chunk.getBlockMetadata(x, y, z) == block1.entry[id].metadata) {
							if (world.func_147465_d((chunkX << 4) + x, y, (chunkZ << 4) + z, block1, id, 3)) {
//								xn++;
								// System.out.println("Replacing tile: " +
								// ((chunkX << 4) + x) + "-" + y + "-" +
								// ((chunkZ << 4) + z));

							}

							continue;
						}
					}
				}
			}
		}

//		n++;

		//System.out.println(((double) xn) / n + " " + n);

	}
}
