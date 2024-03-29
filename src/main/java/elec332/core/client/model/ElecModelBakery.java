package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.client.model.IModelBakery;
import elec332.core.api.client.model.ITemplateBakery;
import elec332.core.api.client.model.model.IModelWithoutQuads;
import elec332.core.api.client.model.model.IQuadProvider;
import elec332.core.api.client.model.template.IModelTemplate;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 5-12-2015.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("all")
public class ElecModelBakery implements IModelBakery {

    protected static final ElecModelBakery instance = new ElecModelBakery();

    private ElecModelBakery() {
    }

    private static final List<BakedQuad> EMPTY_LIST;
    private static final ElecQuadBakery quadBakery;
    private static IModelTemplate defaultBlockTemplate, defaultItemTemplate;
    private static ITemplateBakery templateBakery;
    public static final ItemCameraTransforms DEFAULT_ITEM, DEFAULT_BLOCK;


    @Override
    public IBakedModel forTemplate(IModelTemplate template) {
        return forTemplate(template, null);
    }

    @Override
    public IBakedModel forTemplate(IModelTemplate template, ModelRotation rotation) {
        return forTemplateOverrideQuads(template, rotation, template.getSidedQuads(), template.getGeneralQuads());
    }

    @Override
    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, @Nullable IQuadTemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads) {
        return forTemplateOverrideQuads(template, null, sidedQuads, generalQuads);
    }

    @Override
    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, ModelRotation rotation, @Nullable IQuadTemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads) {
        if (sidedQuads == null) {
            sidedQuads = templateBakery.newQuadSidedMap();
        }
        if (generalQuads == null) {
            generalQuads = ImmutableList.of();
        }
        DefaultBakedModel ret = new DefaultBakedModel(template);
        ret.setGeneralQuads(quadBakery.bakeQuads(generalQuads, rotation.getRotation()));
        ret.setSidedQuads(quadBakery.bakeQuads(sidedQuads, rotation.getRotation()));
        return ret;
    }

    @Override
    public IBakedModel itemModelForTextures(TextureAtlasSprite... textures) {
        return itemModelForTextures(defaultItemTemplate, textures);
    }

    @Override
    public IBakedModel forQuadProvider(IModelTemplate template, final IQuadProvider quadProvider) {
        return new DefaultBakedModel(template) {

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(BlockState state, Direction facing, Random rand) {
                return quadProvider.getBakedQuads(state, facing, rand);
            }

        };
    }

    @Override
    public IBakedModel forQuadProvider(IModelWithoutQuads modelWithoutQuads, IQuadProvider quadProvider) {
        return new DefaultBakedModel(modelWithoutQuads) {

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(BlockState state, Direction facing, Random rand) {
                return quadProvider.getBakedQuads(state, facing, rand);
            }

        };
    }

    @Override
    public IBakedModel itemModelForTextures(IModelTemplate template, TextureAtlasSprite... textures) {
        return _forTemplateNoQuadsI(template).setGeneralQuads(quadBakery.getGeneralItemQuads(textures));
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
    public void injectModelBakery(IAPIHandler apiHandler) {
        apiHandler.inject(instance, IModelBakery.class);
    }

    private BakedItemModel _forTemplateNoQuadsI(IModelTemplate template) {
        return new BakedItemModel(template);
    }

    private class BakedItemModel extends DefaultBakedModel implements IBakedModel {

        private BakedItemModel(IModelTemplate template) {
            super(template);
        }

    }

    private class DefaultBakedModel implements IBakedModel {

        private DefaultBakedModel(IModelWithoutQuads template) {
            this.sidedQuads = null;
            this.generalQuads = EMPTY_LIST;
            this.ao = template.isAmbientOcclusion();
            this.gui3D = template.isGui3d();
            this.builtIn = template.isBuiltInRenderer();
            this.texture = template.getParticleTexture();
            this.ict = template.getItemCameraTransforms();
            this.isSideLit = false; //todo
        }

        private DefaultBakedModel(IModelTemplate template) {
            this.sidedQuads = null;
            this.generalQuads = EMPTY_LIST;
            this.ao = template.isAmbientOcclusion();
            this.gui3D = template.isGui3d();
            this.builtIn = template.isBuiltInRenderer();
            this.texture = template.getTexture();
            this.ict = template.getItemCameraTransforms();
            this.isSideLit = false; //todo
        }

        private IQuadProvider sidedQuads;
        private List<BakedQuad> generalQuads;
        private final boolean ao, gui3D, builtIn, isSideLit;
        private final TextureAtlasSprite texture;
        private final ItemCameraTransforms ict;

        DefaultBakedModel setSidedQuads(@Nonnull IQuadProvider sidedQuads) {
            this.sidedQuads = sidedQuads;
            return this;
        }

        DefaultBakedModel setGeneralQuads(@Nonnull List<BakedQuad> quads) {
            this.generalQuads = quads;
            return this;
        }

        @Override
        @Nonnull
        public List<BakedQuad> getQuads(BlockState state, Direction facing, Random rand) {
            return facing == null ? generalQuads : sidedQuads == null ? EMPTY_LIST : sidedQuads.getBakedQuads(state, facing, rand);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return this.ao;
        }

        @Override
        public boolean isGui3d() {
            return this.gui3D;
        }

        @Override //isSideLit
        public boolean func_230044_c_() {
            return isSideLit;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return this.builtIn;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return this.texture;
        }

        @Override
        @Nonnull
        public ItemCameraTransforms getItemCameraTransforms() {
            return this.ict;
        }

        @Override
        @Nonnull
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.EMPTY;
        }

    }

    @APIHandlerInject
    public void getTemplates(ITemplateBakery templateBakery) {
        defaultBlockTemplate = templateBakery.newDefaultBlockTemplate();
        defaultItemTemplate = templateBakery.newDefaultItemTemplate();
        ElecModelBakery.templateBakery = templateBakery;
    }

    static {
        EMPTY_LIST = ImmutableList.of();
        quadBakery = ElecQuadBakery.instance;
        DEFAULT_ITEM = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 1)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 1)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, -90, 25), applyTranslationScale(new Vector3f(1.13f, 3.2f, 1.13f)), new Vector3f(0.68f, 0.68f, 0.68f)), new ItemTransformVec3f(new Vector3f(0, -90, 25), applyTranslationScale(new Vector3f(1.13f, 3.2f, 1.13f)), new Vector3f(0.68f, 0.68f, 0.68f)), new ItemTransformVec3f(new Vector3f(0, 180, 0), applyTranslationScale(new Vector3f(0, 13, 7)), new Vector3f(1, 1, 1)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 2, 0)), new Vector3f(0.5f, 0.5f, 0.5f)), ItemTransformVec3f.DEFAULT);
        DEFAULT_BLOCK = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75, 45, 0), applyTranslationScale(new Vector3f(0, 2.5f, 0)), new Vector3f(0.375f, 0.375f, 0.375f)), new ItemTransformVec3f(new Vector3f(75, 45, 0), applyTranslationScale(new Vector3f(0, 2.5f, 0)), new Vector3f(0.375f, 0.375f, 0.375f)), new ItemTransformVec3f(new Vector3f(0, 225, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.40f, 0.40f, 0.40f)), new ItemTransformVec3f(new Vector3f(0, 45, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.40f, 0.40f, 0.40f)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(30, 225, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.625f, 0.625f, 0.625f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 0)), new Vector3f(0.25f, 0.25f, 0.25f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.5f, 0.5f, 0.5f)));
    }

    private static Vector3f applyTranslationScale(Vector3f vec) {
        vec.mul(0.0625F);
        return vec;
    }

}
