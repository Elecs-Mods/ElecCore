package elec332.core.abstraction.builder.impl;

import elec332.core.abstraction.builder.IBlockStateSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-3-2018.
 */
public abstract class BlockBuilderBlock extends Block {

    public BlockBuilderBlock(IBlockStateSerializer stateSerializer) {
        super(Material.ANVIL);
        this.stateSerializer = stateSerializer;
    }

    private final IBlockStateSerializer stateSerializer;

    @Override
    @Nonnull
    public abstract BlockStateContainer createBlockState();

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return stateSerializer.getState(meta, this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return stateSerializer.getMeta(state);
    }

}
