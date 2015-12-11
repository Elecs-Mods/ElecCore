package elec332.core.util;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;

import java.util.Map;

/**
 * Created by Elec332 on 7-12-2015.
 */
public class BlockStateHelper {

    public static final PropertyEnum<EnumMeta> TYPE = PropertyEnum.create("meta", EnumMeta.class);

    public static BlockState createBlockState(Block block){
        return new BlockState(block, TYPE);
    }

    public static IBlockState setDefaultMetaState(Block block){
        return block.getBlockState().getBaseState().withProperty(TYPE, EnumMeta.META_0);
    }

    public static IBlockState getStateForMeta(Block block, int meta){
        return block.getDefaultState().withProperty(TYPE, EnumMeta.forMeta(meta));
    }

    public static int getMetaForState(IBlockState state){
        return EnumMeta.forState(state.getValue(TYPE));
    }

    public enum EnumMeta implements IStringSerializable {

        META_0(0),
        META_1(1),
        META_2(2),
        META_3(3),
        META_4(4),
        META_5(5),
        META_6(6),
        META_7(7),
        META_8(8),
        META_9(9),
        META_10(10),
        META_11(11),
        META_12(12),
        META_13(13),
        META_14(14),
        META_15(15),
        META_16(16)
        ;

        private EnumMeta(int meta){
            this.meta = meta;
        }

        private final int meta;

        public int getMeta(){
            return this.meta;
        }

        @Override
        public String getName() {
            return toString();
        }

        private static final Map<Integer, EnumMeta> metaStates;

        public static EnumMeta forMeta(int meta){
            if (meta > 16)
                throw new IllegalArgumentException();
            return metaStates.get(meta);
        }

        public static int forState(EnumMeta enumMeta){
            if (enumMeta == null)
                throw new IllegalArgumentException();
            return enumMeta.getMeta();
        }

        static {
            metaStates = Maps.newHashMap();
            for (EnumMeta enumMeta : values()){
                metaStates.put(enumMeta.getMeta(), enumMeta);
            }
        }

    }

}
