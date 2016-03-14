package elec332.core.client.newstuff;

import elec332.core.client.model.INoJsonBlock;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

/**
 * Created by Elec332 on 12-3-2016.
 */
@ModelHandler
public class INoJsonBlockHandler implements IBlockModelHandler {

    @Override
    public boolean handleBlock(Block block) {
        return block instanceof INoJsonBlock;
    }

    @Override
    public String getIdentifier(IBlockState state) {
        return ""+WorldHelper.getBlockMeta(state);
    }

    @Override
    public IBakedModel getModelFor(IBlockState state, String identifier, ModelResourceLocation fullResourceLocation) {
        return ((INoJsonBlock)state.getBlock()).getBlockModel(state);
    }

}
