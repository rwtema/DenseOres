package com.rwtema.denseores;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

public class TextureOre extends TextureAtlasSprite {

	private int n;

	private ResourceLocation textureLocation;

	private String name;

	private String base;

	public static String getDerivedName(String par1Str) {
		return DenseOresMod.MODID + ":" + par1Str + "_dense";
	}

	public TextureOre(String par1Str, String base) {
		super(getDerivedName(par1Str));
		this.name = par1Str;
		this.base = base;
	}

	public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
		try {
			manager.getResource(location);
		} catch (IOException e) {
			return true;
		}
		System.out.println("Dense Ores: Detected override for " + name);
		return false;
	}

	public ResourceLocation getBlockResource(String s2) {
		String s1 = "minecraft";

		int ind = name.indexOf(58);

		if (ind >= 0) {
			s2 = name.substring(ind + 1, name.length());

			if (ind > 1) {
				s1 = name.substring(0, ind);
			}
		}

		s1 = s1.toLowerCase();
		s2 = "textures/blocks/" + s2 + ".png";
		return new ResourceLocation(s1, s2);
	}

	public boolean load(IResourceManager manager, ResourceLocation location) {

		int mp = Minecraft.getMinecraft().gameSettings.field_151442_I;

		try {
			IResource iresource = manager.getResource(getBlockResource(name));
			IResource iresourceBase = manager.getResource(getBlockResource(base));

			BufferedImage[] abufferedimage = new BufferedImage[1 + mp];

			abufferedimage[0] = ImageIO.read(iresource.getInputStream());

			BufferedImage base = ImageIO.read(iresourceBase.getInputStream());

			int w = abufferedimage[0].getWidth();

			BufferedImage bufferedimage = new BufferedImage(w, w, abufferedimage[0].getType());

			if (w != base.getWidth()) {
				return true;
			}

			int[] aint = new int[w * w];
			int[] bint = new int[w * w];
			int[] nint = new int[w * w];
			abufferedimage[0].getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getWidth(), aint, 0, bufferedimage.getWidth());

			base.getRGB(0, 0, w, w, bint, 0, base.getWidth());

			boolean[] v = new boolean[w * w];

			int h = w;

			for (int i = 0; i < aint.length; i += 1) {
				v[i] = aint[i] == bint[i];
				nint[i] = aint[i];
			}

			for (int i = 0; i < aint.length; i += 1) {
				if (v[i]) {
					int x = (i % w);
					int y = (i - x) / w;

					if (!v[aint.length - 1 - i] && lum(nint[i]) < lum(aint[aint.length - 1 - i]))
						nint[i] = aint[aint.length - 1 - i];
					if (!v[y + (w - 1 - x) * w] && lum(nint[i]) < lum(aint[y + (w - 1 - x) * w]))
						nint[i] = aint[y + (w - 1 - x) * w];
					if (!v[(w - 1 - y) + x * w] && lum(nint[i]) < aint[(w - 1 - y) + x * w])
						nint[i] = aint[(w - 1 - y) + x * w];
				}
			}

			for (int i = 0; i < aint.length; i += 1) {
				aint[i] = nint[aint.length - 1 - i];
			}

			bufferedimage.setRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint, 0, bufferedimage.getWidth());

			abufferedimage[0] = bufferedimage;

			AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection) iresource.getMetadata("animation");
			this.func_147964_a(abufferedimage, null, (float) Minecraft.getMinecraft().gameSettings.field_151443_J > 1.0F);
		} catch (IOException e) {
			return true;
		}

		System.out.println("Dense Ores: Succesfully generated dense ore texture for '" + name + "' with background '" + base + "'. Place " + name + "_dense.png in the assets folder to override.");
		return false;
	}

	public float lum(int col) {
		float r = (float) ((-col) >> 16 & 255) / 255.0F;
		float g = (float) ((-col) >> 8 & 255) / 255.0F;
		float b = (float) ((-col) & 255) / 255.0F;
		r = 1 - r;
		g = 1 - g;
		b = 1 - b;
		return r * 0.2126F + g * 0.7152F + b * 0.0722F;
	}

	public boolean setInt(boolean[] v, int newi, int oldi, int[] nint, int[] aint) {
		if (!v[newi]) {
			nint[oldi] = aint[newi];
			return true;
		} else
			return false;
	}
}
