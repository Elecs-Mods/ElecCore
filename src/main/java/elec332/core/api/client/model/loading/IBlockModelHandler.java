package elec332.core.api.client.model.loading;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;

/**
 * Created by Elec332 on 11-3-2016.
 * <p>
 * Model handler for blocks
 */
public interface IBlockModelHandler {

    /**
     * Whether this handler can handle this block
     *
     * @param block The block
     * @return Whether this handler can handle this block
     */
    public boolean handleBlock(Block block);

    /**
     * Notifies this {@link IBlockModelHandler} of the
     * {@link ModelResourceLocation} of the provided {@link IBlockState}
     *
     * @param state         The {@link IBlockState}
     * @param modelLocation The location of the provided {@link IBlockState}
     */
    default public void notifyModelLocation(IBlockState state, ModelResourceLocation modelLocation) {
    }

    /**
     * Used to create/fetch the model for this {@param block}
     *
     * @param state                The block state
     * @param identifier           The identifier of this block state
     * @param fullResourceLocation The full ModelResourceLocation for this model
     * @return The model for this {@param state}
     */
    public IBakedModel getModelFor(IBlockState state, String identifier, ModelResourceLocation fullResourceLocation);

}
