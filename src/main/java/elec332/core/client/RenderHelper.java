package elec332.core.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import elec332.core.ElecCore;
import elec332.core.api.client.ITessellator;
import elec332.core.client.util.ElecTessellator;
import elec332.core.loader.client.RenderingRegistry;
import elec332.core.util.RegistryHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeTransformationMatrix;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
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
    private static final IRenderTypeBuffer mcRenderTypeBuffer;
    private static final Minecraft mc;
    private static final Map<BufferBuilder, ITessellator> worldRenderTessellators;
    private static final Map<Direction, IForgeTransformationMatrix[]> rotateAroundMap;
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
        return mc.getMainWindow();
    }

    public static RenderType createRenderType(String name, RenderType parent, ResourceLocation texture) {
        RenderType.State state = null;
        for (Field f : parent.getClass().getDeclaredFields()) {
            if (f.getGenericType() == RenderType.State.class) {
                try {
                    f.setAccessible(true);
                    state = (RenderType.State) f.get(parent);
                } catch (Exception e) {
                    continue;
                }
                break;
            }
        }
        if (state == null) {
            throw new UnsupportedOperationException();
        }
        Object o = state.field_230171_p_;
        RenderType.State newState = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(texture, state.texture.blur, state.texture.mipmap))
                .transparency(state.transparency)
                .diffuseLighting(state.diffuseLighting)
                .shadeModel(state.shadowModel)
                .alpha(state.alpha)
                .depthTest(state.depthTest)
                .depthTest(state.depthTest)
                .lightmap(state.lightmap)
                .overlay(state.overlay)
                .fog(state.fog)
                .layer(state.layer)
                .target(state.target)
                .texturing(state.texturing)
                .writeMask(state.writeMask)
                .line(state.line)
                .build(o != o.getClass().getEnumConstants()[0]);
        return RenderType.makeType(name, parent.getVertexFormat(), parent.getDrawMode(), parent.getBufferSize(), parent.isUseDelegate(), parent.needsSorting, newState);
    }

    public static AtlasTexture getBlockTextures() {
        return getTextureMap(getBlocksResourceLocation());
    }

    public static AtlasTexture getTextureMap(ResourceLocation map) {
        return mc.getModelManager().getAtlasTexture(map);
    }

    public static <T extends TileEntity> void registerTESR(Class<T> type, TileEntityRenderer<T> renderer) {
        TileEntityRendererDispatcher.instance.setSpecialRendererInternal(RegistryHelper.getTileEntityType(type), renderer);
    }

    @Nonnull
    public static ITessellator getTessellator() {
        return tessellator;
    }

    public IRenderTypeBuffer getMCRenderTypeBuffer() {
        return mcRenderTypeBuffer;
    }

    public static void drawBuffer(BufferBuilder buffer) {
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);
    }

    public static ItemColors getItemColors() {
        return mc.getItemColors();
    }

    public static BlockColors getBlockColors() {
        return mc.getBlockColors();
    }

    @Nonnull
    public static IBakedModel getMissingModel() {
        return RenderingRegistry.instance().missingModelGetter().get();
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends TileEntity> void renderTileEntityAt(TileEntityRenderer<T> tesr, T tile, double x, double y, double z, float partialTicks) {
        renderTileEntityAt(tesr, tile, x, y, z, mcRenderTypeBuffer, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends TileEntity> void renderTileEntityAt(TileEntityRenderer<T> tesr, T tile, double x, double y, double z, IRenderTypeBuffer buffer, float partialTicks) {
        MatrixStack stack = new MatrixStack();
        stack.translate(x, y, z);
        renderTileEntityAt(tesr, tile, stack, buffer, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends TileEntity> void renderTileEntityAt(TileEntityRenderer<T> tesr, T tile, MatrixStack matrixStack, IRenderTypeBuffer buffer, float partialTicks) {
        int i;
        World world = tile.getWorld();
        if (world != null) {
            i = WorldRenderer.getCombinedLight(world, tile.getPos());
        } else {
            i = 15728880;
        }
        tesr.render(tile, partialTicks, matrixStack, buffer, i, OverlayTexture.NO_OVERLAY);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        renderTileEntityAt(tile, x, y, z, partialTicks, mcRenderTypeBuffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, IRenderTypeBuffer renderTypeBuffer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(x, y, z);
        renderTileEntityAt(tile, matrixStack, partialTicks, renderTypeBuffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderTileEntityAt(TileEntity tile, MatrixStack matrixStack, float partialTicks, IRenderTypeBuffer renderTypeBuffer) {
        TileEntityRendererDispatcher.instance.renderTileEntity(tile, partialTicks, matrixStack, renderTypeBuffer);
    }

    public static void drawExpandedSelectionBoundingBox(@Nonnull AxisAlignedBB aabb) {
        drawSelectionBoundingBox(expandAABB(aabb));
    }

    public static void drawSelectionBoundingBox(@Nonnull AxisAlignedBB aabb) {
        mcTessellator.getBuffer().begin(3, DefaultVertexFormats.POSITION_COLOR);
        WorldRenderer.drawBoundingBox(new MatrixStack(), mcTessellator.getBuffer(), aabb, 0.0F, 0.0F, 0.0F, 0.4F);
        mcTessellator.draw();
    }

    @Nonnull
    public static AxisAlignedBB expandAABB(@Nonnull AxisAlignedBB aabb) {
        return aabb.grow(BB_EXPAND_NUMBER);
    }

    public static void drawSelectionBox(Entity player, World world, BlockPos pos, VoxelShape shapeOverride, float partialTicks) {
        double d0 = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * (double) partialTicks;
        double d1 = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * (double) partialTicks;
        double d2 = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * (double) partialTicks;
        drawSelectionBox(world, pos, shapeOverride, new Vec3d(d0, d1, d2));
    }

    public static void drawSelectionBox(World world, BlockPos pos, VoxelShape shapeOverride, Vec3d projectedView) {
        if (world.getWorldBorder().contains(pos)) {
            MatrixStack stack = new MatrixStack();
            double d0 = projectedView.x;
            double d1 = projectedView.y;
            double d2 = projectedView.z;
            RenderSystem.lineWidth(Math.max(2.5F, (float) getMainWindow().getFramebufferWidth() / 1920.0F * 2.5F));
            WorldRenderer.drawVoxelShapeParts(stack, mcTessellator.getBuffer(), shapeOverride, (double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }

    @Nonnull
    public static IForgeTransformationMatrix getTransformation(int x, int y, int z) {
        if ((z = MathHelper.normalizeAngle(z, 360)) == 0) {
            return ModelRotation.getModelRotation(x, y).getRotation();
        }
        return new TransformationMatrix(null, quatFromXYZDegrees(new Vector3f(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360), z)), null, null);
        //return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, TRSRTransformation.quatFromXYZDegrees(new Vector3f(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360), z)), null, null));
    }

    public static Quaternion quatFromXYZDegrees(Vector3f xyz) {
        return quatFromXYZ((float) Math.toRadians(xyz.getX()), (float) Math.toRadians(xyz.getY()), (float) Math.toRadians(xyz.getZ()));
    }

    public static Quaternion quatFromXYZ(Vector3f xyz) {
        return quatFromXYZ(xyz.getX(), xyz.getY(), xyz.getZ());
    }

    public static Quaternion quatFromXYZ(float x, float y, float z) {
        Quaternion ret = new Quaternion(0, 0, 0, 1), t = new Quaternion(0, 0, 0, 0);
        t.set((float) Math.sin(x / 2), 0, 0, (float) Math.cos(x / 2));
        ret.multiply(t);
        t.set(0, (float) Math.sin(y / 2), 0, (float) Math.cos(y / 2));
        ret.multiply(t);
        t.set(0, 0, (float) Math.sin(z / 2), (float) Math.cos(z / 2));
        ret.multiply(t);
        return ret;
    }

    public static IForgeTransformationMatrix[] getTranformationsFor(Direction axis) {
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
    public static ModelRotation getDefaultRotationFromFacing(Direction facing) {
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
        PlayerEntity player = ElecCore.proxy.getClientPlayer();
        double dX = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * partialTicks;
        double dY = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * partialTicks;
        double dZ = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * partialTicks;
        return new Vec3d(dX, dY, dZ);
    }

    public static void translateToWorld(float partialTicks) {
        Vec3d vec = getPlayerVec(partialTicks);
        RenderSystem.translated(-vec.x, -vec.y, -vec.z);
    }

    @Nonnull
    public static Vec3d getPlayerVec() {
        PlayerEntity player = ElecCore.proxy.getClientPlayer();
        return new Vec3d(player.getPosX(), player.getPosY(), player.getPosZ());
    }

    @Nonnull
    public static ClippingHelperImpl getPlayerCamera(float partialTicks) {
        MatrixStack stack = new MatrixStack();
        ActiveRenderInfo ri = mc.gameRenderer.getActiveRenderInfo();

        stack.getLast().getMatrix().mul(mc.gameRenderer.getProjectionMatrix(ri, partialTicks, true));

        Matrix4f projection = stack.getLast().getMatrix();

        stack.rotate(Vector3f.ZP.rotationDegrees(0)); //roll
        stack.rotate(Vector3f.XP.rotationDegrees(ri.getPitch()));
        stack.rotate(Vector3f.YP.rotationDegrees(ri.getYaw() + 180.0F));

        Matrix4f matrix4f = stack.getLast().getMatrix();

        ClippingHelperImpl clippingHelperImpl = new ClippingHelperImpl(matrix4f, projection);

        Vec3d vec3d = ri.getProjectedView();
        double d0 = vec3d.getX();
        double d1 = vec3d.getY();
        double d2 = vec3d.getZ();
        clippingHelperImpl.setCameraPosition(d0, d1, d2);

        return clippingHelperImpl;
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

    public static Texture getTextureObject(ResourceLocation location) {
        return getTextureManager().getTexture(location);
    }

    public static void bindBlockTextures() {
        bindTexture(getBlocksResourceLocation());
    }

    public static void bindTexture(ResourceLocation rl) {
        getTextureManager().bindTexture(rl);
    }

    public static TextureManager getTextureManager() {
        return mc.textureManager;
    }

    @Nonnull
    public static TextureAtlasSprite checkIcon(TextureAtlasSprite icon) {
        if (icon == null) {
            return getMissingTextureIcon();
        }
        return icon;
    }

    @Nonnull
    public static TextureAtlasSprite getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null) {
            return getMissingTextureIcon();
        }
        FluidAttributes fluidAttributes = fluid.getDefaultState().getFluid().getAttributes();
        return checkIcon((flowing ? getIconFrom(fluidAttributes.getFlowingTexture()) : getIconFrom(fluidAttributes.getStillTexture())));
    }

    @Nonnull
    public static TextureAtlasSprite getMissingTextureIcon() {
        return getBlockTextures().getSprite(MissingTextureSprite.getLocation());//MissingTextureSprite.func_217790_a();//.getSprite();//mc.getTextureMap().getMissingSprite();//((TextureMap) Minecraft.getInstance().getTextureManager().getTexture(getBlocksResourceLocation())).getAtlasSprite("missingno");
    }

    public static TextureAtlasSprite getIconFrom(ResourceLocation rl) {
        return getBlockTextures().getSprite(rl);
    }

    public static TextureAtlasSprite getIconFrom(String rl) {
        return getBlockTextures().getSprite(new ResourceLocation(rl));
    }

    @Nonnull
    public static ResourceLocation getBlocksResourceLocation() {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
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

    static {
        mc = Minecraft.getInstance();
        mcTessellator = Tessellator.getInstance();
        tessellator = new ElecTessellator(mcTessellator);
        mcRenderTypeBuffer = IRenderTypeBuffer.getImpl(mcTessellator.getBuffer());
        worldRenderTessellators = Maps.newHashMap();
        worldRenderTessellators.put(tessellator.getBuffer(), tessellator);
        rotateAroundMap = Maps.newEnumMap(Direction.class);
        for (Direction facing : Direction.values()) {
            switch (facing) {
                case NORTH:
                    rotateAroundMap.put(facing, new IForgeTransformationMatrix[]{RenderHelper.getTransformation(0, 0, 0), RenderHelper.getTransformation(0, 0, 90), RenderHelper.getTransformation(0, 0, 180), RenderHelper.getTransformation(0, 0, 270)});
                    break;
                case EAST:
                    rotateAroundMap.put(facing, new IForgeTransformationMatrix[]{RenderHelper.getTransformation(0, 90, 0), RenderHelper.getTransformation(180, -90, 90), RenderHelper.getTransformation(-90, -90, 90), RenderHelper.getTransformation(0, -90, 90)});
                    break;
                case SOUTH:
                    rotateAroundMap.put(facing, new IForgeTransformationMatrix[]{RenderHelper.getTransformation(180, 0, 0), RenderHelper.getTransformation(180, 0, 90), RenderHelper.getTransformation(180, 0, 180), RenderHelper.getTransformation(180, 0, -90)});
                    break;
                case WEST:
                    rotateAroundMap.put(facing, new IForgeTransformationMatrix[]{RenderHelper.getTransformation(0, 90, 180), RenderHelper.getTransformation(180, 90, 180), RenderHelper.getTransformation(90, 90, 180), RenderHelper.getTransformation(-90, 90, 180)});
                    break;
                case UP:
                    rotateAroundMap.put(facing, new IForgeTransformationMatrix[]{RenderHelper.getTransformation(-90, 0, 0), RenderHelper.getTransformation(90, 0, 90), RenderHelper.getTransformation(90, 0, 180), RenderHelper.getTransformation(90, 0, -90)});
                    break;
                case DOWN:
                    rotateAroundMap.put(facing, new IForgeTransformationMatrix[]{RenderHelper.getTransformation(90, 0, 0), RenderHelper.getTransformation(-90, 0, 90), RenderHelper.getTransformation(-90, 0, 180), RenderHelper.getTransformation(-90, 0, -90)});
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

}
