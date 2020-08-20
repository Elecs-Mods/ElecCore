package elec332.core.api.client.model;

import elec332.core.api.client.model.template.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface ITemplateBakery {

    @Nonnull
    IMutableModelTemplate newDefaultItemTemplate();

    @Nonnull
    IMutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite... textures);

    @Nonnull
    IMutableModelTemplate newDefaultBlockTemplate(TextureAtlasSprite texture);

    @Nonnull
    IMutableModelTemplate newDefaultBlockTemplate();

    @Nonnull
    IMutableModelTemplate newModelTemplate();

    @Nonnull
    IMutableModelTemplate copyOf(IModelTemplate template);

    @Nonnull
    IMutableQuadTemplate templateQuadForTexture(Direction side, TextureAtlasSprite texture);

    @Nonnull
    IMutableQuadTemplate newQuadTemplate(Direction side);

    @Nonnull
    IMutableQuadTemplate copyOf(@Nonnull IQuadTemplate template);

    @Nonnull
    IQuadTemplate makeImmutable(@Nonnull IQuadTemplate template);

    @Nonnull
    IQuadTemplate.IMutableUVData forUV(float uMin, float vMin, float uMax, float vMax);

    @Nonnull
    IQuadTemplate.IUVData makeImmutable(@Nonnull IQuadTemplate.IUVData data);

    @Nonnull
    IQuadTemplateSidedMap newQuadSidedMap();

    @Nonnull
    IQuadTemplateSidedMap newQuadSidedMap(TextureAtlasSprite texture);

    @Nonnull
    IQuadTemplateSidedMap newQuadSidedMap(TextureAtlasSprite... textures);

}
