package elec332.core.api.registration;

import net.minecraft.block.Block;

/**
 * Created by Elec332 on 12-10-2017.
 */
public interface IBlockRegister extends IObjectRegister<Block> {

    @Override
    default public Class<Block> getType(){
        return Block.class;
    }

}
