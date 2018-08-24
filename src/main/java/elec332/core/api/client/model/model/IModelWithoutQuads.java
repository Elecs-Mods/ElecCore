package elec332.core.api.client.model.model;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 15-11-2015.
 */
@SideOnly(Side.CLIENT)
public interface IModelWithoutQuads {

    public boolean isAmbientOcclusion();

    public boolean isGui3d();

    public boolean isBuiltInRenderer();

    public TextureAtlasSprite getParticleTexture();

    public ItemCameraTransforms getItemCameraTransforms();

}
