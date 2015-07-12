package elec332.core.baseclasses.block;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.material.Material;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class BaseRock extends BaseBlock {

    public BaseRock(String blockName, FMLPreInitializationEvent event){
        super(Material.rock, blockName, event);
    }
}
