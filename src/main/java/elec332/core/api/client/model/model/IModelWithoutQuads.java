package elec332.core.api.client.model.model;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 15-11-2015.
 */
@OnlyIn(Dist.CLIENT)
public interface IModelWithoutQuads {

    boolean isAmbientOcclusion();

    boolean isGui3d();

    boolean isBuiltInRenderer();

    TextureAtlasSprite getParticleTexture();

    ItemCameraTransforms getItemCameraTransforms();

}
