package elec332.core.client;

import elec332.core.client.render.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 10-12-2015.
 */
@SideOnly(Side.CLIENT)
public class RenderBlocks {

    /** If set to >=0, all block faces will be rendered using this texture index */
    public TextureAtlasSprite overrideBlockTexture;
    /** Set to true if the texture should be flipped horizontally during render*Face */
    public boolean flipTexture;
    public boolean field_152631_f;

    public boolean renderFromInside = false;
    /** The minimum X value for rendering (default 0.0). */
    public double renderMinX;
    /** The maximum X value for rendering (default 1.0). */
    public double renderMaxX;
    /** The minimum Y value for rendering (default 0.0). */
    public double renderMinY;
    /** The maximum Y value for rendering (default 1.0). */
    public double renderMaxY;
    /** The minimum Z value for rendering (default 0.0). */
    public double renderMinZ;
    /** The maximum Z value for rendering (default 1.0). */
    public double renderMaxZ;
    public boolean lockBlockBounds;
    public boolean partialRenderBounds;
    public final Minecraft minecraftRB;
    public int uvRotateEast;
    public int uvRotateWest;
    public int uvRotateSouth;
    public int uvRotateNorth;
    public int uvRotateTop;
    public int uvRotateBottom;
    /** Whether ambient occlusion is enabled or not */
    public boolean enableAO;
    /** Brightness top left */
    public int brightnessTopLeft;
    /** Brightness bottom left */
    public int brightnessBottomLeft;
    /** Brightness bottom right */
    public int brightnessBottomRight;
    /** Brightness top right */
    public int brightnessTopRight;
    /** Red color value for the top left corner */
    public float colorRedTopLeft;
    /** Red color value for the bottom left corner */
    public float colorRedBottomLeft;
    /** Red color value for the bottom right corner */
    public float colorRedBottomRight;
    /** Red color value for the top right corner */
    public float colorRedTopRight;
    /** Green color value for the top left corner */
    public float colorGreenTopLeft;
    /** Green color value for the bottom left corner */
    public float colorGreenBottomLeft;
    /** Green color value for the bottom right corner */
    public float colorGreenBottomRight;
    /** Green color value for the top right corner */
    public float colorGreenTopRight;
    /** Blue color value for the top left corner */
    public float colorBlueTopLeft;
    /** Blue color value for the bottom left corner */
    public float colorBlueBottomLeft;
    /** Blue color value for the bottom right corner */
    public float colorBlueBottomRight;
    /** Blue color value for the top right corner */
    public float colorBlueTopRight;

    private ITessellator tessellator;

    public RenderBlocks() {
        this.minecraftRB = Minecraft.getMinecraft();
        this.tessellator = RenderHelper.getTessellator();
    }

    public RenderBlocks setTessellator(ITessellator tessellator){
        this.tessellator = tessellator;
        return this;
    }

    public RenderBlocks resetTessellator(){
        return setTessellator(RenderHelper.getTessellator());
    }

    /**
     * Sets overrideBlockTexture
     */
    public void setOverrideBlockTexture(TextureAtlasSprite p_147757_1_) {
        this.overrideBlockTexture = p_147757_1_;
    }

    /**
     * Clear override block texture
     */
    public void clearOverrideBlockTexture() {
        this.overrideBlockTexture = null;
    }

    public boolean hasOverrideBlockTexture() {
        return this.overrideBlockTexture != null;
    }

    public void setRenderFromInside(boolean p_147786_1_) {
        this.renderFromInside = p_147786_1_;
    }

    public void setRenderBounds(double p_147782_1_, double p_147782_3_, double p_147782_5_, double p_147782_7_, double p_147782_9_, double p_147782_11_) {
        if (!this.lockBlockBounds) {
            this.renderMinX = p_147782_1_;
            this.renderMaxX = p_147782_7_;
            this.renderMinY = p_147782_3_;
            this.renderMaxY = p_147782_9_;
            this.renderMinZ = p_147782_5_;
            this.renderMaxZ = p_147782_11_;
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0D || this.renderMaxX < 1.0D || this.renderMinY > 0.0D || this.renderMaxY < 1.0D || this.renderMinZ > 0.0D || this.renderMaxZ < 1.0D);
        }
    }

    /**
     * Like setRenderBounds, but automatically pulling the bounds from the given Block.
     */
    public void setRenderBoundsFromBlock(Block p_147775_1_) {
        if (!this.lockBlockBounds) {
            this.renderMinX = p_147775_1_.getBlockBoundsMinX();
            this.renderMaxX = p_147775_1_.getBlockBoundsMaxX();
            this.renderMinY = p_147775_1_.getBlockBoundsMinY();
            this.renderMaxY = p_147775_1_.getBlockBoundsMaxY();
            this.renderMinZ = p_147775_1_.getBlockBoundsMinZ();
            this.renderMaxZ = p_147775_1_.getBlockBoundsMaxZ();
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0D || this.renderMaxX < 1.0D || this.renderMinY > 0.0D || this.renderMaxY < 1.0D || this.renderMinZ > 0.0D || this.renderMaxZ < 1.0D);
        }
    }

    /**
     * Like setRenderBounds, but locks the values so that RenderBlocks won't change them.  If you use this, you must
     * call unlockBlockBounds after you finish rendering!
     */
    public void overrideBlockBounds(double p_147770_1_, double p_147770_3_, double p_147770_5_, double p_147770_7_, double p_147770_9_, double p_147770_11_) {
        this.renderMinX = p_147770_1_;
        this.renderMaxX = p_147770_7_;
        this.renderMinY = p_147770_3_;
        this.renderMaxY = p_147770_9_;
        this.renderMinZ = p_147770_5_;
        this.renderMaxZ = p_147770_11_;
        this.lockBlockBounds = true;
        this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0D || this.renderMaxX < 1.0D || this.renderMinY > 0.0D || this.renderMaxY < 1.0D || this.renderMinZ > 0.0D || this.renderMaxZ < 1.0D);
    }

    /**
     * Unlocks the visual bounding box so that RenderBlocks can change it again.
     */
    public void unlockBlockBounds() {
        this.lockBlockBounds = false;
    }

    /**
     * Renders the given texture to the bottom face of the block. Args: x, y, z, texture
     */
    public void renderFaceYNeg(double p_147768_2_, double p_147768_4_, double p_147768_6_, TextureAtlasSprite p_147768_8_) {

        if (this.hasOverrideBlockTexture()) {
            p_147768_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147768_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147768_8_.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double)p_147768_8_.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double)p_147768_8_.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double)p_147768_8_.getMinU();
            d4 = (double)p_147768_8_.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d5 = (double)p_147768_8_.getMinV();
            d6 = (double)p_147768_8_.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateBottom == 2) {
            d3 = (double)p_147768_8_.getInterpolatedU(this.renderMinZ * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateBottom == 1) {
            d3 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateBottom == 3) {
            d3 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147768_2_ + this.renderMinX;
        double d12 = p_147768_2_ + this.renderMaxX;
        double d13 = p_147768_4_ + this.renderMinY;
        double d14 = p_147768_6_ + this.renderMinZ;
        double d15 = p_147768_6_ + this.renderMaxZ;

        if (this.renderFromInside) {
            d11 = p_147768_2_ + this.renderMaxX;
            d12 = p_147768_2_ + this.renderMinX;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        } else {
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
    }

    /**
     * Renders the given texture to the top face of the block. Args: x, y, z, texture
     */
    public void renderFaceYPos(double p_147806_2_, double p_147806_4_, double p_147806_6_, TextureAtlasSprite p_147806_8_) {

        if (this.hasOverrideBlockTexture()) {
            p_147806_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147806_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147806_8_.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double)p_147806_8_.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double)p_147806_8_.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double)p_147806_8_.getMinU();
            d4 = (double)p_147806_8_.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d5 = (double)p_147806_8_.getMinV();
            d6 = (double)p_147806_8_.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateTop == 1) {
            d3 = (double)p_147806_8_.getInterpolatedU(this.renderMinZ * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateTop == 2) {
            d3 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateTop == 3) {
            d3 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147806_2_ + this.renderMinX;
        double d12 = p_147806_2_ + this.renderMaxX;
        double d13 = p_147806_4_ + this.renderMaxY;
        double d14 = p_147806_6_ + this.renderMinZ;
        double d15 = p_147806_6_ + this.renderMaxZ;

        if (this.renderFromInside) {
            d11 = p_147806_2_ + this.renderMaxX;
            d12 = p_147806_2_ + this.renderMinX;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        } else {
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
    }

    /**
     * Renders the given texture to the north (z-negative) face of the block.  Args: x, y, z, texture
     */
    public void renderFaceZNeg(double p_147761_2_, double p_147761_4_, double p_147761_6_, TextureAtlasSprite p_147761_8_) {

        if (this.hasOverrideBlockTexture()) {
            p_147761_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147761_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147761_8_.getInterpolatedU(this.renderMaxX * 16.0D);

        if (this.field_152631_f) {
            d4 = (double)p_147761_8_.getInterpolatedU((1.0D - this.renderMinX) * 16.0D);
            d3 = (double)p_147761_8_.getInterpolatedU((1.0D - this.renderMaxX) * 16.0D);
        }

        double d5 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double)p_147761_8_.getMinU();
            d4 = (double)p_147761_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double)p_147761_8_.getMinV();
            d6 = (double)p_147761_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateEast == 2) {
            d3 = (double)p_147761_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateEast == 1) {
            d3 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateEast == 3) {
            d3 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147761_2_ + this.renderMinX;
        double d12 = p_147761_2_ + this.renderMaxX;
        double d13 = p_147761_4_ + this.renderMinY;
        double d14 = p_147761_4_ + this.renderMaxY;
        double d15 = p_147761_6_ + this.renderMinZ;

        if (this.renderFromInside) {
            d11 = p_147761_2_ + this.renderMaxX;
            d12 = p_147761_2_ + this.renderMinX;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        } else {
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
    }

    /**
     * Renders the given texture to the south (z-positive) face of the block.  Args: x, y, z, texture
     */
    public void renderFaceZPos(double p_147734_2_, double p_147734_4_, double p_147734_6_, TextureAtlasSprite p_147734_8_) {

        if (this.hasOverrideBlockTexture()) {
            p_147734_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147734_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147734_8_.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double)p_147734_8_.getMinU();
            d4 = (double)p_147734_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double)p_147734_8_.getMinV();
            d6 = (double)p_147734_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateWest == 1) {
            d3 = (double)p_147734_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateWest == 2) {
            d3 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateWest == 3) {
            d3 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147734_2_ + this.renderMinX;
        double d12 = p_147734_2_ + this.renderMaxX;
        double d13 = p_147734_4_ + this.renderMinY;
        double d14 = p_147734_4_ + this.renderMaxY;
        double d15 = p_147734_6_ + this.renderMaxZ;

        if (this.renderFromInside) {
            d11 = p_147734_2_ + this.renderMaxX;
            d12 = p_147734_2_ + this.renderMinX;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        } else {
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
    }

    /**
     * Renders the given texture to the west (x-negative) face of the block.  Args: x, y, z, texture
     */
    public void renderFaceXNeg(double p_147798_2_, double p_147798_4_, double p_147798_6_, TextureAtlasSprite p_147798_8_) {

        if (this.hasOverrideBlockTexture()) {
            p_147798_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147798_8_.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double)p_147798_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
        double d5 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d3 = (double)p_147798_8_.getMinU();
            d4 = (double)p_147798_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double)p_147798_8_.getMinV();
            d6 = (double)p_147798_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateNorth == 1) {
            d3 = (double)p_147798_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateNorth == 2) {
            d3 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(this.renderMinZ * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateNorth == 3) {
            d3 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147798_2_ + this.renderMinX;
        double d12 = p_147798_4_ + this.renderMinY;
        double d13 = p_147798_4_ + this.renderMaxY;
        double d14 = p_147798_6_ + this.renderMinZ;
        double d15 = p_147798_6_ + this.renderMaxZ;

        if (this.renderFromInside) {
            d14 = p_147798_6_ + this.renderMaxZ;
            d15 = p_147798_6_ + this.renderMinZ;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        } else {
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
    }

    /**
     * Renders the given texture to the east (x-positive) face of the block.  Args: x, y, z, texture
     */
    public void renderFaceXPos(double p_147764_2_, double p_147764_4_, double p_147764_6_, TextureAtlasSprite p_147764_8_) {

        if (this.hasOverrideBlockTexture()) {
            p_147764_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147764_8_.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double)p_147764_8_.getInterpolatedU(this.renderMaxZ * 16.0D);

        if (this.field_152631_f) {
            d4 = (double)p_147764_8_.getInterpolatedU((1.0D - this.renderMinZ) * 16.0D);
            d3 = (double)p_147764_8_.getInterpolatedU((1.0D - this.renderMaxZ) * 16.0D);
        }

        double d5 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d3 = (double)p_147764_8_.getMinU();
            d4 = (double)p_147764_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double)p_147764_8_.getMinV();
            d6 = (double)p_147764_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateSouth == 2) {
            d3 = (double)p_147764_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateSouth == 1) {
            d3 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(this.renderMaxZ * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(this.renderMinZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateSouth == 3) {
            d3 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147764_2_ + this.renderMaxX;
        double d12 = p_147764_4_ + this.renderMinY;
        double d13 = p_147764_4_ + this.renderMaxY;
        double d14 = p_147764_6_ + this.renderMinZ;
        double d15 = p_147764_6_ + this.renderMaxZ;

        if (this.renderFromInside) {
            d14 = p_147764_6_ + this.renderMaxZ;
            d15 = p_147764_6_ + this.renderMinZ;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        } else {
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
    }

}
