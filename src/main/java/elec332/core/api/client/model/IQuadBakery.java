package elec332.core.api.client.model;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.extensions.IForgeTransformationMatrix;

import java.util.List;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IQuadBakery {

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float uMin, float vMin, float uMax, float vMax);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float uMin, float vMin, float uMax, float vMax, int tint);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IModelTransform state, float uMin, float vMin, float uMax, float vMax, int tint);

    /**
     * Bakes the list of general quads for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return The list of baked quads for the given textures.
     */
    List<BakedQuad> createGeneralItemQuads(TextureAtlasSprite... textures);

    /**
     * Bakes a model for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return The baked model for the given textures.
     */
    IBakedModel itemModelForTextures(TextureAtlasSprite... textures);

    ItemCameraTransforms getDefaultItemTransformation();

    ItemCameraTransforms getDefaultBlockTransformation();

}
