package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.IItemBlock;
import elec332.core.api.annotations.CopyMarker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 24-12-2016.
 */
public abstract class AbstractedItemBlock extends ItemBlock implements IAbstractedItem<IItemBlock> {

    public AbstractedItemBlock(Block block) {
        super(block);
        this.defaultBlockPlaceBehaviour = getDefaultBlockPlacementBehaviour(getLinkedItem_INTERNAL_ELEC());
    }

    @CopyMarker
    private final IItemBlock.IDefaultBlockPlaceBehaviour defaultBlockPlaceBehaviour;

    @SideOnly(Side.CLIENT)
    @Override @CopyMarker
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return getLinkedItem_INTERNAL_ELEC().canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }

    @Override @CopyMarker
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        return getLinkedItem_INTERNAL_ELEC().placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState, defaultBlockPlaceBehaviour);
    }

    public static IItemBlock.IDefaultBlockPlaceBehaviour getDefaultBlockPlacementBehaviour(IItemBlock owner){
        return new MC_Link(owner).defaultBlockPlaceBehaviour;
    }

    private static class MC_Link extends ItemBlock {

        @SuppressWarnings("all")
        private MC_Link(IItemBlock owner) {
            super(owner.getBlock());
            this.owner = owner;
            this.defaultBlockPlaceBehaviour = new IItemBlock.IDefaultBlockPlaceBehaviour(){

                @Override
                public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
                    return MC_Link.super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
                }

            };
        }

        private final IItemBlock owner;
        private final IItemBlock.IDefaultBlockPlaceBehaviour defaultBlockPlaceBehaviour;

        @Override
        public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {
            return owner.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState, defaultBlockPlaceBehaviour);
        }

    }

}
