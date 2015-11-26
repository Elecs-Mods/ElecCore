package elec332.core.client.model.model;

import elec332.core.client.model.ElecQuadBakery;
import elec332.core.client.model.RenderingRegistry;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 21-11-2015.
 */
public interface IModelAndTextureLoader {

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     *
     * @param quadBakery The QuadBakery.
     */
    @SideOnly(Side.CLIENT)
    public void registerModels(ElecQuadBakery quadBakery, RenderingRegistry renderingRegistry);

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to register your textures.
     *
     * @param textureMap The TextureMap.
     */
    @SideOnly(Side.CLIENT)
    public void registerTextures(TextureMap textureMap, RenderingRegistry renderingRegistry);

}
