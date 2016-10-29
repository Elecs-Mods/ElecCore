package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.map.IBakedModelRotationMap;
import elec332.core.api.client.model.model.IQuadProvider;
import elec332.core.api.client.model.template.IModelTemplate;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import elec332.core.client.model.map.BakedModelRotationMap;
import elec332.core.client.model.template.ElecTemplateBakery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 5-12-2015.
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings({"WeakerAccess", "deprecation", "unused"})
public class ElecModelBakery implements IElecModelBakery {

    protected static final ElecModelBakery instance = new ElecModelBakery();
    private ElecModelBakery(){
    }

    private static final List<BakedQuad> EMPTY_LIST;
    private static final ElecQuadBakery quadBakery;
    private static final IModelTemplate defaultBlockTemplate, defaultItemTemplate;
    public static final ItemCameraTransforms DEFAULT_ITEM, DEFAULT_BLOCK;

    @Override
    public IBakedModelRotationMap<IBakedModel> forTemplateRotation(IModelTemplate template){
        return forTemplate(template, false, true);
    }

    @Override
    public IBakedModelRotationMap<IBakedModel> forTemplate(IModelTemplate template, boolean x, boolean y){
        BakedModelRotationMap<IBakedModel> ret = new BakedModelRotationMap<>(x, y);
        for (ModelRotation rotation : ModelRotation.values()){
            if (ret.isRotationSupported(rotation)){
                ret.setModel(rotation, forTemplate(template, rotation));
            }
        }
        return ret;
    }

    @Override
    public IBakedModel forTemplate(IModelTemplate template){
        return forTemplate(template, null);
    }

    @Override
    public IBakedModel forTemplate(IModelTemplate template, ModelRotation rotation){
        return forTemplateOverrideQuads(template, rotation, template.getSidedQuads(), template.getGeneralQuads());
    }

    @Override
    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, @Nullable IQuadTemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads){
        return forTemplateOverrideQuads(template, null, sidedQuads, generalQuads);
    }

    @Override
    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, ModelRotation rotation, @Nullable IQuadTemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads) {
        if (sidedQuads == null) {
            sidedQuads = ElecTemplateBakery.instance.newQuadSidedMap();
        }
        if (generalQuads == null) {
            generalQuads = ImmutableList.of();
        }
        DefaultBakedModel ret = new DefaultBakedModel(template);
        ret.setGeneralQuads(quadBakery.bakeQuads(generalQuads, rotation));
        ret.setSidedQuads(quadBakery.bakeQuads(sidedQuads, rotation));
        return ret;
    }

    @Override
    public IBakedModel itemModelForTextures(TextureAtlasSprite... textures){
        return itemModelForTextures(defaultItemTemplate, textures);
    }

    @Override
    public IBakedModel forQuadProvider(IModelTemplate template, final IQuadProvider quadProvider){
        return new DefaultBakedModel(template){

            @Override
            @Nonnull
            public List<BakedQuad> getQuads(IBlockState state, EnumFacing facing, long rand) {
                return quadProvider.getBakedQuads(state, facing, rand);
            }

        };
    }

    @Override
    public IBakedModel itemModelForTextures(IModelTemplate template, TextureAtlasSprite... textures){
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

    private BakedItemModel _forTemplateNoQuadsI(IModelTemplate template){
        return new BakedItemModel(template);
    }

    private class BakedItemModel extends DefaultBakedModel implements IBakedModel {

        private BakedItemModel(IModelTemplate template) {
            super(template);
        }

    }

    private class DefaultBakedModel implements IBakedModel {

        private DefaultBakedModel(IModelTemplate template){
            this.sidedQuads = null;
            this.generalQuads = EMPTY_LIST;
            this.ao = template.isAmbientOcclusion();
            this.gui3D = template.isGui3d();
            this.builtIn = template.isBuiltInRenderer();
            this.texture = template.getTexture();
            this.ict = template.getItemCameraTransforms();
        }

        private IQuadProvider sidedQuads;
        private List<BakedQuad> generalQuads;
        private final boolean ao, gui3D, builtIn;
        private final TextureAtlasSprite texture;
        private final ItemCameraTransforms ict;

        DefaultBakedModel setSidedQuads(@Nonnull IQuadProvider sidedQuads){
            this.sidedQuads = sidedQuads;
            return this;
        }

        DefaultBakedModel setGeneralQuads(@Nonnull List<BakedQuad> quads){
            this.generalQuads = quads;
            return this;
        }

        @Override
        @Nonnull
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing facing, long rand) {
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

        @Override
        public boolean isBuiltInRenderer() {
            return this.builtIn;
        }

        @Override
        @Nonnull
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
            return ItemOverrideList.NONE;
        }

    }

    static {
        EMPTY_LIST = ImmutableList.of();
        quadBakery = ElecQuadBakery.instance;
        DEFAULT_ITEM = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 1)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 1)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, -90, 25), applyTranslationScale(new Vector3f(1.13f, 3.2f, 1.13f)), new Vector3f(0.68f, 0.68f, 0.68f)), new ItemTransformVec3f(new Vector3f(0, -90, 25), applyTranslationScale(new Vector3f(1.13f, 3.2f, 1.13f)), new Vector3f(0.68f, 0.68f, 0.68f)), new ItemTransformVec3f(new Vector3f(0, 180, 0), applyTranslationScale(new Vector3f(0, 13, 7)), new Vector3f(1, 1, 1)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 2, 0)), new Vector3f(0.5f, 0.5f, 0.5f)), ItemTransformVec3f.DEFAULT);
        DEFAULT_BLOCK = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75, 45, 0), applyTranslationScale(new Vector3f(0, 2.5f, 0)), new Vector3f(0.375f, 0.375f, 0.375f)), new ItemTransformVec3f(new Vector3f(75, 45, 0), applyTranslationScale(new Vector3f(0, 2.5f, 0)), new Vector3f(0.375f, 0.375f, 0.375f)), new ItemTransformVec3f(new Vector3f(0, 225, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.40f, 0.40f, 0.40f)), new ItemTransformVec3f(new Vector3f(0, 45, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.40f, 0.40f, 0.40f)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(30, 225, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.625f, 0.625f, 0.625f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 3, 0)), new Vector3f(0.25f, 0.25f, 0.25f)), new ItemTransformVec3f(new Vector3f(0, 0, 0), applyTranslationScale(new Vector3f(0, 0, 0)), new Vector3f(0.5f, 0.5f, 0.5f)));
        defaultBlockTemplate = ElecTemplateBakery.instance.newDefaultBlockTemplate();
        defaultItemTemplate = ElecTemplateBakery.instance.newDefaultItemTemplate();
    }

    private static Vector3f applyTranslationScale(Vector3f vec){
        vec.scale(0.0625F);
        return vec;
    }

}
