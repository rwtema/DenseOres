package com.rwtema.denseores.commands;


import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//Command to allow texture pack makers to retrieve the generated textures
@SideOnly(Side.CLIENT)
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

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
//        BlockDenseOre dense_ore_blocks = DenseOresMod.block;
//        for (int meta = 0; meta < 16; meta++) {
//            if (dense_ore_blocks.isValid && DenseModelGenerator.icons[meta] instanceof TextureOre) {
//                TextureOre textureOre = (TextureOre) DenseModelGenerator.icons[meta];
//
//                BufferedImage image = textureOre.output_image;
//                if (image == null)
//                    continue;
//
//
//                String s1 = "minecraft", s2 = textureOre.name;
//
//                int ind = s2.indexOf(58);
//                if (ind >= 0) {
//                    if (ind > 1) s1 = s2.substring(0, ind);
//                    s2 = s2.substring(ind + 1, s2.length());
//                }
//                s1 = s1.toLowerCase();
//
//                File dir = new File(Minecraft.getMinecraft().mcDataDir, "denseoretextures");
//                File moddir = new File(new File(new File(new File(new File(dir, "assets"), "denseores"), "textures"), "blocks"), s1);
//                File f = new File(moddir, s2 + ".png");
//
//                try {
//                    if (!f.getParentFile().exists() && !f.getParentFile().mkdirs())
//                        return;
//
//                    if (!f.exists() && !f.createNewFile())
//                        continue;
//
//                    ImageIO.write(image, "png", f);
//                } catch (IOException e) {
//                    LogHelper.info("Unable to output " + textureOre.getIconName());
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}
