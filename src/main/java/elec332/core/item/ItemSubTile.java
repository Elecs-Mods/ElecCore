package elec332.core.item;

import com.google.common.base.Preconditions;
import elec332.core.block.BlockSubTile;
import elec332.core.tile.sub.TileMultiObject;
import elec332.core.util.math.RayTraceHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 20-2-2018
 */
public class ItemSubTile extends AbstractItemBlock {

    public ItemSubTile(BlockSubTile block, Properties builder) {
        super(block, builder);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean canBePlaced(@Nonnull BlockItemUseContext context, @Nonnull BlockState state) {
        return true;
    }

    public void afterBlockPlaced(@Nonnull BlockItemUseContext context, @Nonnull BlockState state, @Nonnull TileEntity tile) {
    }

    @Override
    protected boolean placeBlock(@Nonnull BlockItemUseContext context, @Nonnull BlockState state) {
        Preconditions.checkNotNull(context);
        boolean ret = canBePlaced(context, state) && super.placeBlock(context, state);
        if (ret) {
            TileEntity tile = WorldHelper.getTileAt(context.getWorld(), context.getPos());
            if (tile != null && !context.getWorld().isRemote) {
                afterBlockPlaced(context, state, tile);
            }
        }
        return ret;
    }

    public void onExistingObjectClicked(@Nonnull TileEntity tile, @Nonnull BlockRayTraceResult hit, PlayerEntity player, ItemStack stack, BlockState state) {
    }

    public void onEmptySolidSideClicked(@Nonnull World world, @Nonnull BlockPos clickedPos, @Nonnull TileEntity tile, @Nonnull Direction tileSide, PlayerEntity player, ItemStack stack, BlockState state) {
    }

    @SubscribeEvent //Using onRightClick doesn't work if there's a block directly above the object
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() != this) {
            return;
        }
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        PlayerEntity player = event.getPlayer();
        ItemStack stack = player.getHeldItem(event.getHand());
        if (WorldHelper.chunkLoaded(world, pos)) { //You never know...
            TileEntity tile = WorldHelper.getTileAt(world, pos);
            Direction face = event.getFace();
            BlockState state = WorldHelper.getBlockState(world, pos);
            if (state.getBlock() == getBlock() && tile instanceof TileMultiObject) { //attempt to add object
                event.setUseItem(Event.Result.DENY);
                event.setUseBlock(Event.Result.DENY);
                event.setCanceled(true);
                if (!world.isRemote) { //All logic on the server side
                    BlockRayTraceResult hit = RayTraceHelper.retraceBlock(state, world, pos, player);
                    if (hit != null) { //Can be null
                        onExistingObjectClicked(tile, hit, player, stack, state);
                    }
                }
                player.swingArm(event.getHand());
            } else if (face != null) { //attempt to place at face
                if (Block.hasSolidSide(state, world, pos, face)) {
                    tile = WorldHelper.getTileAt(world, pos.offset(face));
                    state = WorldHelper.getBlockState(world, pos.offset(face));
                    if (state.getBlock() == getBlock() && tile instanceof TileMultiObject) {
                        event.setUseItem(Event.Result.DENY);
                        event.setUseBlock(Event.Result.DENY);
                        event.setCanceled(true);
                        if (!world.isRemote) { //All logic on the server side
                            Direction rf = face.getOpposite();
                            onEmptySolidSideClicked(world, pos, tile, rf, player, stack, state);
                        }
                        player.swingArm(event.getHand());
                    }
                }
            }
        }

    }

}
