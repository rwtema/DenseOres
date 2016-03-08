package com.rwtema.denseores.debug;

import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresRegistry;
import com.rwtema.denseores.blocks.TileDepositLevel;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.procedure.TObjectLongProcedure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenAnalyser extends CommandBase implements IWorldGenerator {
    public final static WorldGenAnalyser INSTANCE = new WorldGenAnalyser();

    final TObjectLongHashMap<IBlockState> states = new TObjectLongHashMap<IBlockState>();
    long n;

    public void reset() {
        n = 0;
        states.clear();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        synchronized (states) {
            n++;
            for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(getBlock(0, 0, 0, chunkX, chunkZ), getBlock(15, 255, 15, chunkX, chunkZ))) {
                IBlockState state = chunk.getBlockState(pos);
                states.adjustOrPutValue(state, 1, 1);

                if (world.getTileEntity(pos) instanceof TileDepositLevel) {
                    int num = ((TileDepositLevel) world.getTileEntity(pos)).num;
                    states.adjustOrPutValue(state, num - 1, num - 1);
                }

            }
        }
    }

    public BlockPos getBlock(int x, int y, int z, int chunkXPos, int chunkZPos) {
        return new BlockPos((chunkXPos << 4) + x, y, (chunkZPos << 4) + z);
    }

    @Override
    public String getCommandName() {
        return "denseores_worldgenreport";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "denseores_worldgenreport";
    }

    @Override
    public void processCommand(final ICommandSender sender, String[] args) throws CommandException {
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
                            world.theChunkProviderServer.loadChunk(x + dx, z + dz);
                        }
                    }

                    world.theChunkProviderServer.loadChunk(x, z);
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

            sender.addChatMessage(new ChatComponentText("Unrecognized Command"));
            return;
        }

        synchronized (states) {
            states.forEachEntry(new TObjectLongProcedure<IBlockState>() {
                @Override
                public boolean execute(IBlockState a, long b) {
                    sender.addChatMessage(new ChatComponentText(a + " " + (((double) b / n))));
                    return true;
                }
            });


            double k = 0;
            for (DenseOre denseOre : DenseOresRegistry.ores.values()) {
                sender.addChatMessage(new ChatComponentText("Ore " + denseOre.name));
                double t = 0;
                IBlockState baseBlockState = denseOre.block.getBaseBlockState();
                if (states.containsKey(baseBlockState)) {
                    double v = (double) states.get(baseBlockState) / n;
                    k += v;
                    t += v;
                    sender.addChatMessage(new ChatComponentText("Ore " + baseBlockState + " " + v));
                }

                for (IBlockState oreState : denseOre.block.getBlockState().getValidStates()) {
                    if (states.containsKey(oreState)) {
                        double v = (double) states.get(oreState) / n;
                        k += v;
                        t += v;
                        sender.addChatMessage(new ChatComponentText("Ore " + oreState + " " + v));
                    }
                }
                sender.addChatMessage(new ChatComponentText("SubTotal " + t));
            }
            sender.addChatMessage(new ChatComponentText("Total " + k));

        }
    }
}
