package elec332.core.client.util;

import elec332.core.ElecCore;
import elec332.core.block.ISelectionBoxOverride;
import elec332.core.client.RenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Created by Elec332 on 1-2-2019
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        RayTraceResult hit_ = event.getTarget();
        World world = Minecraft.getInstance().world;
        if (event.getSubID() == 0 && hit_ instanceof BlockRayTraceResult) {
            BlockRayTraceResult hit = (BlockRayTraceResult) hit_;
            BlockPos pos = hit.getPos();
            BlockState state = world.getBlockState(pos);
            if (!state.isAir(world, pos) && state.getBlock() instanceof ISelectionBoxOverride) {
                VoxelShape shape = ((ISelectionBoxOverride) state.getBlock()).getSelectionBox(state, world, pos, ElecCore.proxy.getClientPlayer(), event.getTarget());
                if (shape != null) {
                    RenderHelper.drawSelectionBox(world, pos, shape, event.getInfo().getProjectedView());
                    event.setCanceled(true);
                }
            }
        }
    }

}
