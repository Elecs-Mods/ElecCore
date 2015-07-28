package elec332.core.multiblock;

import elec332.core.baseclasses.tileentity.TileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 28-7-2015.
 */
public class AbstractMultiBlockTile extends TileBase implements IMultiBlockTile {

    public AbstractMultiBlockTile(){
        super();
        this.multiBlockData = new MultiBlockData(this);
    }

    private MultiBlockData multiBlockData;

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return getMultiBlock() instanceof AbstractMultiBlock ?  ((AbstractMultiBlock) getMultiBlock()).onAnyBlockActivated(player): super.onBlockActivated(player, side, hitX, hitY, hitZ);
    }

    /**
     * An IMultiblock cannot be saved to NBT, every time this tile gets loaded after the tile unloaded, this gets re-assigned
     *
     * @param multiBlock The multiblock in which the tile belongs
     * @param facing     The facing of the multiblock -Save this value to NBT!
     * @param structure  The identifier of the multiblock-structure -Save this value to NBT aswell!
     */
    @Override
    public void setMultiBlock(IMultiBlock multiBlock, ForgeDirection facing, String structure) {
        multiBlockData.setMultiBlock(multiBlock, facing, structure);
    }

    /**
     * When an multiblock becomes invalid, this method will get called, use it
     * to set the multiblock to null, and make sure that #isValidMultiBlock() returns false after this method finished processing!
     */
    @Override
    public void invalidateMultiBlock() {
        multiBlockData.isValidMultiBlock();
    }

    /**
     * This returns if this tile is actually part of an multiblock or not
     *
     * @return weather the multiblock is valid, make sure to read/write the value from/to NBT!
     */
    @Override
    public boolean isValidMultiBlock() {
        return multiBlockData.isValidMultiBlock();
    }

    /**
     * This is used for saving, just like TileEntities, make sure this returns the name from the @link IMultiBlockStructure
     * HINT: Save this value to NBT aswell;
     *
     * @return the name of the structure
     */
    @Override
    public String getStructureIdentifier() {
        return multiBlockData.getStructureIdentifier();
    }

    /**
     * This value should return the same value as the value in #setMultiBlock
     *
     * @return The facing of the multiblock
     */
    @Override
    public ForgeDirection getFacing() {
        return multiBlockData.getFacing();
    }

    /**
     * Returns the multiblock this tile belongs too, can be null
     *
     * @return said multiblock
     */
    @Override
    public IMultiBlock getMultiBlock() {
        return multiBlockData.getMultiBlock();
    }
}
