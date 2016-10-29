package elec332.core.api.client.model.loading;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

/**
 * Created by Elec332 on 11-3-2016.
 */
public interface IBlockModelHandler {

    public boolean handleBlock(Block block);

    public String getIdentifier(IBlockState state);

    public IBakedModel getModelFor(IBlockState block, String identifier, ModelResourceLocation fullResourceLocation);

}
