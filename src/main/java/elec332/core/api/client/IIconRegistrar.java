package elec332.core.api.client;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
    TextureAtlasSprite registerSprite(ResourceLocation location);

    /**
     * @return The underlying {@link AtlasTexture}
     */
    AtlasTexture getTextureMap();

}
