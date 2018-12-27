package elec332.core.client.model.template;

import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.api.client.model.template.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public final class ElecTemplateBakery implements IElecTemplateBakery {

    private static final ElecTemplateBakery instance = new ElecTemplateBakery();

    private ElecTemplateBakery() {
    }

    @Nonnull
    @Override
    public IMutableModelTemplate newDefaultItemTemplate() {
        return MutableModelTemplate.newDefaultItemTemplate();
    }

    @Nonnull
    @Override
    public IMutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite... textures) {
        return newDefaultBlockTemplate().setSidedQuads(newQuadSidedMap(textures));
    }

    @Nonnull
    @Override
    public IMutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite texture) {
        return newDefaultBlockTemplate().setTexture(texture).setSidedQuads(newQuadSidedMap(texture));
    }

    @Nonnull
    @Override
    public IMutableModelTemplate newDefaultBlockTemplate() {
        return MutableModelTemplate.newDefaultBlockTemplate();
    }

    @Nonnull
    @Override
    public IMutableModelTemplate newModelTemplate() {
        return MutableModelTemplate.newTemplate();
    }

    @Nonnull
    @Override
    public IMutableModelTemplate copyOf(IModelTemplate template) {
        return MutableModelTemplate.copyOf(template);
    }

    @Nonnull
    public IMutableQuadTemplate templateQuadForTexture(EnumFacing side, TextureAtlasSprite texture) {
        return MutableQuadTemplate.templateForTexture(side, texture);
    }

    @Nonnull
    @Override
    public IMutableQuadTemplate newQuadTemplate(EnumFacing side) {
        return MutableQuadTemplate.newTemplate(side);
    }

    @Nonnull
    @Override
    public IMutableQuadTemplate copyOf(@Nonnull IQuadTemplate template) {
        return MutableQuadTemplate.copyOf(template);
    }

    @Nonnull
    @Override
    public IQuadTemplate makeImmutable(@Nonnull IQuadTemplate template) {
        return MutableQuadTemplate.copyOf(template);//todo
    }

    @Nonnull
    @Override
    public IQuadTemplate.IMutableUVData forUV(float uMin, float vMin, float uMax, float vMax) {
        return MutableQuadTemplate.forUV(uMin, vMin, uMax, vMax);
    }

    @Nonnull
    @Override
    public IQuadTemplate.IUVData makeImmutable(@Nonnull IQuadTemplate.IUVData data) {
        return MutableQuadTemplate.makeImmutable(data);
    }

    @Nonnull
    @Override
    public IQuadTemplateSidedMap newQuadSidedMap() {
        return MutableQuadSidedMap.newQuadSidedMap();
    }

    @Nonnull
    @Override
    public IQuadTemplateSidedMap newQuadSidedMap(TextureAtlasSprite texture) {
        IQuadTemplateSidedMap ret = newQuadSidedMap();
        for (EnumFacing facing : EnumFacing.VALUES) {
            ret.addQuadForSide(facing, templateQuadForTexture(facing, texture));
        }
        return ret;
    }

    @Nonnull
    @Override
    public IQuadTemplateSidedMap newQuadSidedMap(TextureAtlasSprite... textures) {
        if (textures.length != EnumFacing.values().length)
            throw new IllegalArgumentException();
        IQuadTemplateSidedMap ret = newQuadSidedMap();
        for (int i = 0; i < textures.length; i++) {
            ret.addQuadForSide(EnumFacing.VALUES[i], templateQuadForTexture(EnumFacing.VALUES[i], textures[i]));
        }
        return ret;
    }

    @APIHandlerInject
    public void injectTemplateBakery(IAPIHandler apiHandler) {
        apiHandler.inject(instance, IElecTemplateBakery.class);
    }

}
