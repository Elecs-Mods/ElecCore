package elec332.core.client.model;

import elec332.core.client.model.model.IBlockModelWithoutQuads;
import elec332.core.client.model.model.IItemModel;
import elec332.core.client.model.model.IModelAndTextureLoader;
import elec332.core.client.model.model.IQuadProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 15-11-2015.
 */
public interface INoJsonBlock extends INoJsonItem {

    /**
     * This method is used when a model is requested for every valid BlockState,
     * during the initialisation of the ModelRegistry.
     *
     * @param state The current BlockState, can NOT be an ExtendedBlockState.
     * @return The model to render for this block for the given arguments.
     */
    @SideOnly(Side.CLIENT)
    //public IBlockModelWithoutQuads getBlockModel(IBlockState state);
    public IBakedModel getBlockModel(IBlockState state);

    /**
     * This method is used when a model is requested for every valid BlockState,
     * during the initialisation of the ModelRegistry.
     *
     * @param state The current BlockState, can NOT be an ExtendedBlockState.
     * @return The QuadProvider.
     */
    //public IQuadProvider getQuadProvider(IBlockState state);

    /**
     * This method is used when a model is requested when its not placed, so for an item.
     *
     * @return The model to render when the block is not placed.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity);

}
