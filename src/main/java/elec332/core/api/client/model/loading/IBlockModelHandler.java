package elec332.core.api.client.model.loading;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

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
     * Used to create an identifier for this IBlockState
     * (This will also be the variant in the ModelResourceLocation)
     *
     * @param state The block state
     * @return The identifier for this IBlockState
     */
    public String getIdentifier(IBlockState state);

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
