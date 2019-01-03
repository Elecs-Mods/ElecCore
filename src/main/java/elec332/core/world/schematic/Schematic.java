package elec332.core.world.schematic;

import com.google.common.collect.Maps;
import elec332.core.api.structure.ISchematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

/*
 * This was originally created by Lumaceon, rewritten by Elec332.
 * You can find the original here: https://github.com/Lumaceon/ClockworkPhase2/blob/master/src/main/java/lumaceon/mods/clockworkphase2/util/SchematicUtility.java
 *
 * Default schematic implementation
 */
public class Schematic implements ISchematic {

    protected final NBTTagList tileDataList;
    private short width, height, length, horizon; //Horizon sets the "ground" or how far down to translate this.
    protected IBlockState[] blocks;
    private Map<BlockPos, NBTTagCompound> tiles;

    protected Schematic(NBTTagList tileEntities, short width, short height, short length, short horizon, IBlockState[] blocks) {
        this.tileDataList = tileEntities;
        this.width = width;
        this.height = height;
        this.length = length;
        this.horizon = horizon;
        this.blocks = blocks;
        tiles = Maps.newHashMap();
        reloadTileMap();
    }

    /**
     * Reloads the tile data in the schematic.
     */
    private void reloadTileMap() {
        tiles.clear();
        for (int i = 0; i < tileDataList.size(); i++) {
            NBTTagCompound tag = tileDataList.getCompound(i).copy();
            BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            tiles.put(pos, tag);
        }
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
    @Override
    public Block getBlock(int x, int y, int z) {
        return blocks[getIndexFromCoordinates(x, y, z)].getBlock();
    }

    /**
     * Gets the data for the tile entity at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The data from the tile entity located at the specified local schematic coordinates, or null if not found.
     */
    @Override
    public NBTTagCompound getTileData(int x, int y, int z, int worldX, int worldY, int worldZ) {
        NBTTagCompound tag = getTileData(x, y, z);
        if (tag != null) {
            tag.putInt("x", worldX);
            tag.putInt("y", worldY);
            tag.putInt("z", worldZ);
            return tag;
        }
        return null;
    }

    /**
     * Gets the data for the tile entity at the specified coordinates, ranging from 0 to the width (x), height (y) and length (z).
     * 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The data from the tile entity located at the specified local schematic coordinates, or null if not found.
     */
    @Override
    public NBTTagCompound getTileData(int x, int y, int z) {
        NBTTagCompound tag = tiles.get(new BlockPos(x, y, z));
        if (tag != null) {
            return tag.copy();
        }
        return null;
    }

    /**
     * Gets the metadata for the block at the specified coordinates, ranging from 0 to the width (x), height (y) and
     * length (z). 0 is inclusive, the width, height and length values are exclusive.
     *
     * @param x Coordinate x within the local schematic coordinates.
     * @param y Coordinate y within the local schematic coordinates.
     * @param z Coordinate z within the local schematic coordinates.
     * @return The metadata for the block at the specified local schematic coordinates.
     */
    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        return blocks[getIndexFromCoordinates(x, y, z)];
    }

    @Override
    public short getBlockLength() {
        return length;
    }

    @Override
    public short getBlockWidth() {
        return width;
    }

    @Override
    public short getBlockHeight() {
        return height;
    }

    @Override
    public short getHorizon() {
        return horizon;
    }

    /**
     * Get the index for the block based on the local schematic coordinates.
     */
    private int getIndexFromCoordinates(int x, int y, int z) {
        return y * width * length + z * width + x;
    }

}
