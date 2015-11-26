package elec332.core.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.*;
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

import javax.vecmath.Vector3f;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Elec332 on 15-11-2015.
 */
public class ElecQuadBakery {

    protected static final ElecQuadBakery instance = new ElecQuadBakery();
    private ElecQuadBakery(){
        this.faceBakery = new FaceBakery(); //Because MC is selfish and keeps his private...
        dummy = new ItemLayerModel((ImmutableList<ResourceLocation>)null); //Because I need a method that should be static...
    }

    private final FaceBakery faceBakery;
    private final ItemLayerModel dummy;

    public EnumMap<EnumFacing, List<BakedQuad>> bakeDefaultBlock(TextureAtlasSprite texture){
        EnumMap<EnumFacing, List<BakedQuad>> ret = Maps.newEnumMap(EnumFacing.class);
        for (EnumFacing facing : EnumFacing.VALUES){
            List<BakedQuad> atm = Lists.newArrayList();
            atm.add(bakeQuad(new Vector3f(0, 0, 0), new Vector3f(16, 16, 16), texture, facing));
            ret.put(facing, atm);
        }
        return ret;
    }

    public BakedQuad bakeQuad(Vector3f v1, Vector3f v2, TextureAtlasSprite texture, EnumFacing facing){
        BlockFaceUV bfuv = new BlockFaceUV(new float[]{0,0,16,16}, 0); //What's this?
        BlockPartFace bpf = new BlockPartFace(facing, -1, null, bfuv);
        return faceBakery.makeBakedQuad(v1, v2, bpf, texture, facing, ModelRotation.X0_Y0, null, true, true);
    }

    public List<BakedQuad> getGeneralItemQuads(TextureAtlasSprite... textures){
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        for (int i = 0; i < textures.length; i++) {
            builder.addAll(dummy.getQuadsForSprite(i, textures[i], DefaultVertexFormats.ITEM, TRSRTransformation.identity()));
        }
        return builder.build();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void bakeModels(ModelBakeEvent event){
        MinecraftForge.EVENT_BUS.post(new ReplaceJsonEvent(this, RenderingRegistry.instance()));
    }

}
