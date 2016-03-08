package com.rwtema.denseores;


import com.rwtema.denseores.commands.CommandClientOutputTextures;
import com.rwtema.denseores.commands.CommandClientIdentifyBlock;
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
}
