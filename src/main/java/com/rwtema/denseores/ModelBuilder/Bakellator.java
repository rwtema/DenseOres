package com.rwtema.denseores.ModelBuilder;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

public class Bakellator {
    int side;

    int[] rawIntBuffer = new int[28];
    int vertexNo;
    int rawBufferIndex = 0;
    boolean isDrawing;
    int tintIndex;
    int color;

    LinkedList<BakedQuad> generalQuads ;
    ArrayList<LinkedList<BakedQuad>> faceQuads;
    int normal;

    public SimpleBakedModel bake(TextureAtlasSprite texture) {
        return bake(texture, true,true, ItemCameraTransforms.DEFAULT);
    }

    public SimpleBakedModel bake(TextureAtlasSprite texture, boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms camera) {
        SimpleBakedModel model = new SimpleBakedModel(generalQuads, faceQuads, ambientOcclusion, gui3d, texture, camera);
        reset();
        return model;
    }

    public Bakellator() {
        reset();
    }

    public void reset() {
        faceQuads = new ArrayList<LinkedList<BakedQuad>>(6);
        generalQuads = new LinkedList<BakedQuad>();
        for (int i = 0; i < 6; i++)
            faceQuads.set(i, new LinkedList<BakedQuad>());

        vertexNo = 0;
        side = 6;
        isDrawing = false;
        color = -1;
        tintIndex = -1;
        normal = 0;
    }

    public void setGeneral() {
        side = 6;
    }

    public void setFaceSide(int side) {
        this.side = side;
    }

    public void setFaceSide(EnumFacing facing) {
        side = facing.ordinal();
    }

    public void startDrawingQuads() {
        if (isDrawing)
            throw new RuntimeException("Already drawing");
        reset();
        isDrawing = true;
    }

    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    public void setColorOpaque_F(float r, float g, float b) {
        this.setColorOpaque((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F));
    }

    public void setColorRGBA_F(float r, float g, float b, float a) {
        this.setColorRGBA((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F));
    }

    public void setColorOpaque(int r, int g, int b) {
        this.setColorRGBA(r, g, b, 255);
    }

    public void setColorOpaque_I(int col) {
        int j = col >> 16 & 255;
        int k = col >> 8 & 255;
        int l = col & 255;
        this.setColorOpaque(j, k, l);
    }

    public void setColorRGBA_I(int col, int alpha) {
        int k = col >> 16 & 255;
        int l = col >> 8 & 255;
        int i1 = col & 255;
        this.setColorRGBA(k, l, i1, alpha);
    }


    public void setColorRGBA(int r, int g, int b, int a) {

        if (r > 255) {
            r = 255;
        }

        if (g > 255) {
            g = 255;
        }

        if (b > 255) {
            b = 255;
        }

        if (a > 255) {
            a = 255;
        }

        if (r < 0) {
            r = 0;
        }

        if (g < 0) {
            g = 0;
        }

        if (b < 0) {
            b = 0;
        }

        if (a < 0) {
            a = 0;
        }

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.color = a << 24 | b << 16 | g << 8 | r;
        } else {
            this.color = r << 24 | g << 16 | b << 8 | a;
        }
    }

    public void setColorOpaque_B(byte p_154352_1_, byte p_154352_2_, byte p_154352_3_) {
        this.setColorOpaque(p_154352_1_ & 255, p_154352_2_ & 255, p_154352_3_ & 255);
    }

    public void setNormal(float p_78375_1_, float p_78375_2_, float p_78375_3_) {
        byte b0 = (byte) ((int) (p_78375_1_ * 127.0F));
        byte b1 = (byte) ((int) (p_78375_2_ * 127.0F));
        byte b2 = (byte) ((int) (p_78375_3_ * 127.0F));
        this.normal = b0 & 255 | (b1 & 255) << 8 | (b2 & 255) << 16;
    }


    public void addVertexWithUV(double x, double y, double z, double u, double v) {
        this.rawIntBuffer[this.rawBufferIndex] = Float.floatToRawIntBits((float) x);
        this.rawIntBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float) y);
        this.rawIntBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float) z);
        this.rawIntBuffer[this.rawBufferIndex + 3] = this.color;
        this.rawIntBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float) u);
        this.rawIntBuffer[this.rawBufferIndex + 5] = Float.floatToRawIntBits((float) v);

        this.rawBufferIndex += 7;

        vertexNo++;

        if (vertexNo == 4) {
            BakedQuad quad = new BakedQuad(rawIntBuffer, tintIndex, FaceBakery.getFacingFromVertexData(rawIntBuffer));
            if (side == 6)
                generalQuads.add(quad);
            else
                faceQuads.get(side).add(quad);
        }

    }
}



