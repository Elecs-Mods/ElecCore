package elec332.core.api.client.model;

import elec332.core.api.client.model.model.IModelWithoutQuads;
import elec332.core.api.client.model.model.IQuadProvider;
import elec332.core.api.client.model.template.IModelTemplate;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IElecModelBakery {

    public IBakedModel forTemplate(IModelTemplate template);

    public IBakedModel forTemplate(IModelTemplate template, ModelRotation rotation);

    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, @Nullable IQuadTemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads);

    public IBakedModel forTemplateOverrideQuads(IModelTemplate template, ModelRotation rotation, @Nullable IQuadTemplateSidedMap sidedQuads, @Nullable List<IQuadTemplate> generalQuads);

    public IBakedModel itemModelForTextures(TextureAtlasSprite... textures);

    public IBakedModel forQuadProvider(IModelTemplate template, final IQuadProvider quadProvider);

    public IBakedModel forQuadProvider(IModelWithoutQuads modelWithoutQuads, final IQuadProvider quadProvider);

    public IBakedModel itemModelForTextures(IModelTemplate template, TextureAtlasSprite... textures);

    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getDefaultItemTransformation();

    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getDefaultBlockTransformation();

}
