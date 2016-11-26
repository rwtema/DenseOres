package com.rwtema.denseores;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
	@Override
	public void postInit() {
		super.postInit();

	}

	@Override
	public RuntimeException wrap(RuntimeException throwable) {
		return new CustomModLoadingErrorDisplayException(throwable.getMessage(), throwable) {
			@Override
			public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
				
			}

			@Override
			public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
				int offset = 75;

				errorScreen.drawCenteredString(fontRenderer, throwable.getMessage(), errorScreen.width / 2, offset, 0xFFFFFF);
//				offset+=10;
//				errorScreen.drawCenteredString(fontRenderer, String.format("The mod listed below does not want to run in Minecraft version %s", Loader.instance().getMinecraftModContainer().getVersion()), errorScreen.width / 2, offset, 0xFFFFFF);
//				offset+=5;
//				offset+=10;
//				errorScreen.drawCenteredString(fontRenderer, String.format("%s (%s) wants Minecraft %s", wrongMC.mod.getName(), wrongMC.mod.getModId(), wrongMC.mod.acceptableMinecraftVersionRange()), errorScreen.width / 2, offset, 0xEEEEEE);
//				offset+=20;
//				errorScreen.drawCenteredString(fontRenderer, "The file 'fml-client-latest.log' contains more information", errorScreen.width / 2, offset, 0xFFFFFF);
			}
		};
	}
}
