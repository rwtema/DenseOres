package com.rwtema.denseores;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
    @Override
    public void postInit() {
        ClientCommandHandler.instance.registerCommand(new CommandIdentifyBlock());
        ClientCommandHandler.instance.registerCommand(new CommandClientOutputTextures());
    }
}
