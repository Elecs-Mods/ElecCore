package elec332.core.client;

import com.google.common.collect.Maps;
import elec332.core.api.client.ITessellator;
import elec332.core.client.tesselator.ElecTessellator;
import elec332.core.client.tesselator.RenderBlocks;
import elec332.core.main.ElecCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3f;
import java.util.Map;

/**
 * Created by Elec332 on 31-7-2015.
 */
@SideOnly(Side.CLIENT)
public class RenderHelper {

    public static final float renderUnit = 1/16f;
    public static final double BB_EXPAND_NUMBER = 0.0020000000949949026D;
    private static final Tessellator mcTessellator;
    private static final ITessellator tessellator;
    private static final Minecraft mc;
    private static final RenderBlocks renderBlocks;
    private static final Map<VertexBuffer, ITessellator> worldRenderTessellators;
    private static final Map<EnumFacing, ITransformation[]> rotateAroundMap;
    private static IBakedModel nullModel;

    public static ITessellator forWorldRenderer(VertexBuffer renderer){
        ITessellator ret = worldRenderTessellators.get(renderer);
        if (ret == null){
            ret = new ElecTessellator(renderer);
            worldRenderTessellators.put(renderer, ret);
        }
        return ret;
    }

    public static RenderBlocks getBlockRenderer(){
        return renderBlocks;
    }

    public static FontRenderer getMCFontrenderer(){
        return mc.fontRendererObj;
    }

    public static ITessellator getTessellator(){
        return tessellator;
    }

    public static IBakedModel getMissingModel(){
        return Minecraft.getMinecraft().modelManager.getMissingModel();
    }

    public static boolean isBufferDrawing(VertexBuffer buffer){
        return buffer.isDrawing;
    }

    public static void drawExpandedSelectionBoundingBox(@Nonnull AxisAlignedBB aabb){
        drawSelectionBoundingBox(expandAABB(aabb));
    }

    public static void drawSelectionBoundingBox(@Nonnull AxisAlignedBB aabb){
        RenderGlobal.drawSelectionBoundingBox(aabb, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    public static AxisAlignedBB expandAABB(@Nonnull AxisAlignedBB aabb){
        return aabb.expandXyz(BB_EXPAND_NUMBER);
    }

    public static ITransformation getTransformation(int x, int y, int z){
        if ((z = MathHelper.normalizeAngle(z, 360)) == 0){
            return ModelRotation.getModelRotation(x, y);
        }
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, TRSRTransformation.quatFromXYZDegrees(new Vector3f(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360), z)), null, null));
    }

    public static ITransformation[] getTranformationsFor(EnumFacing axis){
        return rotateAroundMap.get(axis);
    }

    public static ModelRotation combine(ModelRotation rotation1, ModelRotation rotation2){
        if (rotation1 == null && rotation2 == null){
            return ModelRotation.X0_Y0;
        }
        if (rotation1 == null){
            return rotation2;
        }
        if (rotation2 == null){
            return rotation1;
        }
        return ModelRotation.getModelRotation(((rotation1.quartersX + rotation2.quartersX)) * 90, ((rotation1.quartersY + rotation2.quartersY)) * 90);
    }

    public static ModelRotation defaultFor(EnumFacing facing){
        switch (facing){
            case EAST:
                return ModelRotation.X0_Y90;
            case SOUTH:
                return ModelRotation.X0_Y180;
            case WEST:
                return ModelRotation.X0_Y270;
            case UP:
                return ModelRotation.X270_Y0;
            case DOWN:
                return ModelRotation.X90_Y0;
            default:
                return ModelRotation.X0_Y0;
        }
    }

    public static Vec3d getPlayerVec(float partialTicks){
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        double dX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        return new Vec3d(dX, dY, dZ);
    }

    public static void translateToWorld(float partialTicks){
        Vec3d vec = getPlayerVec(partialTicks);
        GlStateManager.translate(-vec.xCoord, -vec.yCoord, -vec.zCoord);
    }

    public static Vec3d getPlayerVec(){
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        return new Vec3d(player.posX, player.posY, player.posZ);
    }

    public static ICamera getPlayerCamera(float partialTicks){
        ICamera camera = new Frustum();
        Vec3d vec = getPlayerVec(partialTicks);
        camera.setPosition(vec.xCoord, vec.yCoord, vec.zCoord);
        return camera;
    }

    public static void drawLine(Vec3d from, Vec3d to, Vec3d player, float thickness){
        drawQuad(from, from.addVector(thickness, thickness, thickness), to, to.addVector(thickness, thickness, thickness));
    }

    public static void drawQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4){
        tessellator.addVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord, 0, 0);
        tessellator.addVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord, 1, 0);
        tessellator.addVertexWithUV(v3.xCoord, v3.yCoord, v3.zCoord, 1, 1);
        tessellator.addVertexWithUV(v4.xCoord, v4.yCoord, v4.zCoord, 0, 1);
    }

    public static Vec3d multiply(Vec3d original, double m){
        return new Vec3d(original.xCoord * m, original.yCoord * m, original.zCoord * m);
    }

    public static void bindBlockTextures(){
        bindTexture(getBlocksResourceLocation());
    }

    public static void bindTexture(ResourceLocation rl){
        mc.renderEngine.bindTexture(rl);
    }

    public static TextureAtlasSprite checkIcon(TextureAtlasSprite icon) {
        if (icon == null)
            return getMissingTextureIcon();
        return icon;
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return getMissingTextureIcon();
        return checkIcon(flowing ? getIconFrom(fluid.getFlowing()) : getIconFrom(fluid.getStill()));
    }

    public static TextureAtlasSprite getMissingTextureIcon(){
        return mc.getTextureMapBlocks().getMissingSprite();//((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(getBlocksResourceLocation())).getAtlasSprite("missingno");
    }

    public static TextureAtlasSprite getIconFrom(ResourceLocation rl){
        return mc.getTextureMapBlocks().getAtlasSprite(rl.toString());
    }

    public static ResourceLocation getBlocksResourceLocation(){
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public void spawnParticle(Particle particle){
        mc.effectRenderer.addEffect(particle);
    }

    static {
        mcTessellator = Tessellator.getInstance();
        tessellator = new ElecTessellator(mcTessellator);
        mc = Minecraft.getMinecraft();
        renderBlocks = new RenderBlocks();
        worldRenderTessellators = Maps.newHashMap();
        worldRenderTessellators.put(mcTessellator.getBuffer(), tessellator);
        rotateAroundMap = Maps.newEnumMap(EnumFacing.class);
        for (EnumFacing facing : EnumFacing.VALUES){
            switch (facing){
                case NORTH:
                    rotateAroundMap.put(facing, new ITransformation[]{RenderHelper.getTransformation(0, 0, 0), RenderHelper.getTransformation(0, 0, 90), RenderHelper.getTransformation(0, 0, 180), RenderHelper.getTransformation(0, 0, 270)});
                    break;
                case EAST:
                    rotateAroundMap.put(facing, new ITransformation[]{RenderHelper.getTransformation(0, 90, 0), RenderHelper.getTransformation(180, -90, 90), RenderHelper.getTransformation(-90, -90, 90), RenderHelper.getTransformation(0, -90, 90)});
                    break;
                case SOUTH:
                    rotateAroundMap.put(facing, new ITransformation[]{RenderHelper.getTransformation(180, 0, 0), RenderHelper.getTransformation(180, 0, 90), RenderHelper.getTransformation(180, 0, 180), RenderHelper.getTransformation(180, 0, -90)});
                    break;
                case WEST:
                    rotateAroundMap.put(facing, new ITransformation[]{RenderHelper.getTransformation(0, 90, 180), RenderHelper.getTransformation(180, 90, 180), RenderHelper.getTransformation(90, 90, 180), RenderHelper.getTransformation(-90, 90, 180)});
                    break;
                case UP:
                    rotateAroundMap.put(facing, new ITransformation[]{RenderHelper.getTransformation(-90, 0, 0), RenderHelper.getTransformation(90, 0, 90), RenderHelper.getTransformation(90, 0, 180), RenderHelper.getTransformation(90, 0, -90)});
                    break;
                case DOWN:
                    rotateAroundMap.put(facing, new ITransformation[]{RenderHelper.getTransformation(90, 0, 0), RenderHelper.getTransformation(-90, 0, 90), RenderHelper.getTransformation(-90, 0, 180), RenderHelper.getTransformation(-90, 0, -90)});
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

}
