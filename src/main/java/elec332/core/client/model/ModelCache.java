package elec332.core.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

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
            public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                return getModel(stack);
            }

        };
        debug = false;
    }

    private final Cache<K, Map<Direction, List<BakedQuad>>> quads;
    private final Cache<K, IBakedModel> itemModels;
    private final ItemOverrideList iol;
    protected boolean debug;

    protected abstract K get(BlockState state, IModelData modelState);

    protected abstract K get(ItemStack stack);

    protected abstract void bakeQuads(List<BakedQuad> quads, Direction side, K key);

    private Map<Direction, List<BakedQuad>> getQuads(K key) {
        Callable<Map<Direction, List<BakedQuad>>> loader = () -> {
            Map<Direction, List<BakedQuad>> ret = Maps.newHashMap();
            for (Direction f : Direction.values()) {
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

            Map<Direction, List<BakedQuad>> quads = ModelCache.this.getQuads(key);

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand) {
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

            Map<Direction, List<BakedQuad>> quads = ModelCache.this.getQuads(key);

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand) {
                return quads.get(side);
            }

        };
        try {
            return debug ? loader.call() : itemModels.get(key, loader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public final List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return getQuads(get(state, extraData)).get(side);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        if (needsModelData()){
            throw new UnsupportedOperationException();
        }
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    protected boolean needsModelData(){
        return false;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        if (tileData == EmptyModelData.INSTANCE){
            tileData = new ModelDataMap.Builder().build();
        }
        addModelData(world, pos, state, tileData);
        return tileData;
    }

    public void addModelData(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData modelData) {

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
