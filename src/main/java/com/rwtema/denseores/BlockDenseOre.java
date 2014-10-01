package com.rwtema.denseores;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*  I'm using the 16 metadata values to store each ore block.
 *  (We don't really need to worry about block ids in 1.7
 *   but that's no reason to be wasteful)
 */

public class BlockDenseOre extends BlockOre {

    // no constructor needed here but you still need to specify a material for
    // other blocks.

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (isValid(meta))
            return getBlock(meta).canHarvestBlock(player, getEntry(meta).metadata);

        return super.canHarvestBlock(player, meta);
    }

    @Override
    public int getHarvestLevel(int meta)
    {
        if (isValid(meta))
            return getBlock(meta).getHarvestLevel(getEntry(meta).metadata);

        return super.getHarvestLevel(meta);
    }

    @Override
    public String getHarvestTool(int meta)
    {
        if (isValid(meta))
            return getBlock(meta).getHarvestTool(getEntry(meta).metadata);

        return super.getHarvestTool(meta);
    }

    // Ore Entry stuff
    public DenseOre[] entry = new DenseOre[16];
    public Block[] baseBlocks = new Block[16];
    public boolean[] valid = new boolean[16];
    public boolean init = false;

    public static Block getBlock(String name) {
        return GameData.getBlockRegistry().getObject(name);
    }

    public static Block getBlock(DenseOre ore) {
        return ore != null ? getBlock(ore.baseBlock) : null;
    }

    public void init() {
        init = true;

        for (int i = 0; i < 16; i++) {
            baseBlocks[i] = getBlock(entry[i]);
            valid[i] = baseBlocks[i] != null && baseBlocks[i] != Blocks.air;
        }
    }

    public Block getBlock(int id) {
        if (!init)
            init();

        return baseBlocks[id];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        int id = world.getBlockMetadata(x, y, z);
        if (!isValid(id))
            return;

        try {
            world.setBlock(x, y, z, getBlock(id), getMetadata(id), 0);
            for (int i = 0; i < 1 + rand.nextInt(3); i++)
                getBlock(id).randomDisplayTick(world, x, y, z, rand);
        } catch (Exception e) {
            world.setBlock(x, y, z, this, id, 0);
            throw new RuntimeException(e);
        }

        world.setBlock(x, y, z, this, id, 0);
    }

    public boolean isValid(int id) {
        if (!init)
            init();

        if (id < 0 || id >= 16)
            return false;

        return valid[id];
    }

    public void setEntry(int id, DenseOre ore) {
        this.entry[id] = ore;
    }

    public DenseOre getEntry(int id) {
        return entry[id];
    }

    // add creative blocks
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 16; i++)
            if (isValid(i))
                list.add(new ItemStack(item, 1, i));

    }

    public IIcon[] icons = new IIcon[16];

    // get icon from side/metadata
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (isValid(meta))
            return icons[meta];
        else {
            return getNullOverride(Minecraft.getMinecraft().theWorld).getIcon(0, 0);
        }
    }

    // get icon from side/metadata
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (isValid(meta))
            return icons[meta];
        else {
            return getNullOverride(Minecraft.getMinecraft().theWorld, x, z).getIcon(0, 0);
        }
    }

    public Block getNullOverride(World world, int x, int z) {
        if (world == null)
            return Blocks.stone;

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (biome == BiomeGenBase.hell)
            return Blocks.netherrack;

        if (biome == BiomeGenBase.sky)
            return Blocks.end_stone;

        return getNullOverride(world);
    }

    public Block getNullOverride(World world) {
        if (world.provider == null)
            return Blocks.stone;

        if (world.provider.dimensionId == -1)
            return Blocks.netherrack;

        if (world.provider.dimensionId == 1)
            return Blocks.end_stone;

        return Blocks.stone;
    }

    // register icons
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        if (register instanceof TextureMap) { // should always be true (...but
            // you never know)
            TextureMap map = (TextureMap) register;
            for (int i = 0; i < 16; i++) {
                if (isValid(i)) {

                    // Registering custom icon classes

                    // name of custom icon ( must equal getIconName() )
                    String name = TextureOre.getDerivedName(entry[i].texture);
                    // see if there's already an icon of that name
                    TextureAtlasSprite texture = map.getTextureExtry(name);
                    if (texture == null) {
                        // if not create one and put it in the register
                        texture = new TextureOre(entry[i]);
                        map.setTextureEntry(name, texture);
                    }

                    icons[i] = map.getTextureExtry(name);
                }
            }
        }
    }

    // metadata dropped
    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    // get drops
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        if (isValid(metadata)) {
            Block base = getBlock(metadata);

            if (base == null)
                return ret;

            int m = getMetadata(metadata);

            // get base drops 3 times
            for (int j = 0; j < 3; j++) {
                int count = base.quantityDropped(m, fortune, world.rand);
                for (int i = 0; i < count; i++) {
                    Item item = base.getItemDropped(m, world.rand, fortune);
                    if (item != null) {
                        ret.add(new ItemStack(item, 1, base.damageDropped(m)));
                    }
                }
            }
        } else {
            return getNullOverride(world, x, z).getDrops(world, x, y, z, 0, fortune);
        }
        return ret;
    }

    private int getMetadata(int id) {
        return entry[id].metadata;
    }

    // get hardness
    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (isValid(metadata)) {
            Block base = getBlock(metadata);
            float t = this.blockHardness;

            // quickly change metadata to match what is expected
            world.setBlockMetadataWithNotify(x, y, z, getMetadata(metadata), 0);
            try {
                t = base.getBlockHardness(world, x, y, z);
            } catch (Exception e) {
                // oh oh, it seems it didn't like having a different block id.
                LogHelper.error("The ore block " + entry[metadata].id + "(" + entry[metadata].baseBlock + ")"
                        + " has thrown an error while getting the hardness value. It is likely not compatible with Dense ores", e);

                world.setBlockMetadataWithNotify(x, y, z, getMetadata(metadata), 0); // just in case

                throw new RuntimeException(e);
            }

            // set it back
            world.setBlockMetadataWithNotify(x, y, z, metadata, 0);

            return t;
        }
        return this.blockHardness;
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        int result = 0;
        if(isValid(metadata)) {
            Block base = getBlock(metadata);

            if(base == null)
                return 0;

            int m = getMetadata(metadata);

            //get base exp dropped 3 times
            for(int i = 0; i < 3; i++) {
                result += base.getExpDrop(world, m, fortune);
            }
        } else {
            return getNullOverride((World)world).getExpDrop(world, 0, fortune);
        }
        return result;
    }
}
