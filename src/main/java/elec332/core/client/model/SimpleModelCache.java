package elec332.core.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 28-7-2020
 */
public abstract class SimpleModelCache<K> extends ModelCache<K> {

    public SimpleModelCache(ResourceLocation particle) {
        this.particle = particle;
        property = new ModelProperty<>();
    }

    private final ModelProperty<K> property;
    private final ResourceLocation particle;

    @Override
    protected final K get(BlockState state, IModelData modelState) {
        return modelState.getData(property);
    }

    @Override
    protected abstract K get(ItemStack stack);

    @Override
    public final void addModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData modelData) {
        modelData.setData(property, get(world, pos, state));
    }

    protected abstract K get(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state);

    @Override
    protected abstract void bakeQuads(List<BakedQuad> quads, Direction side, K key);

    @Nonnull
    @Override
    protected ResourceLocation getTextureLocation() {
        return particle;
    }

}
