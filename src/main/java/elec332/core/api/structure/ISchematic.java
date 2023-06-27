package elec332.core.api.structure;

import elec332.core.api.util.Area;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Elec332 on 11-11-2015.
 */
public interface ISchematic {

    /**
     * Gets the block at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param pos Coordinate within the local schematic coordinates.
     * @return The blocks located at the specified local schematic coordinates, or null if out of bounds.
     */
    default Block getBlock(BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Gets the block at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The blocks located at the specified local schematic coordinates, or null if out of bounds.
     */
    Block getBlock(int x, int y, int z);

    /**
     * Gets the data for the tile entity at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The data from the tile entity located at the specified local schematic coordinates, or null if not found.
     */
    CompoundNBT getTileData(int x, int y, int z, int worldX, int worldY, int worldZ);

    /**
     * Gets the data for the tile entity at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The data from the tile entity located at the specified local schematic coordinates, or null if not found.
     */
    CompoundNBT getTileData(int x, int y, int z);

    /**
     * Gets the {@link BlockState} at the specified coordinates, ranging from 0 to the width (x), height (y) and
     * length (z). 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param pos Coordinate within the local schematic coordinates.
     * @return The metadata for the block at the specified local schematic coordinates.
     */
    default BlockState getBlockState(BlockPos pos) {
        return getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Gets the {@link BlockState} at the specified coordinates, ranging from 0 to the width (x), height (y) and
     * length (z). 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The metadata for the block at the specified local schematic coordinates.
     */
    BlockState getBlockState(int x, int y, int z);

    short getBlockLength();

    short getBlockWidth();

    short getBlockHeight();

    /**
     * Horizon sets the "ground" or how far down to translate this.
     *
     * @return The horizon for this schematic.
     */
    default short getHorizon() {
        return 0;
    }

    /**
     * Gets this schematic's area based on a centered coordinate on the horizon.
     *
     * @param x The x coordinate in world-space this is to be centered on.
     * @param y The horizon coordinate in world-space this schematic's horizon is to be matched with.
     * @param z The z coordinate in world-space this is to be centered on.
     * @return The area in world space that this schematic will fill.
     */
    default Area getAreaFromWorldCoordinates(int x, int y, int z) {
        int minX, maxX, minY, maxY, minZ, maxZ;
        int width = getBlockWidth(), height = getBlockHeight(), length = getBlockLength(), horizon = getHorizon();
        if (width % 2 == 0) {
            minX = x + 1 - width / 2;
            maxX = x + width / 2;
        } else {
            minX = x - (width - 1) / 2;
            maxX = x + (width - 1) / 2;
        }

        minY = y - horizon;
        maxY = (y - horizon) + height;

        if (length % 2 == 0) {
            minZ = z + 1 - length / 2;
            maxZ = z + length / 2;
        } else {
            minZ = z - (length - 1) / 2;
            maxZ = z + (length - 1) / 2;
        }

        return new Area(minX, minY, minZ, maxX, maxY, maxZ);
    }

}
