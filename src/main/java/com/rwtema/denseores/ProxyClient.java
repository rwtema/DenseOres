package com.rwtema.denseores;


import com.rwtema.denseores.blocks.BlockDenseOre;
import com.rwtema.denseores.blockstates.DenseOreBlockState;
import com.rwtema.denseores.commands.CommandClientOutputTextures;
import com.rwtema.denseores.commands.CommandClientIdentifyBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
    @Override
    public void postInit() {
        super.postInit();
        ClientCommandHandler.instance.registerCommand(new CommandClientOutputTextures());
        ClientCommandHandler.instance.registerCommand(new CommandClientIdentifyBlock());
    }

    @Override
    public void loadModel(BlockDenseOre blockDenseOre, IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        ((DenseOreBlockState) state).model.handleState(blockDenseOre.getBaseBlockState(), state, worldIn, pos);
    }
}
