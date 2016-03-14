package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import elec332.core.client.model.map.BakedModelRotationMap;
import elec332.core.client.model.map.IBakedModelRotationMap;
import elec332.core.client.model.model.IItemModel;
import elec332.core.client.model.model.IQuadProvider;
import elec332.core.client.model.template.*;
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
@SuppressWarnings({"deprecation", "unused"})
@SideOnly(Side.CLIENT)
public class ElecModelBakery {

    protected static final ElecModelBakery instance = new ElecModelBakery();
    private ElecModelBakery(){
    }

    private static final List<BakedQuad> EMPTY_LIST;
    private static final ElecQuadBakery quadBakery;
    private static final IModelTemplate defaultBlockTemplate, defaultItemTemplate;
    public static final ItemCameraTransforms DEFAULT_ITEM, DEFAULT_BLOCK;

    public IBakedModelRotationMap<IBakedModel> forTemplateRotation(IModelTemplate template){
        return forTemplate(template, false, true);
    }

    public IBakedModelRotationMap<IBakedModel> forTemplate(IModelTemplate template, boolean x, boolean y){
        BakedModelRotationMap<IBakedModel> ret = new BakedModelRotationMap<IBakedModel>(x, y);
        for (ModelRotation rotation : ModelRotation.values()){
            if (ret.isRotationSupported(rotation)){
                ret.setModel(rotation, forTemplate(template, rotation));
            }
        }
        return ret;
    }

    public IBakedModel forTemplate(IModelTemplate template){
        return forTemplate(template, null);
    }

    public IBakedModel forTemplate(IModelTemplate template, ModelRotation rotation){
        return forTemplateOverrideQuads(template, rotation, template.getSidedQuads(), template.getGeneralQuads());
    }

    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, @Nullable ITemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads){
        return forTemplateOverrideQuads(template, null, sidedQuads, generalQuads);
    }

    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, ModelRotation rotation, @Nullable ITemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads) {
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

    public IItemModel itemModelForTextures(TextureAtlasSprite... textures){
        return itemModelForTextures(defaultItemTemplate, textures);
    }

    public IBakedModel forQuadProvider(IModelTemplate template, final IQuadProvider quadProvider){
        return new DefaultBakedModel(template){
            @Override
            public List<BakedQuad> func_188616_a(IBlockState p_188616_1_, EnumFacing p_188616_2_, long p_188616_3_) {
                return quadProvider.getBakedQuads(p_188616_1_, p_188616_2_, p_188616_3_);
            }
        };
    }

    /*private class BakedQuadProvider implements IQuadProvider {

        private BakedQuadProvider(){
            generalQuads = ImmutableList.of();
        }

        private List<BakedQuad> generalQuads;
        private ElecQuadBakery.ISidedMap sidedMap;

        @Override
        public List<BakedQuad> getBakedQuads(IBlockState state, EnumFacing side, long random) {
            return side == null ? generalQuads : sidedMap.getForSide(side);
        }

    }*/

    public IItemModel itemModelForTextures(IModelTemplate template, TextureAtlasSprite... textures){
        return (IItemModel) _forTemplateNoQuadsI(template).setGeneralQuads(quadBakery.getGeneralItemQuads(textures));
    }

    private BakedItemModel _forTemplateNoQuadsI(IModelTemplate template){
        return new BakedItemModel(template);
    }

    private class BakedItemModel extends DefaultBakedModel implements IItemModel {

        private BakedItemModel(IModelTemplate template) {
            super(template);
        }

    }

    @SuppressWarnings("deprecation")
    private class DefaultBakedModel implements IBakedModel {

        private DefaultBakedModel(IModelTemplate template){
            this.sidedQuads = quadBakery.newSidedMap();
            this.generalQuads = EMPTY_LIST;
            this.ao = template.isAmbientOcclusion();
            this.gui3D = template.isGui3d();
            this.builtIn = template.isBuiltInRenderer();
            this.texture = template.getTexture();
            this.ict = template.getItemCameraTransforms();
        }

        private ElecQuadBakery.ISidedMap sidedQuads;
        private List<BakedQuad> generalQuads;
        private final boolean ao, gui3D, builtIn;
        private final TextureAtlasSprite texture;
        private final ItemCameraTransforms ict;

        DefaultBakedModel setSidedQuads(@Nonnull ElecQuadBakery.ISidedMap sidedQuads){
            this.sidedQuads = sidedQuads;
            return this;
        }

        DefaultBakedModel setGeneralQuads(@Nonnull List<BakedQuad> quads){
            this.generalQuads = quads;
            return this;
        }

        @Override
        public List<BakedQuad> func_188616_a(IBlockState p_188616_1_, EnumFacing p_188616_2_, long p_188616_3_) {
            return p_188616_2_ == null ? generalQuads : sidedQuads.getForSide(p_188616_2_);
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
        public boolean func_188618_c() {
            return isBuiltInRenderer();
        }

        //@Override
        public boolean isBuiltInRenderer() {
            return this.builtIn;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return this.texture;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return this.ict;
        }

        @Override
        public ItemOverrideList func_188617_f() {
            return null;
        }

    }

    static {
        EMPTY_LIST = ImmutableList.of();
        quadBakery = ElecQuadBakery.instance;
        DEFAULT_ITEM = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(-90, 0, 0), applyTranslationScale(new Vector3f(0, 1, -3)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, -135, 25), applyTranslationScale(new Vector3f(0, 4, 2)), new Vector3f(1.7f, 1.7f, 1.7f)), ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
        DEFAULT_BLOCK = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(10, -45, 170), applyTranslationScale(new Vector3f(0, 1.5f, -2.75f)), new Vector3f(0.375f, 0.375f, 0.375f)), ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
        defaultBlockTemplate = ElecTemplateBakery.instance.newDefaultBlockTemplate();
        defaultItemTemplate = ElecTemplateBakery.instance.newDefaultItemTemplate();
    }

    private static Vector3f applyTranslationScale(Vector3f vec){
        vec.scale(0.0625F);
        return vec;
    }

}
