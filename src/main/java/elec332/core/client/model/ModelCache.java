package elec332.core.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Elec332 on 20-1-2018.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ModelCache<K> implements IBakedModel {

    protected ModelCache() {
        quads = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.MINUTES).build();
        itemModels = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.MINUTES).build();
        iol = new ItemOverrideList() {

            @Override
            @Nonnull
            public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return getModel(stack);
            }

        };
        debug = false;
    }

    private final Cache<K, Map<EnumFacing, List<BakedQuad>>> quads;
    private final Cache<K, IBakedModel> itemModels;
    private final ItemOverrideList iol;
    protected boolean debug;

    protected abstract K get(IBlockState state);

    protected abstract K get(ItemStack stack);

    protected abstract void bakeQuads(List<BakedQuad> quads, EnumFacing side, K key);

    private Map<EnumFacing, List<BakedQuad>> getQuads(K key) {
        Callable<Map<EnumFacing, List<BakedQuad>>> loader = () -> {
            Map<EnumFacing, List<BakedQuad>> ret = Maps.newHashMap();
            for (EnumFacing f : EnumFacing.values()) {
                List<BakedQuad> q = Lists.newArrayList();
                bakeQuads(q, f, key);
                ret.put(f, ImmutableList.copyOf(q));
            }
            List<BakedQuad> q = Lists.newArrayList();
            bakeQuads(q, null, key);
            ret.put(null, ImmutableList.copyOf(q));
            return ret;
        };
        try {
            return debug ? loader.call() : quads.get(key, loader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final IBakedModel getModel(ItemStack stack) {
        K key = get(stack);
        Callable<IBakedModel> loader = () -> new WrappedModel(ModelCache.this) {

            Map<EnumFacing, List<BakedQuad>> quads = ModelCache.this.getQuads(key);

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, @Nonnull Random rand) {
                return quads.get(side);
            }

        };
        try {
            return debug ? loader.call() : itemModels.get(key, loader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final IBakedModel getModel(K key) {
        Callable<IBakedModel> loader = () -> new WrappedModel(ModelCache.this) {

            Map<EnumFacing, List<BakedQuad>> quads = ModelCache.this.getQuads(key);

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, @Nonnull Random rand) {
                return quads.get(side);
            }

        };
        try {
            return debug ? loader.call() : itemModels.get(key, loader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Nonnull
    public final List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, @Nonnull Random rand) {
        return getQuads(get(state)).get(side);
    }

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
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureMap().getAtlasSprite(getTextureLocation().toString());
    }

    @Nonnull
    protected abstract ResourceLocation getTextureLocation();

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return ElecModelBakery.DEFAULT_BLOCK;
    }

    @Override
    @Nonnull
    public final ItemOverrideList getOverrides() {
        return iol;
    }

}
