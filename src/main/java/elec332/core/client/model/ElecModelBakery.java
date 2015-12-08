package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import elec332.core.client.model.map.BakedModelRotationMap;
import elec332.core.client.model.map.IBakedModelRotationMap;
import elec332.core.client.model.model.IBlockModel;
import elec332.core.client.model.model.IItemModel;
import elec332.core.client.model.template.ElecTemplateBakery;
import elec332.core.client.model.template.IModelTemplate;
import elec332.core.client.model.template.IQuadTemplate;
import elec332.core.client.model.template.ITemplateSidedMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Elec332 on 5-12-2015.
 */
@SuppressWarnings("deprecation")
public class ElecModelBakery {

    protected static final ElecModelBakery instance = new ElecModelBakery();
    private ElecModelBakery(){
    }

    private static final List<BakedQuad> EMPTY_LIST;
    private static final ElecQuadBakery quadBakery;
    private static final IModelTemplate defaultBlockTemplate, defaultItemTemplate;
    public static final ItemCameraTransforms DEFAULT_ITEM, DEFAULT_BLOCK;

    public IBakedModelRotationMap<IBlockModel> forTemplateRotation(IModelTemplate template){
        return forTemplate(template, false, true);
    }

    public IBakedModelRotationMap<IBlockModel> forTemplate(IModelTemplate template, boolean x, boolean y){
        BakedModelRotationMap<IBlockModel> ret = new BakedModelRotationMap<IBlockModel>(x, y);
        for (ModelRotation rotation : ModelRotation.values()){
            if (ret.isRotationSupported(rotation)){
                ret.setModel(rotation, forTemplate(template, rotation));
            }
        }
        return ret;
    }

    public IBlockModel forTemplate(IModelTemplate template){
        return forTemplate(template, null);
    }

    public IBlockModel forTemplate(IModelTemplate template, ModelRotation rotation){
        return forTemplateOverrideQuads(template, rotation, template.getSidedQuads(), template.getGeneralQuads());
    }

    public IBlockModel forTemplateOverrideQuads(IModelTemplate template, @Nullable ITemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads){
        return forTemplateOverrideQuads(template, null, sidedQuads, generalQuads);
    }

    public IBlockModel forTemplateOverrideQuads(IModelTemplate template, ModelRotation rotation, @Nullable ITemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads){
        if (sidedQuads == null){
            sidedQuads = ElecTemplateBakery.instance.newQuadSidedMap();
        }
        if (generalQuads == null){
            generalQuads = ImmutableList.of();
        }
        BakedBlockModel ret = _forTemplateNoQuadsB(template);
        ret.setGeneralQuads(quadBakery.bakeQuads(generalQuads, rotation));
        ret.setSidedQuads(quadBakery.bakeQuads(sidedQuads, rotation));
        return ret;
    }

    public IBlockModel forTemplateNoQuadsB(IModelTemplate template){
        return _forTemplateNoQuadsB(template);
    }

    private BakedBlockModel _forTemplateNoQuadsB(IModelTemplate template){
        return new BakedBlockModel(template);
    }

    private class BakedBlockModel extends DefaultBakedModel implements IBlockModel {

        private BakedBlockModel(IModelTemplate template) {
            super(template);
        }

        @Override
        public ISmartBlockModel handleBlockState(IBlockState state) {
            return this;
        }

    }

    public IItemModel itemModelForTextures(TextureAtlasSprite... textures){
        return itemModelForTextures(defaultItemTemplate, textures);
    }

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

        @Override
        public IBakedModel handleItemState(ItemStack stack) {
            return this;
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
        public List<BakedQuad> getFaceQuads(EnumFacing facing) {
            return sidedQuads.getForSide(facing);
        }

        @Override
        public List<BakedQuad> getGeneralQuads() {
            return generalQuads;
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
        public TextureAtlasSprite getTexture() {
            return this.texture;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return this.ict;
        }

    }

    static {
        EMPTY_LIST = ImmutableList.of();
        quadBakery = ElecQuadBakery.instance;
        defaultBlockTemplate = ElecTemplateBakery.instance.newDefaultBlockTemplate();
        defaultItemTemplate = ElecTemplateBakery.instance.newDefaultItemTemplate();
        DEFAULT_ITEM = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(-90, 0, 0), applyTranslationScale(new Vector3f(0, 1, -3)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, -135, 25), applyTranslationScale(new Vector3f(0, 4, 2)), new Vector3f(1.7f, 1.7f, 1.7f)), ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
        DEFAULT_BLOCK = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(10, -45, 170), applyTranslationScale(new Vector3f(0, 1.5f, -2.75f)), new Vector3f(0.375f, 0.375f, 0.375f)), ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    private static Vector3f applyTranslationScale(Vector3f vec){
        vec.scale(0.0625F);
        return vec;
    }

}
