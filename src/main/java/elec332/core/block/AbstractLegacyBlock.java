package elec332.core.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.core.BlockPos;
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
    public final boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return onBlockActivatedLegacy(world, pos, state, player, hand, hit);
    }

    public boolean onBlockActivatedLegacy(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return super.onBlockActivated(world, pos, state, player, hand, hit);
    }

}
