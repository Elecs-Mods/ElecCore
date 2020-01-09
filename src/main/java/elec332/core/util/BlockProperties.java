package elec332.core.util;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

/**
 * Created by Elec332 on 7-12-2015.
 */
public final class BlockProperties {

    public static final EnumProperty<Direction> FACING_NORMAL = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

}
