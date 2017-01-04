package elec332.core.client.model.model;

import com.google.common.collect.ImmutableList;
import elec332.core.api.client.model.model.IModelWithoutQuads;
import elec332.core.client.model.ElecModelBakery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 15-11-2015.
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings({"deprecation", "unused"})
public abstract class AbstractBlockModel implements IModelWithoutQuads {

    protected static final ImmutableList<BakedQuad> EMPTY_LIST;


    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isTESRItem() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getTextureLocation().toString());
    }

    public abstract ResourceLocation getTextureLocation();

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ElecModelBakery.DEFAULT_BLOCK;
    }

    static {
        EMPTY_LIST = ImmutableList.of();
    }

}
