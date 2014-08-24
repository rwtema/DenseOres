package com.rwtema.denseores;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

// Command to identify a block's name and properties (makes for easier configuration)
@SideOnly(Side.CLIENT)
public class CommandIdentifyBlock extends CommandBase {
    @Override
    public String getCommandName() {
        return "denseores_identifyblock";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "denseores.commandIdentify.help";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if (!(var1 instanceof EntityPlayer))
            return;

        ItemStack item = ((EntityPlayer) var1).getHeldItem();
        if (item == null || !(item.getItem() instanceof ItemBlock) || ((ItemBlock) item.getItem()).field_150939_a == null) {
            ((EntityPlayer) var1).addChatComponentMessage(new ChatComponentTranslation("denseores.commandIdentify.invalid"));
            return;
        }

        var1.addChatMessage(new ChatComponentTranslation("denseores.commandIdentify.start", item.toString()));

        ItemBlock itemBlock = (ItemBlock) item.getItem();
        Block b = itemBlock.field_150939_a;
        int metadata = itemBlock.getMetadata(item.getItemDamage());


        boolean isUnusual = b.getRenderType() != 0;

        var1.addChatMessage(new ChatComponentTranslation("S:baseBlock=%s", GameData.getBlockRegistry().getNameForObject(b)));
        var1.addChatMessage(new ChatComponentTranslation("I:baseBlockMeta=%s", metadata));

        String j = null;
        boolean same = true;
        for (int i = 0; i < 6; i++) {
            IIcon icon = b.getIcon(i, metadata);
            if (icon != null) {
                if (j == null) {
                    isUnusual = isUnusual || icon.getClass() != TextureAtlasSprite.class;
                    j = icon.getIconName();
                } else if (!j.equals(icon.getIconName())) {
                    same = false;
                    break;
                }
            }
        }

        if (same) {
            if (j == null) {
                var1.addChatMessage(new ChatComponentTranslation("denseores.commandIdentify.invalidtexture"));
            } else {
                var1.addChatMessage(new ChatComponentTranslation("S:baseBlockTexture=%s", j));
            }
        } else {
            var1.addChatMessage(new ChatComponentTranslation("denseores.commandIdentify.multipleTextures"));
            for (int i = 0; i < 6; i++) {
                IIcon icon = b.getIcon(i, metadata);
                if (icon != null) {
                    isUnusual = isUnusual || icon.getClass() != TextureAtlasSprite.class;
                    var1.addChatMessage(new ChatComponentTranslation("%s: %s", ForgeDirection.getOrientation(i).toString(), icon.getIconName()));
                }
            }
        }


        if (isUnusual) {
            var1.addChatMessage(new ChatComponentTranslation("denseores.commandIdentify.obligitarydisclaimer"));
        }
    }
}
