package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.DefaultInstances;
import elec332.core.abstraction.IItemBlock;
import elec332.core.abstraction.abstracted.CopyMarker;
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

/**
 * Created by Elec332 on 24-12-2016.
 */
public class AbstractedItemBlock extends ItemBlock implements IAbstractedItem<IItemBlock> {

    public AbstractedItemBlock(Block block) {
        super(block);
        this.defaultBlockPlaceBehaviour = DefaultInstances.getDefaultBlockPlacementBehaviour(getLinkedItem_INTERNAL_ELEC());
    }

    @CopyMarker
    private final IItemBlock.IDefaultBlockPlaceBehaviour defaultBlockPlaceBehaviour;

    @SideOnly(Side.CLIENT)
    @Override @CopyMarker
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }

    @Override @CopyMarker
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        return getLinkedItem_INTERNAL_ELEC().placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState, defaultBlockPlaceBehaviour);
    }

    @Override
    public IItemBlock getLinkedItem_INTERNAL_ELEC() {
        return null;
    }
}
