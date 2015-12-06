package elec332.core.client.model;

import elec332.core.client.model.model.IBlockModel;
import elec332.core.client.model.model.IModelAndTextureLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 15-11-2015.
 */
public interface INoJsonBlock extends IModelAndTextureLoader {

    /**
     * This method is used when a model is requested to render the block in a world.
     *
     * @param state The current BlockState.
     * @param iba The IBlockAccess the block is in.
     * @param pos The position of the block.
     * @return The model to render for this block for the given arguments.
     */
    @SideOnly(Side.CLIENT)
    public IBlockModel getBlockModel(IBlockState state, IBlockAccess iba, BlockPos pos);

    /**
     * This method is used when a model is requested when its not placed, so for an item.
     *
     * @return The model to render when the block is not placed.
     */
    @SideOnly(Side.CLIENT)
    public IBakedModel getBlockModel(Item item, int meta);

}
