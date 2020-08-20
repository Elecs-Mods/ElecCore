package elec332.core.api.client.model;

import elec332.core.api.client.model.model.IQuadProvider;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.extensions.IForgeTransformationMatrix;

import java.util.List;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IQuadBakery {

    /**
     * Bakes all the template quads in the IQuadTemplateSidedMap
     *
     * @param from The IQuadTemplateSidedMap containing the template quads.
     * @return The ISidedMap with the baked quads.
     */
    IQuadProvider bakeQuads(IQuadTemplateSidedMap from);

    /**
     * Bakes all the template quads in the ITemplateMap for a fixed rotation
     *
     * @param from     The IQuadTemplateSidedMap containing the template quads.
     * @param rotation The fixed quad rotation.
     * @return The ISidedMap with the baked quads.
     */
    IQuadProvider bakeQuads(IQuadTemplateSidedMap from, IForgeTransformationMatrix rotation);

    /**
     * Bakes all template quads in the list.
     *
     * @param from A list containing template quads.
     * @return A new list with the baked quads.
     */
    List<BakedQuad> bakeQuads(List<IQuadTemplate> from);

    /**
     * Bakes all template quads in the list for a fixed rotation.
     *
     * @param from     A list containing template quads.
     * @param rotation The fixed quad rotation.
     * @return A new list with the baked quads.
     */
    List<BakedQuad> bakeQuads(List<IQuadTemplate> from, IForgeTransformationMatrix rotation);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float uMin, float vMin, float uMax, float vMax);

    BakedQuad bakeQuad(IQuadTemplate template);

    BakedQuad bakeQuad(IQuadTemplate template, IForgeTransformationMatrix rotation);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, IQuadTemplate.IUVData uvData, int tint);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float uMin, float vMin, float uMax, float vMax, int tint);

    BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IModelTransform state, float uMin, float vMin, float uMax, float vMax, int tint);

    /**
     * Bakes the list of general quads for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return the list of baked quads for the given textures.
     */
    List<BakedQuad> getGeneralItemQuads(TextureAtlasSprite... textures);

}
