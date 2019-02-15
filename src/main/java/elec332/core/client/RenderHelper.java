package elec332.core.client;

import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.client.ITessellator;
import elec332.core.client.util.ElecTessellator;
import net.minecraft.client.MainWindow;
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
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3f;
import java.util.Map;

/**
 * Created by Elec332 on 31-7-2015.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("all")
public class RenderHelper {

    public static final float renderUnit = 1 / 16f;
    public static final double BB_EXPAND_NUMBER = 0.0020000000949949026D;
    private static final Tessellator mcTessellator;
    private static final ITessellator tessellator;
    private static final Minecraft mc;
    private static final Map<BufferBuilder, ITessellator> worldRenderTessellators;
    private static final Map<EnumFacing, ITransformation[]> rotateAroundMap;
    private static IBakedModel nullModel;

    @Nonnull
    @SuppressWarnings("all")
    public static ITessellator forWorldRenderer(BufferBuilder renderer) {
        ITessellator ret = worldRenderTessellators.get(renderer);
        if (ret == null) {
            ret = new ElecTessellator(renderer);
            worldRenderTessellators.put(renderer, ret);
        }
        return ret;
    }

    @Nonnull
    public static FontRenderer getFontRenderer(ItemStack stack) {
        FontRenderer ret = stack.getItem().getFontRenderer(stack);
        if (ret == null) {
            ret = getMCFontrenderer();
        }
        return ret;
    }

    @Nonnull
    public static FontRenderer getMCFontrenderer() {
        return mc.fontRenderer;
    }

    public static MainWindow getMainWindow() {
        return mc.mainWindow;
    }

    @Nonnull
    public static ITessellator getTessellator() {
        return tessellator;
    }

    @Nonnull
    public static IBakedModel getMissingModel() {
        return Minecraft.getInstance().modelManager.getMissingModel();
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends TileEntity> void renderTileEntityAt(TileEntityRenderer<T> tesr, T tile, double x, double y, double z, float partialTicks, int destroyStage) {
        tesr.render(tile, x, y, x, partialTicks, destroyStage);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        TileEntityRendererDispatcher.instance.render(tile, x, y, z, partialTicks, destroyStage, false);
    }

    public static void drawExpandedSelectionBoundingBox(@Nonnull AxisAlignedBB aabb) {
        drawSelectionBoundingBox(expandAABB(aabb));
    }

    public static void drawSelectionBoundingBox(@Nonnull AxisAlignedBB aabb) {
        RenderGlobal.drawSelectionBoundingBox(aabb, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    @Nonnull
    public static AxisAlignedBB expandAABB(@Nonnull AxisAlignedBB aabb) {
        return aabb.grow(BB_EXPAND_NUMBER);
    }

    public static void drawSelectionBox(EntityPlayer player, World world, BlockPos pos, VoxelShape shapeOverride, float partialTicks) {
        if (world.getWorldBorder().contains(pos)) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float) mc.mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 0.999F);
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
            RenderGlobal.drawShape(shapeOverride, (double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    @Nonnull
    public static ITransformation getTransformation(int x, int y, int z) {
        if ((z = MathHelper.normalizeAngle(z, 360)) == 0) {
            return ModelRotation.getModelRotation(x, y);
        }
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, TRSRTransformation.quatFromXYZDegrees(new Vector3f(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360), z)), null, null));
    }

    public static ITransformation[] getTranformationsFor(EnumFacing axis) {
        return rotateAroundMap.get(axis);
    }

    @Nonnull
    public static ModelRotation combine(ModelRotation rotation1, ModelRotation rotation2) {
        if (rotation1 == null && rotation2 == null) {
            return ModelRotation.X0_Y0;
        }
        if (rotation1 == null) {
            return rotation2;
        }
        if (rotation2 == null) {
            return rotation1;
        }
        return ModelRotation.getModelRotation(((rotation1.quartersX + rotation2.quartersX)) * 90, ((rotation1.quartersY + rotation2.quartersY)) * 90);
    }

    @Nonnull
    public static ModelRotation getDefaultRotationFromFacing(EnumFacing facing) {
        switch (facing) {
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
    public static Vec3d getPlayerVec(float partialTicks) {
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        double dX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        return new Vec3d(dX, dY, dZ);
    }

    public static void translateToWorld(float partialTicks) {
        Vec3d vec = getPlayerVec(partialTicks);
        GlStateManager.translated(-vec.x, -vec.y, -vec.z);
    }

    @Nonnull
    public static Vec3d getPlayerVec() {
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        return new Vec3d(player.posX, player.posY, player.posZ);
    }

    @Nonnull
    public static ICamera getPlayerCamera(float partialTicks) {
        ICamera camera = new Frustum();
        Vec3d vec = getPlayerVec(partialTicks);
        camera.setPosition(vec.x, vec.y, vec.z);
        return camera;
    }

    public static void drawLine(Vec3d from, Vec3d to, Vec3d player, float thickness) {
        drawQuad(from, from.add(thickness, thickness, thickness), to, to.add(thickness, thickness, thickness));
    }

    public static void drawQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4) {
        tessellator.addVertexWithUV(v1.x, v1.y, v1.z, 0, 0);
        tessellator.addVertexWithUV(v2.x, v2.y, v2.z, 1, 0);
        tessellator.addVertexWithUV(v3.x, v3.y, v3.z, 1, 1);
        tessellator.addVertexWithUV(v4.x, v4.y, v4.z, 0, 1);
    }

    @Nonnull
    public static Vec3d multiply(Vec3d original, double m) {
        return new Vec3d(original.x * m, original.y * m, original.z * m);
    }

    public static void bindBlockTextures() {
        bindTexture(getBlocksResourceLocation());
    }

    public static void bindTexture(ResourceLocation rl) {
        mc.textureManager.bindTexture(rl);
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
    public static TextureAtlasSprite getMissingTextureIcon() {
        return MissingTextureSprite.getSprite();//mc.getTextureMap().getMissingSprite();//((TextureMap) Minecraft.getInstance().getTextureManager().getTexture(getBlocksResourceLocation())).getAtlasSprite("missingno");
    }

    public static TextureAtlasSprite getIconFrom(ResourceLocation rl) {
        return mc.getTextureMap().getAtlasSprite(rl.toString());
    }

    @Nonnull
    public static ResourceLocation getBlocksResourceLocation() {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public void spawnParticle(Particle particle) {
        mc.particles.addEffect(particle);
    }

    public static void disableStandardItemLighting() {
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }

    public static void enableStandardItemLighting() {
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    }

    public static void enableGUIStandardItemLighting() {
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
    }

    static {
        mcTessellator = Tessellator.getInstance();
        tessellator = new ElecTessellator(mcTessellator);
        mc = Minecraft.getInstance();
        worldRenderTessellators = Maps.newHashMap();
        worldRenderTessellators.put(mcTessellator.getBuffer(), tessellator);
        rotateAroundMap = Maps.newEnumMap(EnumFacing.class);
        for (EnumFacing facing : EnumFacing.values()) {
            switch (facing) {
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
