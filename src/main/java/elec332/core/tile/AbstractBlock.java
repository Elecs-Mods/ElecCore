package elec332.core.tile;

import elec332.core.mcabstractionlayer.impl.MCAbstractedBlock;
import elec332.core.mcabstractionlayer.object.IAbstractedBlock;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends MCAbstractedBlock implements IAbstractedBlock {

    public AbstractBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    public AbstractBlock(Material materialIn) {
        super(materialIn);
    }

}
