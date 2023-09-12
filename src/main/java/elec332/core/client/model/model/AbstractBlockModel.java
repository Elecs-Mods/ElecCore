package elec332.core.client.model.model;

import com.google.common.collect.ImmutableList;
import elec332.core.api.client.model.model.IModelWithoutQuads;
import elec332.core.client.model.ElecModelBakery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 15-11-2015.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"deprecation", "unused"})
public abstract class AbstractBlockModel implements IModelWithoutQuads {

    private static final ImmutableList<BakedQuad> EMPTY_LIST;

    @Override
    public boolean isAmbientOcclusion() {
        return true;
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
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureMap().getAtlasSprite(getTextureLocation().toString());
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
