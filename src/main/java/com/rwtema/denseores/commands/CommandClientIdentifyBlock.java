package com.rwtema.denseores.commands;


import com.rwtema.denseores.blockaccess.SingleBlockWorldAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


// Command to identify a block's name and properties (makes for easier configuration)
@SideOnly(Side.CLIENT)
public class CommandClientIdentifyBlock extends CommandBase {
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
        if (item == null || !(item.getItem() instanceof ItemBlock) || ((ItemBlock) item.getItem()).getBlock() == null) {
            ((EntityPlayer) var1).addChatComponentMessage(new ChatComponentTranslation("denseores.commandIdentify.invalid"));
            return;
        }

        var1.addChatMessage(new ChatComponentTranslation("denseores.commandIdentify.start", item.toString()));
//
        ItemBlock itemBlock = (ItemBlock) item.getItem();
        Block b = itemBlock.getBlock();
        int metadata = itemBlock.getMetadata(item.getItemDamage());

        IBlockState state = b.getStateFromMeta(metadata);


        var1.addChatMessage(new ChatComponentTranslation("S:baseBlock=%s", GameData.getBlockRegistry().getNameForObject(b)));
        var1.addChatMessage(new ChatComponentTranslation("I:baseBlockMeta=%s", b.getMetaFromState(state)));

        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        Map<String, TextureAtlasSprite> textures = ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, map, 6);

        SingleBlockWorldAccess world = new SingleBlockWorldAccess(state);

        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelFromBlockState(state, world, SingleBlockWorldAccess.CENTER_POS);

        HashSet<TextureAtlasSprite> sprites = new HashSet<TextureAtlasSprite>();

        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        quads.addAll(model.getGeneralQuads());
        for (EnumFacing facing : EnumFacing.values()) {
            quads.addAll(model.getFaceQuads(facing));
        }

        for (TextureAtlasSprite sprite : textures.values()) {
            if (matches(quads, sprite))
                sprites.add(sprite);
        }

        for (TextureAtlasSprite sprite : sprites) {
            String iconName = sprite.getIconName();
            if (iconName.startsWith("minecraft:")) iconName = iconName.substring("minecraft:".length());
            var1.addChatMessage(new ChatComponentTranslation("tex=%s", iconName));
        }
    }

    public boolean matches(List<BakedQuad> quad, TextureAtlasSprite sprite) {
        for (BakedQuad bakedQuad : quad) {
            if (matches(bakedQuad, sprite))
                return true;
        }
        return false;
    }

    public boolean matches(BakedQuad quad, TextureAtlasSprite sprite) {
        int[] vertexData = quad.getVertexData();
        for (int i = 0; i < 28; i += 7) {
            float u = Float.intBitsToFloat(vertexData[i + 4]);
            float v = Float.intBitsToFloat(vertexData[i + 5]);

            if (u >= sprite.getMinU() && u <= sprite.getMaxU() && v >= sprite.getMinV() && v <= sprite.getMaxV())
                return true;
        }
        return false;
    }
}
