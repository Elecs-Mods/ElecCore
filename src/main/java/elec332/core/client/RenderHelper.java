package elec332.core.client;

import com.google.common.collect.Maps;
import elec332.core.api.client.ITessellator;
import elec332.core.client.tesselator.ElecTessellator;
import elec332.core.client.tesselator.RenderBlocks;
import elec332.core.main.ElecCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
    private static final Map<BufferBuilder, ITessellator> worldRenderTessellators;
    private static final Map<EnumFacing, ITransformation[]> rotateAroundMap;
    private static IBakedModel nullModel;

    @Nonnull
    @SuppressWarnings("all")
    public static ITessellator forWorldRenderer(BufferBuilder renderer){
        ITessellator ret = worldRenderTessellators.get(renderer);
        if (ret == null){
            ret = new ElecTessellator(renderer);
            worldRenderTessellators.put(renderer, ret);
        }
        return ret;
    }

    @Nonnull
    public static RenderBlocks getBlockRenderer(){
        return renderBlocks;
    }

    @Nonnull
    public static FontRenderer getFontRenderer(ItemStack stack){
        FontRenderer ret = stack.getItem().getFontRenderer(stack);
        if (ret == null){
            ret = getMCFontrenderer();
        }
        return ret;
    }

    @Nonnull
    public static FontRenderer getMCFontrenderer(){
        return mc.fontRenderer;
    }

    @Nonnull
    public static ITessellator getTessellator(){
        return tessellator;
    }

    @Nonnull
    public static IBakedModel getMissingModel(){
        return Minecraft.getMinecraft().modelManager.getMissingModel();
    }

    @SideOnly(Side.CLIENT)
    public static <T extends TileEntity> void renderTileEntityAt(TileEntitySpecialRenderer<T> tesr, T tile, double x, double y, double z, float partialTicks, int destroyStage){
        renderTileEntityAt(tesr, tile, x, y, z, partialTicks, destroyStage, 1.0f);
    }

    @SideOnly(Side.CLIENT)
    public static <T extends TileEntity> void renderTileEntityAt(TileEntitySpecialRenderer<T> tesr, T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        tesr.render(tile, x, y, x, partialTicks, destroyStage, alpha);
    }

    @SideOnly(Side.CLIENT)
    public static void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage){
        renderTileEntityAt(tile, x, y, z, partialTicks, destroyStage, 1.0f);
    }

    @SideOnly(Side.CLIENT)
    public static void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        TileEntityRendererDispatcher.instance.render(tile, x, y, z, partialTicks, destroyStage, alpha);
    }

    public static void drawExpandedSelectionBoundingBox(@Nonnull AxisAlignedBB aabb){
        drawSelectionBoundingBox(expandAABB(aabb));
    }

    public static void drawSelectionBoundingBox(@Nonnull AxisAlignedBB aabb){
        RenderGlobal.drawSelectionBoundingBox(aabb, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    @Nonnull
    public static AxisAlignedBB expandAABB(@Nonnull AxisAlignedBB aabb){
        return aabb.grow(BB_EXPAND_NUMBER);
    }

    @Nonnull
    public static ITransformation getTransformation(int x, int y, int z){
        if ((z = MathHelper.normalizeAngle(z, 360)) == 0){
            return ModelRotation.getModelRotation(x, y);
        }
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, TRSRTransformation.quatFromXYZDegrees(new Vector3f(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360), z)), null, null));
    }

    public static ITransformation[] getTranformationsFor(EnumFacing axis){
        return rotateAroundMap.get(axis);
    }

    @Nonnull
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

    @Nonnull
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

    @Nonnull
    public static Vec3d getPlayerVec(float partialTicks){
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        double dX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        return new Vec3d(dX, dY, dZ);
    }

    public static void translateToWorld(float partialTicks){
        Vec3d vec = getPlayerVec(partialTicks);
        GlStateManager.translate(-vec.x, -vec.y, -vec.z);
    }

    @Nonnull
    public static Vec3d getPlayerVec(){
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        return new Vec3d(player.posX, player.posY, player.posZ);
    }

    @Nonnull
    public static ICamera getPlayerCamera(float partialTicks){
        ICamera camera = new Frustum();
        Vec3d vec = getPlayerVec(partialTicks);
        camera.setPosition(vec.x, vec.y, vec.z);
        return camera;
    }

    public static void drawLine(Vec3d from, Vec3d to, Vec3d player, float thickness){
        drawQuad(from, from.addVector(thickness, thickness, thickness), to, to.addVector(thickness, thickness, thickness));
    }

    public static void drawQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4){
        tessellator.addVertexWithUV(v1.x, v1.y, v1.z, 0, 0);
        tessellator.addVertexWithUV(v2.x, v2.y, v2.z, 1, 0);
        tessellator.addVertexWithUV(v3.x, v3.y, v3.z, 1, 1);
        tessellator.addVertexWithUV(v4.x, v4.y, v4.z, 0, 1);
    }

    @Nonnull
    public static Vec3d multiply(Vec3d original, double m){
        return new Vec3d(original.x * m, original.y * m, original.z * m);
    }

    public static void bindBlockTextures(){
        bindTexture(getBlocksResourceLocation());
    }

    public static void bindTexture(ResourceLocation rl){
        mc.renderEngine.bindTexture(rl);
    }

    @Nonnull
    public static TextureAtlasSprite checkIcon(TextureAtlasSprite icon) {
        if (icon == null)
            return getMissingTextureIcon();
        return icon;
    }

    @Nonnull
    public static TextureAtlasSprite getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return getMissingTextureIcon();
        return checkIcon(flowing ? getIconFrom(fluid.getFlowing()) : getIconFrom(fluid.getStill()));
    }

    @Nonnull
    public static TextureAtlasSprite getMissingTextureIcon(){
        return mc.getTextureMapBlocks().getMissingSprite();//((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(getBlocksResourceLocation())).getAtlasSprite("missingno");
    }

    public static TextureAtlasSprite getIconFrom(ResourceLocation rl){
        return mc.getTextureMapBlocks().getAtlasSprite(rl.toString());
    }

    @Nonnull
    public static ResourceLocation getBlocksResourceLocation(){
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public void spawnParticle(Particle particle){
        mc.effectRenderer.addEffect(particle);
    }

    public static void disableStandardItemLighting(){
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }

    public static void enableStandardItemLighting(){
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    }

    public static void enableGUIStandardItemLighting(){
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
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
