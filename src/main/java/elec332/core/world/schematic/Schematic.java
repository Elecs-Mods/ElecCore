package elec332.core.world.schematic;

import com.google.common.collect.Maps;
import elec332.core.api.structure.ISchematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;

import java.util.Map;

/*
 * These three classes were made by Lumaceon.
 * You can find them hhere: https://github.com/Lumaceon/ClockworkPhase2/blob/master/src/main/java/lumaceon/mods/clockworkphase2/util/SchematicUtility.java
 */

public class Schematic implements ISchematic {

    protected final NBTTagList tileDataList;
    public short width, height, length, horizon; //Horizon sets the "ground" or how far down to translate this.
    public byte[] data;
    public Block[] blocks;
    private Map<BlockPos, NBTTagCompound> tiles;
    public int areaBlockCount; //Does not actually count blocks, just the size of the area in blocks.

    protected Schematic(NBTTagList tileEntities, short width, short height, short length, short horizon, byte[] data, Block[] blocks) {
        this.tileDataList = tileEntities;
        this.width = width;
        this.height = height;
        this.length = length;
        this.horizon = horizon;
        this.data = data;
        this.blocks = blocks;
        areaBlockCount = width * height * length;
        tiles = Maps.newHashMap();
        reloadTileMap();
    }

    /**
     * Reloads the tile data in the schematic.
     */
    private void reloadTileMap(){
        tiles.clear();
        for (int i = 0; i < tileDataList.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound) tileDataList.getCompoundTagAt(i).copy();
            BlockPos pos = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
            tiles.put(pos, tag);
        }
    }

    public Block getBlock(BlockPos pos){
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
    public Block getBlock(int x, int y, int z){
        return blocks[getIndexFromCoordinates(x, y, z)];
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
    public NBTTagCompound getTileData(int x, int y, int z, int worldX, int worldY, int worldZ) {
        NBTTagCompound tag = getTileData(x, y, z);
        if (tag != null){
            tag.setInteger("x", worldX);
            tag.setInteger("y", worldY);
            tag.setInteger("z", worldZ);
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
    public NBTTagCompound getTileData(int x, int y, int z) {
        NBTTagCompound tag = tiles.get(new BlockPos(x, y, z));
        if (tag != null)
            return (NBTTagCompound) tag.copy();
        return null;
    }

    public byte getMetaData(BlockPos pos){
        return getMetadata(pos.getX(), pos.getY(), pos.getZ());
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
    public byte getMetadata(int x, int y, int z) {
        return data[getIndexFromCoordinates(x, y, z)];
    }

    /**
     * Gets this schematic's area based on a centered coordinate on the horizon.
     *
     * @param x The x coordinate in world-space this is to be centered on.
     * @param y The horizon coordinate in world-space this schematic's horizon is to be matched with.
     * @param z The z coordinate in world-space this is to be centered on.
     * @return The area in world space that this schematic will fill.
     */
    public Area getAreaFromWorldCoordinates(int x, int y, int z) {
        int minX, maxX, minY, maxY, minZ, maxZ;
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

    public IBlockState getState(BlockPos pos){
        return getBlock(pos).getStateFromMeta(getMetaData(pos));
    }

    /**
     * Get the index for the block based on the local schematic coordinates.
     */
    protected int getIndexFromCoordinates(int x, int y, int z) {
        return y * width * length + z * width + x;
    }

}
