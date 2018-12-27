package elec332.core.api.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 9-12-2015.
 * <p>
 * Simple helper to register icons ({@link TextureAtlasSprite}'s)
 */
public interface IIconRegistrar {

    /**
     * Used for registering sprites
     *
     * @param location The sprite location
     * @return The registered sprite
     */
    public TextureAtlasSprite registerSprite(ResourceLocation location);

    /**
     * @return The underlying {@link TextureMap}
     */
    public TextureMap getTextureMap();

}
