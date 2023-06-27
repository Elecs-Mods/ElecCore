package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.client.model.IQuadBakery;
import elec332.core.client.model.model.AbstractItemModel;
import elec332.core.client.util.SimpleModelTransform;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeTransformationMatrix;
import net.minecraftforge.client.model.ItemLayerModel;

import java.util.List;

/**
 * Created by Elec332 on 15-11-2015.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"WeakerAccess", "unused", "deprecation"})
public class ElecQuadBakery implements IQuadBakery {

    protected static final ElecQuadBakery instance = new ElecQuadBakery();

    private ElecQuadBakery() {
        this.faceBakery = new FaceBakery(); //Because MC is selfish and keeps his private...
    }

    public static final ItemCameraTransforms DEFAULT_ITEM, DEFAULT_BLOCK;
    private final FaceBakery faceBakery;

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing) {
        return bakeQuad(v1, v2, texture, facing, ModelRotation.X0_Y0.getRotation());
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation) {
        return bakeQuad(v1, v2, texture, facing, rotation, 0, 0, 16, 16);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float f1, float f2, float f3, float f4) {
        return bakeQuad(v1, v2, texture, facing, rotation, f1, f2, f3, f4, -1);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float f1, float f2, float f3, float f4, int tint) {
        return bakeQuad(v1, v2, texture, facing, new SimpleModelTransform(rotation.getTransformaion(), false), f1, f2, f3, f4, tint);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IModelTransform rotation, float f1, float f2, float f3, float f4, int tint) {
        BlockFaceUV bfuv = new BlockFaceUV(new float[]{f1, f2, f3, f4}, 0);
        BlockPartFace bpf = new BlockPartFace(rotation.getPartTransformation(null).rotateTransform(facing), tint, "", bfuv);
        return faceBakery.bakeQuad(v1, v2, bpf, texture, facing, rotation, null, true, null);
        //return faceBakery.makeBakedQuad(v1, v2, bpf, texture, facing, rotation, null, true);
    }

    /**
     * Bakes the list of general quads for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return the list of baked quads for the given textures.
     */
    @Override
    public List<BakedQuad> createGeneralItemQuads(TextureAtlasSprite... textures) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        for (int i = 0; i < textures.length; i++) {
            builder.addAll(ItemLayerModel.getQuadsForSprite(i, textures[i], TransformationMatrix.identity()));
        }
        return builder.build();
    }

    @Override
    public IBakedModel itemModelForTextures(TextureAtlasSprite... textures) {
        if (textures.length == 0) {
            throw new UnsupportedOperationException();
        }
        final List<BakedQuad> generalQuads = createGeneralItemQuads(textures);
        return new AbstractItemModel() {

            @Override
            public List<BakedQuad> getGeneralQuads() {
                return generalQuads;
            }

            @Override
            public ResourceLocation getTextureLocation() {
                return textures[0].getName();
            }

            @Override
            public boolean func_230044_c_() {
                return false;
            }

        };
    }

    @Override
    public ItemCameraTransforms getDefaultItemTransformation() {
        return DEFAULT_ITEM;
    }

    @Override
    public ItemCameraTransforms getDefaultBlockTransformation() {
        return DEFAULT_BLOCK;
    }

    @APIHandlerInject
    public void injectQuadBakery(IAPIHandler apiHandler) {
        apiHandler.inject(instance, IQuadBakery.class);
    }

    static {
        DEFAULT_ITEM = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 1)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 1)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, -90, 25), applyTranslationScale(new Vector3f(1.13f, 3.2f, 1.13f)), new Vector3f(0.68f, 0.68f, 0.68f)), new ItemTransformVec3f(new Vector3f(0, -90, 25), applyTranslationScale(new Vector3f(1.13f, 3.2f, 1.13f)), new Vector3f(0.68f, 0.68f, 0.68f)), new ItemTransformVec3f(new Vector3f(0, 180, 0), applyTranslationScale(new Vector3f(0, 13, 7)), new Vector3f(1, 1, 1)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 2, 0)), new Vector3f(0.5f, 0.5f, 0.5f)), ItemTransformVec3f.DEFAULT);
        DEFAULT_BLOCK = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75, 45, 0), applyTranslationScale(new Vector3f(0, 2.5f, 0)), new Vector3f(0.375f, 0.375f, 0.375f)), new ItemTransformVec3f(new Vector3f(75, 45, 0), applyTranslationScale(new Vector3f(0, 2.5f, 0)), new Vector3f(0.375f, 0.375f, 0.375f)), new ItemTransformVec3f(new Vector3f(0, 225, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.40f, 0.40f, 0.40f)), new ItemTransformVec3f(new Vector3f(0, 45, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.40f, 0.40f, 0.40f)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(30, 225, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.625f, 0.625f, 0.625f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 0)), new Vector3f(0.25f, 0.25f, 0.25f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.5f, 0.5f, 0.5f)));
    }

    private static Vector3f applyTranslationScale(Vector3f vec) {
        vec.mul(0.0625F);
        return vec;
    }

}
