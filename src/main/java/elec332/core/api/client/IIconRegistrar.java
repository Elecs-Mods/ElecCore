package elec332.core.api.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

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
     * @return The underlying {@link TextureAtlas}
     */
    public TextureAtlas getTextureMap();

}
