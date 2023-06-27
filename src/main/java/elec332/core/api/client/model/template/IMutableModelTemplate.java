package elec332.core.api.client.model.template;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IMutableModelTemplate extends IModelTemplate {

    IMutableModelTemplate setGeneralQuads(List<IQuadTemplate> generalQuads);

    IMutableModelTemplate setSidedQuads(IQuadTemplateSidedMap sidedQuads);

    IMutableModelTemplate setAmbientOcclusion(boolean ao);

    IMutableModelTemplate setGui3d(boolean gui3d);

    IMutableModelTemplate setBuiltIn(boolean builtIn);

    IMutableModelTemplate setTexture(TextureAtlasSprite texture);

    IMutableModelTemplate setItemCameraTransforms(ItemCameraTransforms transforms);

}
