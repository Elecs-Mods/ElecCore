package elec332.core.util;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 7-12-2015.
 */
@Deprecated
public interface IBlockStateHelper<M extends Comparable<M>> {

    public static final IBlockStateHelper<EnumFacing> FACING_NORMAL = new IBlockStateHelper<EnumFacing>() {

        private final PropertyEnum<EnumFacing> TYPE = PropertyEnum.create("facing", EnumFacing.class);

        @Override
        public BlockStateContainer createMetaBlockState(Block block, IProperty... otherProperties) {
            return new BlockStateContainer(block, Lists.asList(TYPE, otherProperties).toArray(new IProperty[1]));
        }

        @Override
        public IBlockState setDefaultMetaState(Block block) {
            return block.getBlockState().getBaseState().withProperty(TYPE, EnumFacing.NORTH);
        }

        @Override
        public IBlockState getStateForMeta(Block block, int meta) {
            return block.getBlockState().getBaseState().withProperty(TYPE, EnumFacing.getFront(meta));
        }

        @Override
        public int getMetaForState(IBlockState state) {
            return state.getValue(TYPE).ordinal();
        }

        @Override
        public IProperty<EnumFacing> getProperty() {
            return TYPE;
        }

    };

    public BlockStateContainer createMetaBlockState(Block block, IProperty... otherProperties);

    public IBlockState setDefaultMetaState(Block block);

    public IBlockState getStateForMeta(Block block, int meta);

    public int getMetaForState(IBlockState state);

    public IProperty<M> getProperty();

}
