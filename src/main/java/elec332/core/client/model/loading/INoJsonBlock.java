package elec332.core.client.model.loading;

import elec332.core.api.client.model.IModelAndTextureLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 15-11-2015.
 */
public interface INoJsonBlock extends IModelAndTextureLoader {

    /**
     * This method is used when a model is requested for every valid BlockState,
     * during the initialisation of the ModelRegistry.
     *
     * @param state The current BlockState, can NOT be an ExtendedBlockState.
     * @return The model to render for this block for the given arguments.
     */
    @OnlyIn(Dist.CLIENT)
    public IBakedModel getBlockModel(IBlockState state);

}
