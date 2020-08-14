package elec332.core.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;

/**
 * Created by Elec332 on 9-2-2019
 */
public interface IActivatableTile {

    default ActionResultType onBlockActivated(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return ActionResultType.PASS;
    }

}
