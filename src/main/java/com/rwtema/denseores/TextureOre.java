package com.rwtema.denseores;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

// Custom texture class to handle the ore generation
@SideOnly(Side.CLIENT)
public class TextureOre extends TextureAtlasSprite {

    private int n;

    private ResourceLocation textureLocation;

    public String name;

    public String base;
    public int type;

    public BufferedImage output_image = null;


    public static String getDerivedName(String s2) {
        String s1 = "minecraft";

        int ind = s2.indexOf(58);

        if (ind >= 0) {
            if (ind > 1) {
                s1 = s2.substring(0, ind);
            }

            s2 = s2.substring(ind + 1, s2.length());
        }

        s1 = s1.toLowerCase();

        return DenseOresMod.MODID + ":" + s1 + "/" + s2;
    }

    private int renderType = 0;

    public TextureOre(DenseOre denseOre) {
        this(denseOre.texture, denseOre.underlyingBlocktexture);
        renderType = denseOre.rendertype;
    }

    public TextureOre(String par1Str, String base) {
        super(getDerivedName(par1Str));
        this.name = par1Str;
        this.base = base;
    }

    // should we use a custom loader to get our texture?
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {

        ResourceLocation location1 = new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", new Object[]{"textures/blocks", location.getResourcePath(), ".png"}));
        try {
            // check to see if the resource can be loaded (someone added an
            // override)
            manager.getResource(location1);
            LogHelper.info("Dense Ores: Detected override for " + name);
            return false;
        } catch (IOException e) {
            // file not found: let's generate one
            return true;
        }
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
        s2 = "textures/blocks/" + s2 + ".png";

        return new ResourceLocation(s1, s2);
    }

    // loads the textures
    // note: the documentation

    /**
     * Load the specified resource as this sprite's data. Returning false from
     * this function will prevent this icon from being stitched onto the master
     * texture.
     *
     * @param manager  Main resource manager
     * @param location File resource location
     * @return False to prevent this Icon from being stitched
     */
    // is not correct - return TRUE to prevent this Icon from being stitched
    // (makes no sense but... whatever)

    // this code is based on code from TextureMap.loadTextureAtlas, only with
    // the
    // code for custom mip-mapping textures and animation removed.
    // TODO: add animation support
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
        output_image = new BufferedImage(w, h, 2);

        // create some arrays t hold the pixel data
        // pixel data is in the form 0xaarrggbb
        int[] ore_data = new int[w * w];
        int[] stone_data = new int[w * w];

        stone_image.getRGB(0, 0, w, w, stone_data, 0, w);

        for (int y = 0; y < h; y += w) {
            // read the ARGB color data into our arrays
            ore_image[0].getRGB(0, y, w, w, ore_data, 0, w);

            // generate our new texture
            int[] new_data = createDenseTexture(w, ore_data, stone_data, renderType);

            // write the new image data to the output image buffer
            output_image.setRGB(0, y, w, w, new_data, 0, w);
        }

        // replace the old texture
        ore_image[0] = output_image;

        // load the texture
        this.loadSprite(ore_image, animation, (float) Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1.0F);

        LogHelper.info("Dense Ores: Succesfully generated dense ore texture for '" + name + "' with background '" + base + "'. Place " + name + "_dense.png in the assets folder to override.");
        return false;
    }

    private static int[] createDenseTexture(int w, int[] ore_data, int[] stone_data, int renderType) {
        int[] new_data = new int[w * w];

        // we need to work out which pixels should be considered 'ore pixels' and which should be 'base pixels'
        boolean[] same = new boolean[w * w];
        for (int i = 0; i < ore_data.length; i += 1) {
            if (getAlpha(ore_data[i]) == 0) {   // if the ore texture pixel is transparent, overwrite with the corresponding stone pixel
                same[i] = true;
                ore_data[i] = stone_data[i];
            } else if (ore_data[i] == stone_data[i]) {
                same[i] = true;
            } else {
                int r = Math.abs(getRed(ore_data[i]) - getRed(stone_data[i]));
                int g = Math.abs(getGreen(ore_data[i]) - getGreen(stone_data[i]));
                int b = Math.abs(getBlue(ore_data[i]) - getBlue(stone_data[i]));

                same[i] = (r + g + b) < 20; // check to see if the two pixels are not exactly the same but 'close'
            }

            new_data[i] = ore_data[i];
        }

        int[] dx;
        int[] dy;

        //allows for different convolution filters
        switch (renderType) {
            default:
            case 0:
                dx = new int[]{-1, 2, 3};
                dy = new int[]{-1, 0, 1};
                break;
            case 1:
                dx = new int[]{-1, 1, 0, 0, -1, -1, 1, 1, -2, 2, 0, 0};
                dy = new int[]{0, 0, -1, 1, -1, 1, -1, 1, 0, 0, -2, 2};
                break;
            case 2:
                dx = new int[]{-1, 0, 1};
                dy = new int[]{-1, 0, 1};
                break;
            case 3:
                dx = new int[]{-2, 2, 1, 1};
                dy = new int[]{1, 1, -2, 2};
            case 4:
                dx = new int[]{-6, -3, 3, 6};
                dy = new int[]{0, 0, 0, 0};
                break;
            case 5:
                dx = new int[]{-5, -5, 5, 5};
                dy = new int[]{-5, 5, -5, 5};
                break;
            case 6:
                dx = new int[]{0, 1, 2, 3};
                dy = new int[]{0, -3, 2, -1};
                break;
            case 7:
                dx = new int[]{-1, 1, 0, 0};
                dy = new int[]{0, 0, -1, 1};
                break;
        }


        // where the magic happens
        for (int i = 0; i < ore_data.length; i += 1) {
            int x = (i % w);
            int y = (i - x) / w;

            // if the pixel an ore pixel, we don't need to do anything so continue
            if (!same[i])
                continue;

            // use our convolution filter to see if we can find an ore pixel nearby
            for (int j = 0; j < dx.length; j++) {
                final int new_x = x + dx[j];
                final int new_y = y + dy[j];

                if (new_x >= 0 && new_x < w && new_y >= 0 && new_y < w) // is valid pixel location
                    if (!same[new_x + new_y * w]) { // is it an ore pixel?
                        new_data[i] = ore_data[new_x + new_y * w];
                        break;
                    }
            }
        }
        return new_data;
    }

    public static int getAlpha(int col) {
        return (col & 0xff000000) >> 24;
    }

    public static int getRed(int col) {
        return (col & 0x00ff0000) >> 16;
    }

    public static int getGreen(int col) {
        return (col & 0x0000ff00) >> 8;
    }

    public static int getBlue(int col) {
        return col & 0x000000ff;
    }

    public static int makeCol(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
