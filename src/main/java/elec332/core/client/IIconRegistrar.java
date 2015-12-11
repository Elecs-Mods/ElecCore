package elec332.core.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 9-12-2015.
 */
public interface IIconRegistrar {

    public TextureAtlasSprite registerSprite(ResourceLocation location);

    public TextureMap getTextureMap();

}
