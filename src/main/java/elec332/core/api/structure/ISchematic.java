package elec332.core.api.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by Elec332 on 11-11-2015.
 */
public interface ISchematic {

    /**
     * Gets the block at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The blocks located at the specified local schematic coordinates, or null if out of bounds.
     */
    public Block getBlock(int x, int y, int z);

    /**
     * Gets the data for the tile entity at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The data from the tile entity located at the specified local schematic coordinates, or null if not found.
     */
    public NBTTagCompound getTileData(int x, int y, int z, int worldX, int worldY, int worldZ);

    /**
     * Gets the data for the tile entity at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The data from the tile entity located at the specified local schematic coordinates, or null if not found.
     */
    public NBTTagCompound getTileData(int x, int y, int z);

    /**
     * Gets the metadata for the block at the specified coordinates, ranging from 0 to the width (x), height (y) and
     * length (z). 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The metadata for the block at the specified local schematic coordinates.
     */
    public byte getMetadata(int x, int y, int z);

    public interface IModSchematic extends ISchematic {

        /**
         * Gets the blocks that are missing in the MC instance for this structure.
         *
         * @return A list of the missing blocks
         */
        public List<String> getMissingBlocks();

    }
}
