package elec332.core.client.model.loading.handler;

import elec332.core.api.client.model.loading.IBlockModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.model.loading.INoJsonBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 12-3-2016.
 */
@ModelHandler
@OnlyIn(Dist.CLIENT)
public class INoJsonBlockHandler implements IBlockModelHandler {

    @Override
    public boolean handleBlock(Block block) {
        return block instanceof INoJsonBlock;
    }

    @Override
    public IBakedModel getModelFor(BlockState state, String identifier, ModelResourceLocation fullResourceLocation) {
        return ((INoJsonBlock) state.getBlock()).getBlockModel(state);
    }

}
