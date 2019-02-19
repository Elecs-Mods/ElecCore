package elec332.core.api.client.model.template;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Created by Elec332 on 5-12-2015.
 */
@OnlyIn(Dist.CLIENT)
public interface IModelTemplate extends IQuadProviderTemplate {

    public List<IQuadTemplate> getGeneralQuads();

    public IQuadTemplateSidedMap getSidedQuads();

    boolean isAmbientOcclusion();

    boolean isGui3d();

    boolean isBuiltInRenderer();

    TextureAtlasSprite getTexture();

    @SuppressWarnings("deprecation")
    ItemCameraTransforms getItemCameraTransforms();

}
