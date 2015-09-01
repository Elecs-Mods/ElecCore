package elec332.core.multiblock;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 27-7-2015.
 */
public interface IMultiBlockTile {

    /**
     * An IMultiblock cannot be saved to NBT, every time this tile gets loaded after the tile unloaded, this gets re-assigned
     *
     * @param multiBlock The multiblock in which the tile belongs
     * @param facing The facing of the multiblock -Save this value to NBT!
     * @param structure The identifier of the multiblock-structure -Save this value to NBT aswell!
     */
    public void setMultiBlock(IMultiBlock multiBlock, ForgeDirection facing, String structure);

    /**
     * When an multiblock becomes invalid, this method will get called, use it
     * to set the multiblock to null, and make sure that #isValidMultiBlock() returns false after this method finished processing!
     */
    public void invalidateMultiBlock();

    /**
     * This returns if this tile is actually part of an multiblock or not
     *
     * @return weather the multiblock is valid, make sure to read/write the value from/to NBT!
     */
    public boolean isValidMultiBlock();

    /**
     * This is used for saving, just like TileEntities, make sure this returns the name from the @link IMultiBlockStructure
     * HINT: Save this value to NBT aswell;
     *
     * @return the name of the structure
     */
    public String getStructureIdentifier();

    /**
     * This value should return the same value as the value in #setMultiBlock
     *
     * @return The facing of the multiblock
     */
    public ForgeDirection getMultiBlockFacing();

    /**
     * Returns the multiblock this tile belongs too, can be null
     *
     * @return said multiblock
     */
    public IMultiBlock getMultiBlock();

}
