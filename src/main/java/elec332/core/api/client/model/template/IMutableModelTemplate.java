package elec332.core.api.client.model.template;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IMutableModelTemplate extends IModelTemplate {

    public IMutableModelTemplate setGeneralQuads(List<IQuadTemplate> generalQuads);

    public IMutableModelTemplate setSidedQuads(IQuadTemplateSidedMap sidedQuads);

    public IMutableModelTemplate setAmbientOcclusion(boolean ao);

    public IMutableModelTemplate setGui3d(boolean gui3d);

    public IMutableModelTemplate setBuiltIn(boolean builtIn);

    public IMutableModelTemplate setTexture(TextureAtlasSprite texture);

    @SuppressWarnings("deprecation")
    public IMutableModelTemplate setItemCameraTransforms(ItemCameraTransforms transforms);

}
