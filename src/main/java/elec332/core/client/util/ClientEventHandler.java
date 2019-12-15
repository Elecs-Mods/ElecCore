package elec332.core.client.util;

import elec332.core.block.ISelectionBoxOverride;
import elec332.core.client.RenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
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
        RayTraceResult hit = event.getTarget();
        World world = Minecraft.getInstance().world;
        if (event.getSubID() == 0 && hit.type == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (!state.isAir(world, pos) && state.getBlock() instanceof ISelectionBoxOverride) {
                VoxelShape shape = ((ISelectionBoxOverride) state.getBlock()).getSelectionBox(state, world, pos, event.getPlayer(), event.getTarget());
                if (shape != null) {
                    RenderHelper.drawSelectionBox(event.getPlayer(), world, pos, shape, event.getPartialTicks());
                    event.setCanceled(true);
                }
            }
        }
    }

}
