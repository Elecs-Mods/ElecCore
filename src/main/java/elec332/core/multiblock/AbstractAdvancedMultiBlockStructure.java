package elec332.core.multiblock;

import elec332.core.util.BlockLoc;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 2-9-2015.
 */
public abstract class AbstractAdvancedMultiBlockStructure implements IMultiBlockStructure {

    @Override
    public BlockStructure.IStructureFiller replaceUponCreated() {
        return null;
    }

    /**
     * @param player The player trying to create the multiblock
     *
     * @return if said player is allowed to create the multiblock
     */
    public abstract boolean canCreate(EntityPlayerMP player);

    /**
     * This gets called after the structure is complete and valid, but if this method returns false,
     * the registry will assume the multiblock was invalid and not create it.
     *
     * @param world The world
     * @param bottomLeft The coordinate of the front-bottom-left position
     * @param facing The facing of the multiblock
     *
     * @return If all secondary conditions are met.
     */
    public abstract boolean areSecondaryConditionsMet(World world, BlockLoc bottomLeft, ForgeDirection facing);

    protected BlockLoc getTranslatedPosition(BlockLoc bottomLeft, ForgeDirection facing, int length, int width, int height){
        return MultiBlockStructureRegistry.getTranslated(bottomLeft.xCoord, bottomLeft.yCoord, bottomLeft.zCoord, facing, length, width, height);
    }
}
