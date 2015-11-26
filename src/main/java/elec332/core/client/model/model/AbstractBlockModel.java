package elec332.core.client.model.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartBlockModel;

import java.util.List;

/**
 * Created by Elec332 on 15-11-2015.
 */
@SuppressWarnings("deprecation")
public abstract class AbstractBlockModel implements IBlockModel {

    private static final ImmutableList<BakedQuad> EMPTY_LIST;

    @Override
    public ISmartBlockModel handleBlockState(IBlockState state) {
        return this;
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        return EMPTY_LIST;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getTextureLocation().toString());
    }

    public abstract ResourceLocation getTextureLocation();

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    static {
        EMPTY_LIST = ImmutableList.of();
    }

}
