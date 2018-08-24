package elec332.core.client.model.loading;

import elec332.core.api.client.model.IModelAndTextureLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    @SideOnly(Side.CLIENT)
    public IBakedModel getBlockModel(IBlockState state);

}
