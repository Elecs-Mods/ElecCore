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
    public static final EnumProperty<Direction> FACING_ALL = FACING_NORMAL;
    public static final EnumProperty<Direction> FACING_HORIZONTAL = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty ON = BooleanProperty.create("on");

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

}
