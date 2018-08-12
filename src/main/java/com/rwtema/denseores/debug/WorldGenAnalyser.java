package com.rwtema.denseores.debug;

import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresRegistry;
import com.rwtema.denseores.compat.Compat;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.procedure.TObjectLongProcedure;
import mcjty.lib.compat.CompatCommand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.Random;

@Optional.Interface(iface = "mcjty.lib.compat.CompatCommand", modid = "compatlayer")
public class WorldGenAnalyser extends CommandBase implements IWorldGenerator, CompatCommand {
	public final static WorldGenAnalyser INSTANCE = new WorldGenAnalyser();

	final TObjectLongHashMap<IBlockState> states = new TObjectLongHashMap<>();
	long n;

	public static void register(FMLServerStartingEvent event) {
		INSTANCE.reset();
		event.registerServerCommand(INSTANCE);
	}

	public static void registerWorldGen() {
		GameRegistry.registerWorldGenerator(INSTANCE, 1000000);
	}

	public void reset() {
		n = 0;
		states.clear();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		Chunk chunk = world.getChunk(chunkX, chunkZ);

		synchronized (states) {
			n++;
			for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(getBlock(0, 0, 0, chunkX, chunkZ), getBlock(15, 255, 15, chunkX, chunkZ))) {
				IBlockState state = chunk.getBlockState(pos);
				states.adjustOrPutValue(state, 1, 1);

			}
		}
	}

	public BlockPos getBlock(int x, int y, int z, int chunkXPos, int chunkZPos) {
		return new BlockPos((chunkXPos << 4) + x, y, (chunkZPos << 4) + z);
	}

	@Nonnull
	@Override
	public String getName() {
		return "denseores_worldgenreport";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender) {
		return "denseores_worldgenreport";
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
		if (!sender.sendCommandFeedback()) {
			return;
		}

		if (args.length == 1) {
			String arg = args[0];
			if ("reset".equals(arg)) {
				reset();
				return;
			}

			if ("gen".equals(arg)) {
				WorldServer world = (WorldServer) sender.getEntityWorld();
				BlockPos pos = sender.getPosition();
				for (int i = 0; i < 100; i++) {
					int x = world.rand.nextInt(2000) - 1000;
					int z = world.rand.nextInt(2000) - 1000;
					for (int dx = -1; dx <= 1; dx++) {
						for (int dz = -1; dz <= 1; dz++) {
							world.getChunkProvider().loadChunk(x + dx, z + dz);
						}
					}

					world.getChunkProvider().loadChunk(x, z);
				}
				return;
			}

			if ("fly".equals(arg)) {
				if (sender instanceof EntityPlayerMP) {
					EntityPlayerMP playerMP = (EntityPlayerMP) sender;
					playerMP.capabilities.setFlySpeed(0.25F);
					playerMP.sendPlayerAbilities();

				}
			}

			Compat.INSTANCE.addChatMessage(sender, new TextComponentString("Unrecognized Command"));
			return;
		}

		synchronized (states) {
			states.forEachEntry(new TObjectLongProcedure<IBlockState>() {
				@Override
				public boolean execute(IBlockState a, long b) {
					Compat.INSTANCE.addChatMessage(sender, new TextComponentString(a + " " + (((double) b / n))));
					return true;
				}
			});


			double k = 0;
			for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
				Compat.INSTANCE.addChatMessage(sender, new TextComponentString("Ore " + denseOre.name));
				double t = 0;
				IBlockState baseBlockState = denseOre.block.getBaseBlockState();
				if (states.containsKey(baseBlockState)) {
					double v = (double) states.get(baseBlockState) / n;
					k += v;
					t += v;
					Compat.INSTANCE.addChatMessage(sender, new TextComponentString("Ore " + baseBlockState + " " + v));
				}

				for (IBlockState oreState : denseOre.block.getBlockState().getValidStates()) {
					if (states.containsKey(oreState)) {
						double v = (double) states.get(oreState) / n;
						k += v;
						t += v;
						Compat.INSTANCE.addChatMessage(sender, new TextComponentString("Ore " + oreState + " " + v));
					}
				}
				Compat.INSTANCE.addChatMessage(sender, new TextComponentString("SubTotal " + t));
			}
			Compat.INSTANCE.addChatMessage(sender, new TextComponentString("Total " + k));
		}
	}
}
