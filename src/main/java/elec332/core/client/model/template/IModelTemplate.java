package elec332.core.client.model.template;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

/**
 * Created by Elec332 on 5-12-2015.
 */
public interface IModelTemplate {

    public List<IQuadTemplate> getGeneralQuads();

    public ITemplateSidedMap getSidedQuads();

    boolean isAmbientOcclusion();

    boolean isGui3d();

    boolean isBuiltInRenderer();

    TextureAtlasSprite getTexture();

    @SuppressWarnings("deprecation")
    ItemCameraTransforms getItemCameraTransforms();

}
