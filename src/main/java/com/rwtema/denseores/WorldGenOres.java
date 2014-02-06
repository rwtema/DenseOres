package com.rwtema.denseores;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenOres implements IWorldGenerator {

	// TODO: add retrogen

	// generates blocks in the world (unchanged from 1.6)
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		for (int i = 0; i < 800; i++) {
			int x = (chunkX << 4) + random.nextInt(16);
			int y = 1 + random.nextInt(80);
			int z = (chunkZ << 4) + random.nextInt(16);

			Block block = world.getBlock(x, y, z);

			for (BlockDenseOre dense_ore_blocks : DenseOresRegistry.blocks.values()) {
				for (int id = 0; id < 16; id++) {
					if (dense_ore_blocks.isValid(id)) {
						if (block == dense_ore_blocks.getBlock(id) && world.getBlockMetadata(x, y, z) == dense_ore_blocks.entry[id].metadata) {
							world.setBlock(x, y, z, dense_ore_blocks, id, 3);
							continue;
						}
					}
				}
			}
		}
	}
}
