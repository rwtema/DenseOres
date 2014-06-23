package com.rwtema.denseores;

import net.minecraftforge.client.ClientCommandHandler;

public class ProxyClient extends Proxy {
    @Override
    public void postInit() {
        ClientCommandHandler.instance.registerCommand(new CommandClientOutputTextures());
    }
}
