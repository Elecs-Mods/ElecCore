package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.model.IQuadProvider;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import elec332.core.client.util.SimpleModelTransform;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeTransformationMatrix;
import net.minecraftforge.client.model.ItemLayerModel;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 15-11-2015.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"WeakerAccess", "unused"})
public class ElecQuadBakery implements IElecQuadBakery {

    protected static final ElecQuadBakery instance = new ElecQuadBakery();

    private ElecQuadBakery() {
        this.faceBakery = new FaceBakery(); //Because MC is selfish and keeps his private...
    }

    private final FaceBakery faceBakery;
    private static final List<BakedQuad> EMPTY_LIST;

    /**
     * Bakes all the template quads in the IQuadTemplateSidedMap
     *
     * @param from The IQuadTemplateSidedMap containing the template quads.
     * @return The ISidedMap with the baked quads.
     */
    @Override
    public IQuadProvider bakeQuads(IQuadTemplateSidedMap from) {
        return bakeQuads(from, null);
    }

    /**
     * Bakes all the template quads in the ITemplateMap for a fixed rotation
     *
     * @param from     The IQuadTemplateSidedMap containing the template quads.
     * @param rotation The fixed quad rotation.
     * @return The ISidedMap with the baked quads.
     */
    @Override
    public IQuadProvider bakeQuads(IQuadTemplateSidedMap from, IForgeTransformationMatrix rotation) {
        SidedMap ret = new SidedMap();
        for (Direction facing : Direction.values()) {
            ret.setQuadsForSide(rotation == null ? facing : rotation.rotateTransform(facing), bakeQuads(from.getForSide(facing), rotation));
        }
        return ret;
    }

    /**
     * Bakes all template quads in the list.
     *
     * @param from A list containing template quads.
     * @return A new list with the baked quads.
     */
    @Override
    public List<BakedQuad> bakeQuads(List<IQuadTemplate> from) {
        return bakeQuads(from, null);
    }

    /**
     * Bakes all template quads in the list for a fixed rotation.
     *
     * @param from     A list containing template quads.
     * @param rotation The fixed quad rotation.
     * @return A new list with the baked quads.
     */
    @Override
    public List<BakedQuad> bakeQuads(List<IQuadTemplate> from, IForgeTransformationMatrix rotation) {
        ImmutableList.Builder<BakedQuad> builder = new ImmutableList.Builder<>();
        for (IQuadTemplate quadTemplate : from) {
            builder.add(bakeQuad(quadTemplate, rotation == null ? quadTemplate.getRotation().getRotation() : rotation));
        }
        return builder.build();
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing) {
        return bakeQuad(v1, v2, texture, facing, ModelRotation.X0_Y0.getRotation());
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation) {
        return bakeQuad(v1, v2, texture, facing, rotation, 0, 0, 16, 16);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float f1, float f2, float f3, float f4) {
        return bakeQuad(v1, v2, texture, facing, rotation, f1, f2, f3, f4, -1);
    }

    @Override
    public BakedQuad bakeQuad(IQuadTemplate template) {
        return bakeQuad(template, template.getRotation().getRotation());
    }

    @Override
    public BakedQuad bakeQuad(IQuadTemplate template, IForgeTransformationMatrix rotation) {
        return bakeQuad(template.getV1(), template.getV2(), template.getTexture(), template.getSide(), rotation, template.getUVData(), template.getTintIndex());
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, IQuadTemplate.IUVData uvData, int tint) {
        return bakeQuad(v1, v2, texture, facing, rotation, uvData.getUMin(), uvData.getVMin(), uvData.getUMax(), uvData.getVMax(), tint);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IForgeTransformationMatrix rotation, float f1, float f2, float f3, float f4, int tint) {
        return bakeQuad(v1, v2, texture, facing, new SimpleModelTransform(rotation.getTransformaion(), false), f1, f2, f3, f4, tint);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, Direction facing, IModelTransform rotation, float f1, float f2, float f3, float f4, int tint) {
        BlockFaceUV bfuv = new BlockFaceUV(new float[]{f1, f2, f3, f4}, 0);
        BlockPartFace bpf = new BlockPartFace(rotation.getPartTransformation(null).rotateTransform(facing), tint, "", bfuv);
        return faceBakery.bakeQuad(v1, v2, bpf, texture, facing, rotation, null, true, null);
        //return faceBakery.makeBakedQuad(v1, v2, bpf, texture, facing, rotation, null, true);
    }

    /**
     * Bakes the list of general quads for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return the list of baked quads for the given textures.
     */
    @Override
    public List<BakedQuad> getGeneralItemQuads(TextureAtlasSprite... textures) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        for (int i = 0; i < textures.length; i++) {
            builder.addAll(ItemLayerModel.getQuadsForSprite(i, textures[i], TransformationMatrix.identity()));
        }
        return builder.build();
    }

    @APIHandlerInject
    public void injectQuadBakery(IAPIHandler apiHandler) {
        apiHandler.inject(instance, IElecQuadBakery.class);
    }

    private static class SidedMap implements IQuadProvider {

        private SidedMap() {
            this(Maps.newEnumMap(Direction.class));
        }

        private SidedMap(EnumMap<Direction, List<BakedQuad>> quads) {
            this.quads = quads;
        }

        private final EnumMap<Direction, List<BakedQuad>> quads;

        public void setQuadsForSide(Direction side, List<BakedQuad> newQuads) {
            quads.put(side, newQuads);
        }

        @Override
        public List<BakedQuad> getBakedQuads(@Nullable BlockState state, Direction side, Random random) {
            List<BakedQuad> ret = quads.get(side);
            if (ret == null) {
                ret = EMPTY_LIST;
            }
            return ret;
        }

    }

    static {
        EMPTY_LIST = ImmutableList.of();
    }

}
