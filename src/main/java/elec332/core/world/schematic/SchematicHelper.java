package elec332.core.world.schematic;

import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.structure.ISchematic;
import elec332.core.api.util.Area;
import elec332.core.util.NBTTypes;
import elec332.core.util.RegistryHelper;
import elec332.core.util.ResourceHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*
 * This was originally created by Lumaceon, rewritten by Elec332.
 * You can find the original here: https://github.com/Lumaceon/ClockworkPhase2/blob/master/src/main/java/lumaceon/mods/clockworkphase2/util/SchematicUtility.java
 */
public enum SchematicHelper {

    INSTANCE;

    /**
     * Loads a schematic from a file
     *
     * @param file The file
     * @return The schematic loaded from the specified file
     */
    public Schematic loadSchematic(File file) {
        try {
            return loadSchematic(new FileInputStream(file));
        } catch (IOException e) {
            ElecCore.logger.fatal("Error loading schematic at: " + file);
            ElecCore.logger.fatal(e);
            return null;
        }
    }

    /**
     * Loads a schematic from a {@link ResourceLocation}
     *
     * @param rl The Resource location
     * @return The schematic loaded from the specified {@link ResourceLocation}
     */
    public Schematic loadSchematic(ResourceLocation rl) {
        try {
            return loadSchematic(ResourceHelper.getInputStreamFromResource(rl));
        } catch (IOException e) {
            ElecCore.logger.fatal("Error loading schematic at: " + rl);
            ElecCore.logger.fatal(e);
            return null;
        }
    }

    /**
     * Loads a schematic from a {@link InputStream}
     *
     * @param is The inputstream
     * @return The schematic loaded from the specified {@link InputStream}
     */
    public Schematic loadSchematic(InputStream is) {
        try {
            CompoundNBT tag = CompressedStreamTools.readCompressed(is);
            is.close();
            return loadModSchematic(tag);
        } catch (Exception e) {
            ElecCore.logger.fatal("Error loading schematic.");
            ElecCore.logger.fatal(e);
            return null;
        }
    }

    /**
     * Loads a schematic from NBT
     *
     * @param nbt The NBT data
     * @return The loaded ModSchematic.
     */
    public Schematic loadModSchematic(CompoundNBT nbt) {
        try {
            ListNBT tileEntities = nbt.getList("TileEntities", NBTTypes.COMPOUND.getID());
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            short horizon = nbt.getShort("Horizon");
            ListNBT blockData = nbt.getList("BlockData", NBTTypes.COMPOUND.getID());
            int[] blockArray = nbt.getIntArray("Blocks");
            BlockState[] blocks = new BlockState[width * height * length];
            Map<Integer, BlockState> idMap = Maps.newHashMap();
            idMap.put(-1, Blocks.AIR.getDefaultState());
            for (int i = 0; i < blockData.size(); i++) {
                CompoundNBT tag = blockData.getCompound(i);
                idMap.put(tag.getInt("p"), NBTUtil.readBlockState(tag.getCompound("ibs")));
            }
            for (int i = 0; i < blockArray.length; i++) {
                blocks[i] = idMap.get(blockArray[i]);
            }
            return new Schematic(tileEntities, width, height, length, horizon, blocks);
        } catch (Exception ex) {
            ElecCore.logger.fatal("Error loading ModSchematic.");
            ElecCore.logger.fatal(ex);
            return null;
        }
    }

    /**
     * Writes a schematic to NBT
     *
     * @param schematic_ The schematic
     * @return The specified schematic serialized to NBT
     */
    public CompoundNBT writeSchematic(ISchematic schematic_) {
        Schematic schematic = wrap(schematic_);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putShort("Width", schematic.getBlockWidth());
        nbt.putShort("Height", schematic.getBlockHeight());
        nbt.putShort("Length", schematic.getBlockLength());
        nbt.putShort("Horizon", schematic.getHorizon());
        nbt.put("TileEntities", schematic.tileDataList);
        int[] blocks = new int[schematic.blocks.length];
        ListNBT blockData = new ListNBT();
        Map<BlockState, Integer> map = Maps.newHashMap();
        int id = 0;
        for (int i = 0; i < schematic.blocks.length; i++) {
            BlockState block = schematic.blocks[i];
            if (block != Blocks.AIR.getDefaultState()) {
                if (!map.containsKey(block)) {
                    ResourceLocation rl = RegistryHelper.getBlockRegistry().getKey(block.getBlock());
                    if (rl == null) {
                        blocks[i] = -1;
                        continue;
                    }
                    CompoundNBT tag = new CompoundNBT();
                    int ID = id++;
                    tag.putInt("p", ID);
                    map.put(block, ID);
                    tag.put("ibs", NBTUtil.writeBlockState(block));
                    blockData.add(tag);
                }
                blocks[i] = map.get(block);
            } else {
                blocks[i] = -1;
            }
        }
        nbt.putIntArray("Blocks", blocks);
        nbt.put("BlockData", blockData);
        return nbt;
    }

    /**
     * Creates a Schematic from the specified area.
     *
     * @param world   The world
     * @param area    The area in the World from which a schematic will be created.
     * @param horizon The horizon, AKA offset down
     */
    public Schematic createModSchematic(IBlockReader world, Area area, short horizon) {
        if (world == null || area == null) {
            return null;
        }
        BlockState[] blocks = new BlockState[area.getBlockCount()];
        ListNBT tileList = new ListNBT();
        int i = 0;

        int schematicY = 0;
        for (int y = area.minY; y <= area.maxY; y++) {
            int schematicZ = 0;
            for (int z = area.minZ; z <= area.maxZ; z++) {
                int schematicX = 0;
                for (int x = area.minX; x <= area.maxX; x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    blocks[i] = WorldHelper.getBlockState(world, pos);
                    TileEntity tile = WorldHelper.getTileAt(world, pos);
                    if (tile != null) {
                        CompoundNBT tileNBT = new CompoundNBT();
                        tile.write(tileNBT);
                        tileNBT.putInt("x", schematicX);
                        tileNBT.putInt("y", schematicY);
                        tileNBT.putInt("z", schematicZ);
                        tileList.add(tileNBT);
                    }
                    ++i;
                    schematicX++;
                }
                schematicZ++;
            }
            schematicY++;
        }

        return new Schematic(tileList, area.getBlockWidth(), area.getBlockHeight(), area.getBlockHeight(), horizon, blocks);
    }

    /**
     * Wraps a generic {@link ISchematic} into a version which can be used by the serializer.
     * Inefficient, usage of the default implementation is recommended
     */
    @SuppressWarnings("all")
    public Schematic wrap(ISchematic schematic) {
        if (schematic instanceof Schematic) {
            return (Schematic) schematic;
        }
        return createModSchematic(new IBlockReader() {

            @Nullable
            @Override
            public TileEntity getTileEntity(BlockPos pos) {
                CompoundNBT tag = schematic.getTileData(pos.getX(), pos.getY(), pos.getZ());
                if (tag != null) {
                    TileEntity.create(tag);
                }
                return null;
            }

            @Override
            public BlockState getBlockState(BlockPos pos) {
                return schematic.getBlockState(pos);
            }

            @Override
            public IFluidState getFluidState(BlockPos blockPos) {
                return null;
            }

        }, new Area(0, 0, 0, schematic.getBlockWidth() - 1, schematic.getBlockHeight() - 1, schematic.getBlockLength() - 1), schematic.getHorizon());
    }

}
