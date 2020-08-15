package elec332.core.inventory;

import com.google.common.collect.Lists;
import elec332.core.util.ItemStackHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * Created by Elec332 on 14-8-2020
 */
public class ItemEjector {

    private Direction side;
    private BlockPos pos;
    private LazyOptional<IItemHandler> inventory;

    public void eject(World world, BlockPos pos, Iterable<Direction> sidez, ItemStack stack) {
        if (WorldHelper.isClient(world) || !ItemStackHelper.isStackValid(stack)) {
            return;
        }
        List<Direction> sides = Lists.newArrayList(sidez);
        if (sides.contains(side)) {
            BlockPos p = pos.offset(side);
            if (p.equals(this.pos)) {
                stack = eject(world, p, side, stack);
                sides.remove(side);
            }
        }
        if (stack.isEmpty()) {
            return;
        }
        for (Direction dir : sides) {
            stack = eject(world, pos.offset(dir), dir, stack);
            if (stack.isEmpty()) {
                return;
            }
        }
        WorldHelper.dropStack(world, pos, stack);
    }

    private ItemStack eject(World world, BlockPos pos, Direction side, ItemStack stack) {
        LazyOptional<IItemHandler> inventory = LazyOptional.empty();
        if (pos.equals(this.pos)) {
            inventory = this.inventory;
        }
        if (!inventory.isPresent()) {
            TileEntity tile = WorldHelper.getTileAt(world, pos);
            if (tile != null) {
                inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
            }
        }
        ItemStack ret = inventory.map(inv -> ItemHandlerHelper.insertItemStacked(inv, stack, false)).orElse(stack);
        if (ret.isEmpty()) {
            this.side = side;
            this.pos = pos;
            this.inventory = inventory;
        }
        return ret;
    }

}
