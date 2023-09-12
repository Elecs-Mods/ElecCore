package elec332.core.api.client.model;

import elec332.core.api.client.model.template.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IElecTemplateBakery {

    @Nonnull
    public IMutableModelTemplate newDefaultItemTemplate();

    @Nonnull
    public IMutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite... textures);

    @Nonnull
    public IMutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite texture);

    @Nonnull
    public IMutableModelTemplate newDefaultBlockTemplate();

    @Nonnull
    public IMutableModelTemplate newModelTemplate();

    @Nonnull
    public IMutableModelTemplate copyOf(IModelTemplate template);

    @Nonnull
    public IMutableQuadTemplate templateQuadForTexture(Direction side, TextureAtlasSprite texture);

    @Nonnull
    public IMutableQuadTemplate newQuadTemplate(Direction side);

    @Nonnull
    public IMutableQuadTemplate copyOf(@Nonnull IQuadTemplate template);

    @Nonnull
    public IQuadTemplate makeImmutable(@Nonnull IQuadTemplate template);

    @Nonnull
    public IQuadTemplate.IMutableUVData forUV(float uMin, float vMin, float uMax, float vMax);

    @Nonnull
    public IQuadTemplate.IUVData makeImmutable(@Nonnull IQuadTemplate.IUVData data);

    @Nonnull
    public IQuadTemplateSidedMap newQuadSidedMap();

    @Nonnull
    public IQuadTemplateSidedMap newQuadSidedMap(TextureAtlasSprite texture);

    @Nonnull
    public IQuadTemplateSidedMap newQuadSidedMap(TextureAtlasSprite... textures);

}
