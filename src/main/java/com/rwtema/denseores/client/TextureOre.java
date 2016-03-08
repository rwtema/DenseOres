package com.rwtema.denseores.client;

import com.google.common.base.Throwables;
import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.DenseOresMod;
import com.rwtema.denseores.blockstates.OreType;
import com.rwtema.denseores.utils.ColorHelper;
import com.rwtema.denseores.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

// Custom texture class to handle the ore generation
@SideOnly(Side.CLIENT)
public class TextureOre extends TextureAtlasSprite {

    private final OreType oreType;
    public String name;
    public String base;
    public int type;
    private int renderType = 0;

    public TextureOre(DenseOre denseOre, OreType oreType) {
        this(oreType, denseOre.texture, denseOre.underlyingBlockTexture);
        renderType = denseOre.rendertype;
    }

    public TextureOre(OreType oreType, String par1Str, String base) {
        super(getDerivedName(par1Str, oreType));
        this.oreType = oreType;
        this.name = par1Str;
        this.base = base;
    }

    public static String getDerivedName(String s2, OreType oreType) {
        String s1 = "minecraft";

        int ind = s2.indexOf(58);

        if (ind >= 0) {
            if (ind > 1) {
                s1 = s2.substring(0, ind);
            }

            s2 = s2.substring(ind + 1, s2.length());
        }

        s1 = s1.toLowerCase();

        return DenseOresMod.MODID + ":" + s1 + "/" + s2 + "_" + oreType.getName();
    }

    // converts texture name to resource location
    public static ResourceLocation getBlockResource(String s2) {
        String s1 = "minecraft";

        int ind = s2.indexOf(58);

        if (ind >= 0) {
            if (ind > 1) {
                s1 = s2.substring(0, ind);
            }

            s2 = s2.substring(ind + 1, s2.length());
        }

        s1 = s1.toLowerCase();
        s2 = "textures/" + s2 + ".png";

        return new ResourceLocation(s1, s2);
    }

    private static int[] createDenseTexture(int w, int[] ore_data, int[] stone_data, int renderType, OreType oreType) {
        int[] new_data = new int[w * w];

        int r = w >> 4;

        // we need to work out which pixels should be considered 'ore pixels' and which should be 'base pixels'
        boolean[] same = new boolean[w * w];
        for (int i = 0; i < ore_data.length; i += 1) {
            if (ColorHelper.getAlpha(ore_data[i]) == 0) {   // if the ore texture pixel is transparent, overwrite with the corresponding stone pixel
                same[i] = true;
                ore_data[i] = stone_data[i];
            } else if (ColorHelper.areColorsClose(ore_data[i], stone_data[i])) {
                same[i] = true;
            } else {
                int x = i % w;
                searchLoop:
                for (int dx = -r; dx <= r; dx++) {
                    if ((x + dx) <= 0 || (x + dx) > w)
                        continue;
                    for (int dy = -(r * w); dy <= (r * w); dy += w) {
                        int k = i + dx + dy;

                        if (k < 0 || k >= stone_data.length)
                            continue;

                        if (ColorHelper.areColorsClose(ore_data[i], stone_data[k])) {
                            same[i] = true;
                            break searchLoop;
                        }
                    }
                }

            }

            new_data[i] = ore_data[i];
        }

        try {
            oreType.assignOreTexData(w, ore_data, stone_data, renderType, new_data, same);
        } catch (Throwable err) {
            err.printStackTrace();
            throw Throwables.propagate(err);
        }
        return new_data;
    }

    // loads the textures
    // note: the documentation

    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }


    public boolean load(IResourceManager manager, ResourceLocation location) {

        // get mipmapping level
        int mp = Minecraft.getMinecraft().gameSettings.mipmapLevels;

        // creates a buffer that will be used for our texture and the
        // various mip-maps
        // (mip-mapping is where you use smaller textures when objects are
        // far-away
        // see: http://en.wikipedia.org/wiki/Mipmap)
        // these will be generated from the base texture by Minecraft
        BufferedImage[] ore_image = new BufferedImage[1 + mp];

        BufferedImage stone_image;
        int w;

        AnimationMetadataSection animation;

        try {
            IResource iresource = manager.getResource(getBlockResource(name));
            IResource iresourceBase = manager.getResource(getBlockResource(base));

            // load the ore texture
            ore_image[0] = ImageIO.read(iresource.getInputStream());

            // load animation
            animation = (AnimationMetadataSection) iresource.getMetadata("animation");

            // load the stone texture
            stone_image = ImageIO.read(iresourceBase.getInputStream());

            w = ore_image[0].getWidth();

            if (stone_image.getWidth() != w) {
                List resourcePacks = manager.getAllResources(getBlockResource(base));
                for (int i = resourcePacks.size() - 1; i >= 0; --i) {
                    IResource resource = (IResource) resourcePacks.get(i);
                    stone_image = ImageIO.read(resource.getInputStream());

                    if (stone_image.getWidth() == w)
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        if (stone_image.getWidth() != w) {
            LogHelper.error("Error generating texture" + name + ". Unable to find base texture with same size.");
            return true;
        }

        int h = ore_image[0].getHeight();

        // create an ARGB output image that will be used as our texture
        BufferedImage output_image = new BufferedImage(w, h, 2);

        // create some arrays t hold the pixel data
        // pixel data is in the form 0xaarrggbb
        int[] ore_data = new int[w * w];
        int[] stone_data = new int[w * w];

        stone_image.getRGB(0, 0, w, w, stone_data, 0, w);

        for (int y = 0; y < h; y += w) {
            // read the ARGB color data into our arrays
            ore_image[0].getRGB(0, y, w, w, ore_data, 0, w);

            // generate our new texture
            int[] new_data = createDenseTexture(w, ore_data, stone_data, renderType, oreType);

            // write the new image data to the output image buffer
            output_image.setRGB(0, y, w, w, new_data, 0, w);
        }

        // replace the old texture
        ore_image[0] = output_image;

        // load the texture
        try {
            this.loadSprite(ore_image, animation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LogHelper.info("Dense Ores: Succesfully generated dense ore texture for '" + name + "' with background '" + base + "'. Place " + name + "_dense.png in the assets folder to override.");
        return false;
    }
}
