package elec332.core.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.ElecCore;
import elec332.core.api.client.ITessellator;
import elec332.core.api.client.ITextureLocation;
import elec332.core.client.model.legacy.LegacyTextureLocation;
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
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
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
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.*;
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
    public static final int MAX_BRIGHTNESS = 15728880;
    private static final Tessellator mcTessellator;
    private static final ITessellator tessellator;
    private static final IRenderTypeBuffer mcRenderTypeBuffer;
    private static final Minecraft mc;
    private static final Map<IVertexBuilder, ITessellator> worldRenderTessellators;
    private static final Map<Direction, IForgeTransformationMatrix[]> rotateAroundMap;
    private static final Map<ModelRotation, Integer> xRotation, yRotation;
    private static IBakedModel nullModel;

    @Nonnull
    @SuppressWarnings("all")
    public static ITessellator forWorldRenderer(IVertexBuilder renderer) {
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
        RenderSystem.enableColorMaterial();
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

    public static ITextureLocation createTextureLocation(ResourceLocation location) {
        return new LegacyTextureLocation(location);
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

    public static int drawString(String text, float x, float y, int color) {
        return getMCFontrenderer().drawString(new MatrixStack(), text, x, y, color);
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
        drawSelectionBox(world, pos, shapeOverride, new Vector3d(d0, d1, d2));
    }

    public static void drawSelectionBox(World world, BlockPos pos, VoxelShape shapeOverride, Vector3d projectedView) {
        if (world.getWorldBorder().contains(pos)) {
            MatrixStack stack = new MatrixStack();
            double d0 = projectedView.x;
            double d1 = projectedView.y;
            double d2 = projectedView.z;
            RenderSystem.lineWidth(Math.max(2.5F, (float) getMainWindow().getFramebufferWidth() / 1920.0F * 2.5F));
            WorldRenderer.drawVoxelShapeParts(stack, mcTessellator.getBuffer(), shapeOverride, (double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }

    public static void drawSelectionBox(World world, BlockPos pos, VoxelShape shapeOverride, Vector3d projectedView, MatrixStack renderer, IRenderTypeBuffer buffer) {
        if (world == null || world.getWorldBorder().contains(pos)) {
            double d0 = projectedView.x;
            double d1 = projectedView.y;
            double d2 = projectedView.z;
            RenderSystem.lineWidth(Math.max(2.5F, (float) getMainWindow().getFramebufferWidth() / 1920.0F * 2.5F));
            WorldRenderer.drawVoxelShapeParts(renderer, buffer.getBuffer(RenderType.getLines()), shapeOverride, (double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F);
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

    public static IForgeTransformationMatrix rotateFromDown(Direction insideSide) {
        int x = insideSide.getAxis() == Direction.Axis.Z ? 180 + (90 * insideSide.getAxisDirection().getOffset()) : insideSide == Direction.UP ? 180 : 0;
        int z = insideSide.getAxis() == Direction.Axis.X ? 180 - (90 * insideSide.getAxisDirection().getOffset()) : 0;
        return new TransformationMatrix(null, quatFromXYZDegrees(new Vector3f(x, 0, z)), null, null);
    }

    public static IForgeTransformationMatrix merge(IModelTransform first, IModelTransform second) {
        return merge(first, second.getRotation());
    }

    public static IForgeTransformationMatrix merge(IModelTransform first, IForgeTransformationMatrix second) {
        return merge(first.getRotation(), second);
    }

    public static IForgeTransformationMatrix merge(IForgeTransformationMatrix first, IModelTransform second) {
        return merge(first, second.getRotation());
    }

    public static IForgeTransformationMatrix merge(IForgeTransformationMatrix first, IForgeTransformationMatrix second) {
        Matrix4f m = new Matrix4f(second.getTransformaion().getMatrix());
        m.mul(first.getTransformaion().getMatrix());
        return new TransformationMatrix(m);
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
        return ModelRotation.getModelRotation(xRotation.get(rotation1) + xRotation.get(rotation2), yRotation.get(rotation1) + yRotation.get(rotation2));
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

    /**
     * Rotates the stack to face the player
     *
     * @param stack Matrix stack
     */
    public static void facingPlayer(MatrixStack stack) {
        stack.rotate(ClientHelper.getMinecraft().getRenderManager().info.getRotation());
    }

    @Nonnull
    public static Vector3d getPlayerVec(float partialTicks) {
        PlayerEntity player = ElecCore.proxy.getClientPlayer();
        double dX = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * partialTicks;
        double dY = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * partialTicks;
        double dZ = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * partialTicks;
        return new Vector3d(dX, dY, dZ);
    }

    public static void translateToWorld(float partialTicks) {
        Vector3d vec = getPlayerVec(partialTicks);
        RenderSystem.translated(-vec.x, -vec.y, -vec.z);
    }

    @Nonnull
    public static Vector3d getPlayerVec() {
        PlayerEntity player = ElecCore.proxy.getClientPlayer();
        return new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
    }

    @Nonnull
    public static ClippingHelper getPlayerCamera(float partialTicks) {
        MatrixStack stack = new MatrixStack();
        ActiveRenderInfo ri = mc.gameRenderer.getActiveRenderInfo();

        stack.getLast().getMatrix().mul(mc.gameRenderer.getProjectionMatrix(ri, partialTicks, true));

        Matrix4f projection = stack.getLast().getMatrix();

        stack.rotate(Vector3f.ZP.rotationDegrees(0)); //roll
        stack.rotate(Vector3f.XP.rotationDegrees(ri.getPitch()));
        stack.rotate(Vector3f.YP.rotationDegrees(ri.getYaw() + 180.0F));

        Matrix4f matrix4f = stack.getLast().getMatrix();

        ClippingHelper clippingHelperImpl = new ClippingHelper(matrix4f, projection);

        Vector3d Vector3d = ri.getProjectedView();
        double d0 = Vector3d.getX();
        double d1 = Vector3d.getY();
        double d2 = Vector3d.getZ();
        clippingHelperImpl.setCameraPosition(d0, d1, d2);

        return clippingHelperImpl;
    }

    public static void drawLine(Vector3d from, Vector3d to, Vector3d player, float thickness) {
        drawQuad(from, from.add(thickness, thickness, thickness), to, to.add(thickness, thickness, thickness));
    }

    public static void drawQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        tessellator.addVertexWithUV(v1.x, v1.y, v1.z, 0, 0);
        tessellator.addVertexWithUV(v2.x, v2.y, v2.z, 1, 0);
        tessellator.addVertexWithUV(v3.x, v3.y, v3.z, 1, 1);
        tessellator.addVertexWithUV(v4.x, v4.y, v4.z, 0, 1);
    }

    @Nonnull
    public static Vector3d multiply(Vector3d original, double m) {
        return new Vector3d(original.x * m, original.y * m, original.z * m);
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
        worldRenderTessellators.put(mcTessellator.getBuffer(), tessellator);
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
        xRotation = Maps.newEnumMap(ModelRotation.class);
        yRotation = Maps.newEnumMap(ModelRotation.class);
        for (int x = 0; x <= 360; x += 90) {
            for (int y = 0; y <= 360; y += 90) {
                ModelRotation rot = Preconditions.checkNotNull(ModelRotation.getModelRotation(x, y));
                xRotation.put(rot, x);
                yRotation.put(rot, y);
            }
        }
    }

}
