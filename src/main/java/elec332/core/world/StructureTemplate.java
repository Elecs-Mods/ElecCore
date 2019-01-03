package elec332.core.world;

import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.structure.GenerationType;
import elec332.core.api.structure.ISchematic;
import elec332.core.api.util.Area;
import elec332.core.world.schematic.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;

import java.util.Set;

/**
 * A class to help with the generation of schematics
 */
public class StructureTemplate {

    public StructureTemplate(Schematic schematic, GenerationType generationType, Integer... dimensions) {
        this.schematic = schematic;
        this.generationType = generationType;
        this.dimensions = Sets.newHashSet(dimensions);
    }

    public StructureTemplate(Schematic schematic, Integer... dimensions) {
        this(schematic, GenerationType.NONE, dimensions);
    }

    protected Schematic schematic;
    protected Set<Integer> dimensions;
    protected GenerationType generationType;

    /**
     * Generates a structure at the provided coordinates
     *
     * @param location The coordinates
     * @param world    The world
     */
    public void generateStructure(BlockPos location, World world) {
        if (!world.isRemote) { /* You never know what someone else might be trying to do */
            int xCoord = location.getX();
            int yCoord = location.getY();
            int zCoord = location.getZ();
            switch (generationType) {
                case SEA_LEVEL:
                    yCoord = 62;
                    break;
                case UNDERGROUND:
                    yCoord = 16;
                    break;
                case SURFACE:
                    yCoord = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, location).getY();
                    break;
                case NONE:
                    break;
            }

            if (!canSpawnInDimension(WorldHelper.getDimID(world))) {
                ElecCore.logger.info("Error, structure attempting to generate in invalid dimension!");
                return;
            }

            ISchematic schematic = getSchematic();
            Area schematicArea = schematic.getAreaFromWorldCoordinates(xCoord, yCoord, zCoord);
            int chunkX = location.getX() >> 4;
            int chunkZ = location.getZ() >> 4;

            if (schematicArea.doAreasIntersect(new Area(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 16, 255, (chunkZ << 4) + 16))) {
                for (int x = 0; x < schematic.getBlockWidth(); x++) {
                    for (int y = 0; y < schematic.getBlockHeight(); y++) {
                        for (int z = 0; z < schematic.getBlockLength(); z++) {
                            int worldX = xCoord + x;
                            int worldY = yCoord + y;
                            int worldZ = zCoord + z;
                            BlockPos schematicPos = new BlockPos(x, y, z);
                            BlockPos worldPos = new BlockPos(worldX, worldY, worldZ);
                            Block block = schematic.getBlock(schematicPos);
                            IBlockState state = schematic.getBlockState(schematicPos);

                            if (block != null) {
                                WorldHelper.setBlockState(world, worldPos, Blocks.AIR.getDefaultState(), 2);
                                if (!block.isAir(state, world, worldPos)) {
                                    WorldHelper.setBlockState(world, worldPos, state, 2);
                                    if (block.hasTileEntity(state)) {
                                        NBTTagCompound tileData = schematic.getTileData(x, y, z, worldX, worldY, worldZ);
                                        if (tileData != null) {
                                            WorldHelper.getTileAt(world, worldPos).read(tileData);
                                            WorldHelper.markBlockForUpdate(world, worldPos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = chunkX; i < chunkX + (schematicArea.getBlockWidth() >> 4); i++) {
                    for (int j = chunkZ; j < chunkZ + (schematicArea.getBlockLength() >> 4); j++) {
                        Chunk chunk = world.getChunk(chunkX, chunkZ);
                        chunk.setModified(true);
                        chunk.enqueueRelightChecks();
                    }
                }
            }
        }
    }

    public ISchematic getSchematic() {
        return schematic;
    }

    public GenerationType getGenerationType() {
        return generationType;
    }

    public Set<Integer> getDimensions() {
        return dimensions;
    }

    public boolean isDimensionRestricted() {
        return getDimensions() != null && !getDimensions().isEmpty();
    }

    public boolean canSpawnInDimension(int dimension) {
        return !isDimensionRestricted() || getDimensions().contains(dimension);
    }

}
