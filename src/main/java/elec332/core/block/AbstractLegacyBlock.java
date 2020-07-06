package elec332.core.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 6-7-2020
 */
public class AbstractLegacyBlock extends AbstractBlock {

    public AbstractLegacyBlock(Properties builder) {
        super(builder);
    }

    @Override
    public final ActionResultType onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return onBlockActivatedLegacy(world, pos, state, player, hand, hit) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    public boolean onBlockActivatedLegacy(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return super.onBlockActivated(world, pos, state, player, hand, hit) == ActionResultType.SUCCESS;
    }

}
