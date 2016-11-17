package elec332.core.world.schematic;

import com.google.common.collect.Maps;
import elec332.core.api.structure.ISchematic;
import elec332.core.api.util.Area;
import elec332.core.main.ElecCore;
import elec332.core.util.IOUtil;
import elec332.core.util.NBTTypes;
import elec332.core.util.RegistryHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

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
public enum  SchematicHelper {

    INSTANCE;

    public Schematic loadSchematic(File file){
        try {
            return loadSchematic(new FileInputStream(file));
        } catch (IOException e){
            ElecCore.logger.fatal("Error loading schematic at: "+file);
            ElecCore.logger.fatal(e);
            return null;
        }
    }

    public Schematic loadSchematic(ResourceLocation rl){
        try {
            return loadSchematic(IOUtil.getFromResource(rl));
        } catch (IOException e){
            ElecCore.logger.fatal("Error loading schematic at: "+rl);
            ElecCore.logger.fatal(e);
            return null;
        }
    }

    public Schematic loadSchematic(InputStream is){
        try {
            NBTTagCompound tag = CompressedStreamTools.readCompressed(is);
            is.close();
            return loadModSchematic(tag);
        } catch (Exception e){
            ElecCore.logger.fatal("Error loading schematic.");
            ElecCore.logger.fatal(e);
            return null;
        }
    }

    /**
     * Loads a .modschematic file and returns a ModSchematic class. Does not load .schematic files.
     *
     * @return The loaded ModSchematic.
     */
    public Schematic loadModSchematic(NBTTagCompound nbt){
        try {
            NBTTagList tileEntities = nbt.getTagList("TileEntities", NBTTypes.COMPOUND.getID());
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            short horizon = nbt.getShort("Horizon");
            NBTTagList blockData = nbt.getTagList("BlockData", NBTTypes.COMPOUND.getID());
            byte[] metadata = nbt.getByteArray("Data");
            int[] blockArray = nbt.getIntArray("Blocks");
            Block[] blocks = new Block[width * height * length];
            Map<Integer, Block> idMap = Maps.newHashMap();
            idMap.put(-1, Blocks.AIR);
            for (int i = 0; i < blockData.tagCount(); i++) {
                NBTTagCompound tag = blockData.getCompoundTagAt(i);
                idMap.put(tag.getInteger("p"), RegistryHelper.getBlockRegistry().getObject(new ResourceLocation(tag.getString("m"), tag.getString("b"))));
            }
            for (int i = 0; i < blockArray.length; i++) {
                blocks[i] = idMap.get(blockArray[i]);
            }
            return new Schematic(tileEntities, width, height, length, horizon, metadata, blocks);
        } catch (Exception ex) {
            ElecCore.logger.fatal("Error loading ModSchematic.");
            ElecCore.logger.fatal(ex);
            return null;
        }
    }

    public NBTTagCompound writeSchematic(ISchematic schematic_) {
        Schematic schematic = wrap(schematic_);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setShort("Width", schematic.getBlockWidth());
        nbt.setShort("Height", schematic.getBlockHeight());
        nbt.setShort("Length", schematic.getBlockLength());
        nbt.setShort("Horizon", schematic.getHorizon());
        nbt.setByteArray("Data", schematic.data);
        nbt.setTag("TileEntities", schematic.tileDataList);
        int[] blocks = new int[schematic.blocks.length];
        NBTTagList blockData = new NBTTagList();
        Map<Block, Integer> map = Maps.newHashMap();
        int id = 0;
        for (int i = 0; i < schematic.blocks.length; i++) {
            Block block = schematic.blocks[i];
            if (block != Blocks.AIR) {
                if (!map.containsKey(block)) {
                    NBTTagCompound tag = new NBTTagCompound();
                    int ID = id++;
                    tag.setInteger("p", ID);
                    map.put(block, ID);
                    ResourceLocation rl = RegistryHelper.getBlockRegistry().getNameForObject(block);
                    tag.setString("m", rl.getResourceDomain());
                    tag.setString("b", rl.getResourcePath());
                    blockData.appendTag(tag);
                }
                blocks[i] = map.get(block);
            } else {
                blocks[i] = -1;
            }
        }
        nbt.setIntArray("Blocks", blocks);
        nbt.setTag("BlockData", blockData);
        return nbt;
    }

    /**
     * Creates a Schematic.
     *
     * @param area     The area in the World which will be saved as a .modschematic file.
     */
    public Schematic createModSchematic(IBlockAccess world, Area area, short horizon) {
        if (world == null || area == null) {
            return null;
        }
        Block[] blocks = new Block[area.getBlockCount()];
        byte[] data = new byte[area.getBlockCount()];
        NBTTagList tileList = new NBTTagList();
        int i = 0;

        int schematicY = 0;
        for (int y = area.minY; y <= area.maxY; y++) {
            int schematicZ = 0;
            for (int z = area.minZ; z <= area.maxZ; z++) {
                int schematicX = 0;
                for (int x = area.minX; x <= area.maxX; x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    blocks[i] = WorldHelper.getBlockAt(world, pos);
                    data[i] = (byte) WorldHelper.getBlockMeta(world, pos);
                    TileEntity tile = WorldHelper.getTileAt(world, pos);
                    if (tile != null) {
                        NBTTagCompound tileNBT = new NBTTagCompound();
                        tile.writeToNBT(tileNBT);
                        tileNBT.setInteger("x", schematicX);
                        tileNBT.setInteger("y", schematicY);
                        tileNBT.setInteger("z", schematicZ);
                        tileList.appendTag(tileNBT);
                    }
                    ++i;
                    schematicX++;
                }
                schematicZ++;
            }
            schematicY++;
        }

        return new Schematic(tileList, area.getBlockWidth(), area.getBlockHeight(), area.getBlockHeight(), horizon, data, blocks);
    }

    @SuppressWarnings("all")
    public Schematic wrap(ISchematic schematic){
        if (schematic instanceof Schematic){
            return (Schematic) schematic;
        }
        return createModSchematic(new IBlockAccess() {

            @Nullable
            @Override
            public TileEntity getTileEntity(BlockPos pos) {
                NBTTagCompound tag = schematic.getTileData(pos.getX(), pos.getY(), pos.getZ());
                if (tag != null){
                    TileEntity.create(null, tag);
                }
                return null;
            }

            @Override
            public int getCombinedLight(BlockPos pos, int lightValue) {
                throw new UnsupportedOperationException();
            }

            @Override
            public IBlockState getBlockState(BlockPos pos) {
                return schematic.getBlockState(pos);
            }

            @Override
            public boolean isAirBlock(BlockPos pos) {
                throw new UnsupportedOperationException();
            }


            @Override
            public Biome getBiome(BlockPos pos) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getStrongPower(BlockPos pos, EnumFacing direction) {
                throw new UnsupportedOperationException();
            }

            @Override
            public WorldType getWorldType() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
                throw new UnsupportedOperationException();
            }

        }, new Area(0, 0, 0, schematic.getBlockWidth() - 1, schematic.getBlockHeight() - 1, schematic.getBlockLength() - 1), schematic.getHorizon());
    }

}
