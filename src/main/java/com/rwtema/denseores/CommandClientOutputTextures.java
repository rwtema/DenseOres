package com.rwtema.denseores;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//Command to allow texture pack makers to retrieve the generated textures
public class CommandClientOutputTextures extends CommandBase {
    @Override
    public String getCommandName() {
        return "denseores_outputtextures";
    }


    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }


    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "denseores.command.help";
    }

    public String stripColon(String s) {
        int i = s.indexOf(58);
        if (i >= 0)
            s = s.substring(i + 1, s.length());
        return s;
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        for (BlockDenseOre dense_ore_blocks : DenseOresRegistry.blocks.values()) {
            for (int meta = 0; meta < 16; meta++) {
                if (dense_ore_blocks.isValid(meta)) {
                    if (dense_ore_blocks.icons[meta] instanceof TextureOre) {
                        TextureOre textureOre = (TextureOre) dense_ore_blocks.icons[meta];

                        BufferedImage image = textureOre.output_image;
                        if (image == null)
                            continue;

                        String s2 = textureOre.name;
                        String s1 = "minecraft";

                        int ind = s2.indexOf(58);

                        if (ind >= 0) {
                            if (ind > 1) {
                                s1 = s2.substring(0, ind);
                            }

                            s2 = s2.substring(ind + 1, s2.length());
                        }

                        s1 = s1.toLowerCase();

                        File dir = new File(Minecraft.getMinecraft().mcDataDir, "denseoretextures");
                        File moddir = new File(new File(new File(new File(new File(dir, "assets"), "denseores"), "textures"), "blocks"), s1);
                        File f = new File(moddir, s2 + ".png");

                        try {
                            f.getParentFile().mkdirs();
                            if (!f.exists())
                                f.createNewFile();

                            ImageIO.write(image, "png", f);
                        } catch (IOException e) {
                            LogHelper.info("Unable to output " + textureOre.getIconName());
                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            }
        }
    }
}
