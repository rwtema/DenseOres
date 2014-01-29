package com.rwtema.denseores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDenseOre extends BlockOre {
	public DenseOre[] entry = new DenseOre[16];
	public Block[] baseBlocks = new Block[16];
	public boolean[] valid = new boolean[16];

	public IIcon[] icons = new IIcon[16];

	public boolean init = false;

	public static Block getBlock(String name) {
		return GameData.blockRegistry.get(name);
	}

	public static Block getBlock(DenseOre ore) {
		return ore != null ? getBlock(ore.baseBlock) : null;
	}

	public void init() {
		init = true;

		for (int i = 0; i < 16; i++) {
			baseBlocks[i] = getBlock(entry[i]);
			valid[i] = baseBlocks[i] != null;
		}
	}

	public Block getBlock(int id) {
		if (!init)
			init();

		return baseBlocks[id];
	}

	public boolean isValid(int id) {
		if (!init)
			init();

		return valid[id];
	}

	public void setEntry(int id, DenseOre ore) {
		this.entry[id] = ore;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		for (int i = 0; i < 16; i++)
			if (isValid(i))
				p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));

	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon func_149691_a(int side, int meta) {

		return icons[meta];

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void func_149651_a(IIconRegister register) {
		if (register instanceof TextureMap) {
			TextureMap map = (TextureMap) register;
			for (int i = 0; i < 16; i++) {
				if (isValid(i)) {
					String name = TextureOre.getDerivedName(entry[i].texture);
					TextureAtlasSprite texture = map.getTextureExtry(name);
					if (texture == null) {
						texture = new TextureOre(entry[i].texture, entry[i].underlyingBlock);
					}
					map.setTextureEntry(name, texture);
					icons[i] = map.getTextureExtry(name);
				}
			}
		}
	}

	@Override
	public int func_149692_a(int p_149692_1_) {
		return p_149692_1_;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (isValid(metadata)) {
			Block base = getBlock(metadata);
			int m = entry[metadata].metadata;

			for (int j = 0; j < 3; j++) {
				int count = quantityDropped(m, fortune, world.rand);
				for (int i = 0; i < count; i++) {
					Item item = base.func_149650_a(m, world.rand, fortune);
					if (item != null) {
						ret.add(new ItemStack(item, 1, base.func_149692_a(m)));
					}
				}
			}
		}
		return ret;
	}

	@Override
	public float func_149712_f(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (isValid(metadata)) {
			Block base = getBlock(metadata);
			float t = this.field_149782_v;

			world.setBlockMetadataWithNotify(x, y, z, entry[metadata].metadata, 0);
			try {
				t = base.func_149712_f(world, x, y, z);
			} catch (Exception e) {

			}
			world.setBlockMetadataWithNotify(x, y, z, metadata, 0);
			return t;
		}
		return this.field_149782_v;
	}

}
