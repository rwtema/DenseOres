package com.rwtema.denseores;


import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
    @Override
    public void postInit() {
//        ClientCommandHandler.instance.registerCommand(new CommandIdentifyBlock());
        ClientCommandHandler.instance.registerCommand(new CommandClientOutputTextures());
    }
}
