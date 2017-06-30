package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.model.IQuadProvider;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import elec332.core.client.model.template.ElecTemplateBakery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by Elec332 on 15-11-2015.
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings({"WeakerAccess", "unused"})
public class ElecQuadBakery implements IElecQuadBakery {

    protected static final ElecQuadBakery instance = new ElecQuadBakery();
    private ElecQuadBakery(){
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
    public IQuadProvider bakeQuads(IQuadTemplateSidedMap from){
        return bakeQuads(from, null);
    }

    /**
     * Bakes all the template quads in the ITemplateMap for a fixed rotation
     *
     * @param from The IQuadTemplateSidedMap containing the template quads.
     * @param rotation The fixed quad rotation.
     * @return The ISidedMap with the baked quads.
     */
    @Override
    public IQuadProvider bakeQuads(IQuadTemplateSidedMap from, ITransformation rotation){
        SidedMap ret = new SidedMap();
        for (EnumFacing facing : EnumFacing.VALUES){
            ret.setQuadsForSide(rotation == null ? facing : rotation.rotate(facing), bakeQuads(from.getForSide(facing), rotation));
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
    public List<BakedQuad> bakeQuads(List<IQuadTemplate> from){
        return bakeQuads(from, null);
    }

    /**
     * Bakes all template quads in the list for a fixed rotation.
     *
     * @param from A list containing template quads.
     * @param rotation The fixed quad rotation.
     * @return A new list with the baked quads.
     */
    @Override
    public List<BakedQuad> bakeQuads(List<IQuadTemplate> from, ITransformation rotation){
        ImmutableList.Builder<BakedQuad> builder = new ImmutableList.Builder<BakedQuad>();
        for (IQuadTemplate quadTemplate : from){
            builder.add(bakeQuad(quadTemplate, rotation == null ? quadTemplate.getRotation() : rotation));
        }
        return builder.build();
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing){
        return bakeQuad(v1, v2, texture, facing, ModelRotation.X0_Y0);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ITransformation rotation){
        return bakeQuad(v1, v2, texture, facing, rotation, 0, 0, 16, 16);
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ITransformation rotation, float f1, float f2, float f3, float f4){
        return bakeQuad(v1, v2, texture, facing, rotation, f1, f2, f3, f4, -1);
    }

    @Override
    public BakedQuad bakeQuad(IQuadTemplate template){
        return bakeQuad(template, template.getRotation());
    }

    @Override
    public BakedQuad bakeQuad(IQuadTemplate template, ITransformation rotation){
        return bakeQuad(template.getV1(), template.getV2(), template.getTexture(), template.getSide(), rotation, template.getUVData(), template.getTintIndex());
    }

    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ITransformation rotation, IQuadTemplate.IUVData uvData, int tint){
        return bakeQuad(v1, v2, texture, facing, rotation, uvData.getUMin(), uvData.getVMin(), uvData.getUMax(), uvData.getVMax(), tint);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ITransformation rotation, float f1, float f2, float f3, float f4, int tint){
        BlockFaceUV bfuv = new BlockFaceUV(new float[]{f1, f2, f3, f4}, ((ModelRotation)rotation).quartersX * 90);
        BlockPartFace bpf = new BlockPartFace(rotation.rotate(facing), tint, null, bfuv);
        return faceBakery.makeBakedQuad(v1, v2, bpf, texture, facing, rotation, null, false, true);
    }

    /**
     * Bakes the list of general quads for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return the list of baked quads for the given textures.
     */
    @Override
    public List<BakedQuad> getGeneralItemQuads(TextureAtlasSprite... textures){
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        for (int i = 0; i < textures.length; i++) {
            builder.addAll(ItemLayerModel.getQuadsForSprite(i, textures[i], DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
        }
        return builder.build();
    }

    /*
     * Event stuff
     */

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void bakeModels(ModelBakeEvent event){
        MinecraftForge.EVENT_BUS.post(new ModelLoadEventImpl(this, ElecModelBakery.instance, ElecTemplateBakery.instance, event.getModelRegistry()));
    }

    private class SidedMap implements IQuadProvider {

        private SidedMap(){
            this(Maps.newEnumMap(EnumFacing.class));
        }

        private SidedMap(EnumMap<EnumFacing, List<BakedQuad>> quads){
            this.quads = quads;
        }

        private final EnumMap<EnumFacing, List<BakedQuad>> quads;

        public void setQuadsForSide(EnumFacing side, List<BakedQuad> newQuads){
            quads.put(side, newQuads);
        }

        @Override
        public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, EnumFacing side, long random) {
            List<BakedQuad> ret = quads.get(side);
            if (ret == null){
                ret = EMPTY_LIST;
            }
            return ret;
        }

    }

    static {
        EMPTY_LIST = ImmutableList.of();
    }

}
