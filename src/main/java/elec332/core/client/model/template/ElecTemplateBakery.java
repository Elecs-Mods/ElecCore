package elec332.core.client.model.template;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public final class ElecTemplateBakery {

    public static final ElecTemplateBakery instance = new ElecTemplateBakery();
    private ElecTemplateBakery(){
    }

    @Nonnull
    public MutableModelTemplate newDefaultItemTemplate(){
        return MutableModelTemplate.newDefaultItemTemplate();
    }

    @Nonnull
    public MutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite... textures){
        return newDefaultBlockTemplate().setSidedQuads(newQuadSidedMap(textures));
    }

    @Nonnull
    public MutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite texture){
        return newDefaultBlockTemplate().setTexture(texture).setSidedQuads(newQuadSidedMap(texture));
    }

    @Nonnull
    public MutableModelTemplate newDefaultBlockTemplate(){
        return MutableModelTemplate.newDefaultBlockTemplate();
    }

    @Nonnull
    public MutableModelTemplate newModelTemplate(){
        return MutableModelTemplate.newTemplate();
    }

    @Nonnull
    public MutableModelTemplate copyOf(IModelTemplate template){
        return MutableModelTemplate.copyOf(template);
    }

    @Nonnull
    public MutableQuadTemplate templateQuadForTexture(EnumFacing side, TextureAtlasSprite texture){
        return MutableQuadTemplate.templateForTexture(side, texture);
    }

    @Nonnull
    public MutableQuadTemplate newQuadTemplate(EnumFacing side){
        return MutableQuadTemplate.newTemplate(side);
    }

    @Nonnull
    public MutableQuadTemplate copyOf(IQuadTemplate template) {
        return MutableQuadTemplate.copyOf(template);
    }

    @Nonnull
    public MutableQuadSidedMap newQuadSidedMap(){
        return MutableQuadSidedMap.newQuadSidedMap();
    }

    @Nonnull
    public MutableQuadSidedMap newQuadSidedMap(TextureAtlasSprite texture){
        MutableQuadSidedMap ret = newQuadSidedMap();
        for (EnumFacing facing : EnumFacing.VALUES){
            ret.addQuadForSide(facing, templateQuadForTexture(facing, texture));
        }
        return ret;
    }

    @Nonnull
    public MutableQuadSidedMap newQuadSidedMap(TextureAtlasSprite... textures){
        if (textures.length != EnumFacing.values().length)
            throw new IllegalArgumentException();
        MutableQuadSidedMap ret = newQuadSidedMap();
        for (int i = 0; i < textures.length; i++) {
            ret.addQuadForSide(EnumFacing.VALUES[i], templateQuadForTexture(EnumFacing.VALUES[i], textures[i]));
        }
        return ret;
    }

}
