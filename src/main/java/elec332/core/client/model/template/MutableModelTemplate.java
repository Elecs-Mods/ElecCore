package elec332.core.client.model.template;

import com.google.common.collect.Lists;
import elec332.core.api.client.model.template.IModelTemplate;
import elec332.core.api.client.model.template.IMutableModelTemplate;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import elec332.core.client.model.ElecModelBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 5-12-2015.
 */
@SuppressWarnings({"deprecation", "unused"})
@OnlyIn(Dist.CLIENT)
public class MutableModelTemplate implements IMutableModelTemplate {

    @Nonnull
    public static MutableModelTemplate newDefaultItemTemplate() {
        return newTemplate().setItemCameraTransforms(ElecModelBakery.DEFAULT_ITEM);
    }

    @Nonnull
    public static MutableModelTemplate newDefaultBlockTemplate() {
        return newTemplate().setGui3d(true).setItemCameraTransforms(ElecModelBakery.DEFAULT_BLOCK);
    }

    @Nonnull
    public static MutableModelTemplate newTemplate() {
        return new MutableModelTemplate();
    }

    @Nonnull
    public static MutableModelTemplate copyOf(IModelTemplate template) {
        MutableModelTemplate ret = newTemplate();
        ret.ao = template.isAmbientOcclusion();
        ret.gui3d = template.isGui3d();
        ret.builtIn = template.isBuiltInRenderer();
        ret.texture = template.getTexture();
        ret.transforms = template.getItemCameraTransforms();
        ret.generalQuads = Lists.newArrayList();
        for (IQuadTemplate quadTemplate : template.getGeneralQuads()) {
            ret.generalQuads.add(MutableQuadTemplate.copyOf(quadTemplate));
        }
        ret.sidedQuads = MutableQuadSidedMap.newQuadSidedMap();
        for (EnumFacing facing : EnumFacing.values()) {
            List<IQuadTemplate> toAdd = Lists.newArrayList();
            for (IQuadTemplate quadTemplate : template.getSidedQuads().getForSide(facing)) {
                toAdd.add(MutableQuadTemplate.copyOf(quadTemplate));
            }
            ret.sidedQuads.setQuadsForSide(facing, toAdd);
        }
        return ret;
    }

    private MutableModelTemplate() {
        this.transforms = ItemCameraTransforms.DEFAULT;
    }

    private boolean ao, gui3d, builtIn;
    private TextureAtlasSprite texture;
    private ItemCameraTransforms transforms;
    private List<IQuadTemplate> generalQuads;
    private IQuadTemplateSidedMap sidedQuads;

    @Override
    public MutableModelTemplate setGeneralQuads(List<IQuadTemplate> generalQuads) {
        this.generalQuads = generalQuads;
        return this;
    }

    @Override
    public MutableModelTemplate setSidedQuads(IQuadTemplateSidedMap sidedQuads) {
        this.sidedQuads = sidedQuads;
        return this;
    }

    @Override
    public MutableModelTemplate setAmbientOcclusion(boolean ao) {
        this.ao = ao;
        return this;
    }

    @Override
    public MutableModelTemplate setGui3d(boolean gui3d) {
        this.gui3d = gui3d;
        return this;
    }

    @Override
    public MutableModelTemplate setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
        return this;
    }

    @Override
    public MutableModelTemplate setTexture(TextureAtlasSprite texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public MutableModelTemplate setItemCameraTransforms(ItemCameraTransforms transforms) {
        this.transforms = transforms;
        return this;
    }

    @Override
    public List<IQuadTemplate> getGeneralQuads() {
        return this.generalQuads;
    }

    @Override
    public IQuadTemplateSidedMap getSidedQuads() {
        return sidedQuads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.ao;
    }

    @Override
    public boolean isGui3d() {
        return this.gui3d;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.builtIn;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return texture;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.transforms;
    }

}
