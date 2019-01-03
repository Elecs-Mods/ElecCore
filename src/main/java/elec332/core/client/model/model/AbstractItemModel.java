package elec332.core.client.model.model;

import com.google.common.collect.ImmutableList;
import elec332.core.client.model.ElecModelBakery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 21-11-2015.
 */
@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractItemModel implements IBakedModel {

    protected static final ItemCameraTransforms DEFAULT_ITEM_TRANSFORM;
    protected static final ImmutableList<BakedQuad> EMPTY_LIST;

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand) {
        return side == null ? getGeneralQuads() : EMPTY_LIST;
    }

    public abstract List<BakedQuad> getGeneralQuads();

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public final boolean isBuiltInRenderer() {
        return isItemTESR();
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    public boolean isItemTESR() {
        return false;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureMap().getAtlasSprite(getTextureLocation().toString());
    }

    public abstract ResourceLocation getTextureLocation();

    @Override
    @Nonnull
    public ItemCameraTransforms getItemCameraTransforms() {
        return DEFAULT_ITEM_TRANSFORM;
    }

    static {
        DEFAULT_ITEM_TRANSFORM = ElecModelBakery.DEFAULT_ITEM;
        EMPTY_LIST = ImmutableList.of();
    }

    protected static Vector3f applyTranslationScale(Vector3f vec) {
        vec.mul(0.0625F);
        return vec;
    }

}
