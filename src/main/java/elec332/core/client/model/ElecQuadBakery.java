package elec332.core.client.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import elec332.core.client.model.template.ElecTemplateBakery;
import elec332.core.client.model.template.IQuadTemplate;
import elec332.core.client.model.template.ITemplateSidedMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Elec332 on 15-11-2015.
 */
public class ElecQuadBakery {

    protected static final ElecQuadBakery instance = new ElecQuadBakery();
    private ElecQuadBakery(){
        this.faceBakery = new FaceBakery(); //Because MC is selfish and keeps his private...
        this.dummy = new ItemLayerModel((ImmutableList<ResourceLocation>)null); //Because I need a method that should be static...
    }

    private final FaceBakery faceBakery;
    private final ItemLayerModel dummy;
    private static final List<BakedQuad> EMPTY_LIST;

    /**
     * Creates a SidedMap, this cannot be rotated, you need to fill the map yourself.
     *
     * @return A new SidedMap
     */
    public ISidedMap newSidedMap(){
        return new SidedMap();
    }

    /**
     * Bakes all the template quads in the ITemplateSidedMap
     *
     * @param from The ITemplateSidedMap containing the template quads.
     * @return The ISidedMap with the baked quads.
     */
    public ISidedMap bakeQuads(ITemplateSidedMap from){
        return bakeQuads(from, null);
    }

    /**
     * Bakes all the template quads in the ITemplateMap for a fixed rotation
     *
     * @param from The ITemplateSidedMap containing the template quads.
     * @param rotation The fixed quad rotation.
     * @return The ISidedMap with the baked quads.
     */
    public ISidedMap bakeQuads(ITemplateSidedMap from, ModelRotation rotation){
        ISidedMap ret = newSidedMap();
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
    public List<BakedQuad> bakeQuads(List<IQuadTemplate> from, ModelRotation rotation){
        ImmutableList.Builder<BakedQuad> builder = new ImmutableList.Builder<BakedQuad>();
        for (IQuadTemplate quadTemplate : from){
            builder.add(bakeQuad(quadTemplate, rotation == null ? quadTemplate.getRotation() : rotation));
        }
        return builder.build();
    }

    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing){
        return bakeQuad(v1, v2, texture, facing, ModelRotation.X0_Y0);
    }

    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ModelRotation rotation){
        return bakeQuad(v1, v2, texture, facing, rotation, 0, 0, 16, 16);
    }

    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ModelRotation rotation, float f1, float f2, float f3, float f4){
        return bakeQuad(v1, v2, texture, facing, rotation, f1, f2, f3, f4, -1);
    }

    public BakedQuad bakeQuad(IQuadTemplate template){
        return bakeQuad(template, template.getRotation());
    }

    public BakedQuad bakeQuad(IQuadTemplate template, ModelRotation rotation){
        return bakeQuad(template.getV1(), template.getV2(), template.getTexture(), template.getSide(), rotation, template.getUVData(), template.getTintIndex());
    }

    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ModelRotation rotation, IQuadTemplate.IUVData uvData, int tint){
        return bakeQuad(v1, v2, texture, facing, rotation, uvData.getUMin(), uvData.getVMin(), uvData.getUMax(), uvData.getVMax(), tint);
    }

    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing, ModelRotation rotation, float f1, float f2, float f3, float f4, int tint){
        BlockFaceUV bfuv = new BlockFaceUV(new float[]{f1, f2, f3, f4}, 0);
        BlockPartFace bpf = new BlockPartFace(facing, tint, null, bfuv);
        return faceBakery.makeBakedQuad(v1, v2, bpf, texture, facing, rotation, null, true, true);
    }

    /**
     * Bakes the list of general quads for an item from the provided textures.
     * Multiple textures means multiple item layers.
     *
     * @param textures The layer textures.
     * @return the list of baked quads for the given textures.
     */
    public List<BakedQuad> getGeneralItemQuads(TextureAtlasSprite... textures){
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        for (int i = 0; i < textures.length; i++) {
            builder.addAll(dummy.getQuadsForSprite(i, textures[i], DefaultVertexFormats.ITEM, Optional.of(TRSRTransformation.identity())));
        }
        return builder.build();
    }

    /*
     * Event stuff
     */

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void bakeModels(ModelBakeEvent event){
        MinecraftForge.EVENT_BUS.post(new ReplaceJsonEvent(this, ElecModelBakery.instance, ElecTemplateBakery.instance));
    }

    private class SidedMap implements ISidedMap {

        private SidedMap(){
            this(Maps.<EnumFacing, List<BakedQuad>>newEnumMap(EnumFacing.class));
        }

        private SidedMap(EnumMap<EnumFacing, List<BakedQuad>> quads){
            this.quads = quads;
        }

        private final EnumMap<EnumFacing, List<BakedQuad>> quads;

        @Override
        public void setQuadsForSide(EnumFacing side, List<BakedQuad> newQuads){
            quads.put(side, newQuads);
        }

        @Override
        @Nonnull
        public List<BakedQuad> getForSide(EnumFacing side) {
            List<BakedQuad> ret = quads.get(side);
            if (ret == null){
                ret = EMPTY_LIST;
            }
            return ret;
        }

    }

    public interface ISidedMap {

        public void setQuadsForSide(EnumFacing side, List<BakedQuad> newQuads);

        @Nonnull
        public List<BakedQuad> getForSide(EnumFacing side);

    }

    static {
        EMPTY_LIST = ImmutableList.of();
    }

}
