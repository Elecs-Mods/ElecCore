package elec332.core.world.schematic;

import com.google.common.collect.Maps;
import elec332.core.main.ElecCore;
import elec332.core.util.FileHelper;
import elec332.core.util.NBT;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

import java.io.*;
import java.util.Map;

/*
 * This was originally created by Lumaceon, rewritten by Elec332.
 * You can find the original here: https://github.com/Lumaceon/ClockworkPhase2/blob/master/src/main/java/lumaceon/mods/clockworkphase2/util/SchematicUtility.java
 */
public class SchematicHelper {

    public static final SchematicHelper INSTANCE = new SchematicHelper();

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
            return loadSchematic(FileHelper.getFromResource(rl));
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
            NBTTagList tileEntities = nbt.getTagList("TileEntities", NBT.NBTData.COMPOUND.getID());
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            short horizon = nbt.getShort("Horizon");
            NBTTagList blockData = nbt.getTagList("BlockData", NBT.NBTData.COMPOUND.getID());
            byte[] metadata = nbt.getByteArray("Data");
            int[] blockArray = nbt.getIntArray("Blocks");
            Block[] blocks = new Block[width * height * length];
            Map<Integer, Block> idMap = Maps.newHashMap();
            idMap.put(-1, Blocks.air);
            for (int i = 0; i < blockData.tagCount(); i++) {
                NBTTagCompound tag = blockData.getCompoundTagAt(i);
                idMap.put(tag.getInteger("p"), GameData.getBlockRegistry().getObject(new ResourceLocation(tag.getString("m"), tag.getString("b"))));
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

    public NBTTagCompound writeSchematic(Schematic schematic) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setShort("Width", schematic.width);
        nbt.setShort("Height", schematic.height);
        nbt.setShort("Length", schematic.length);
        nbt.setShort("Horizon", schematic.horizon);
        nbt.setByteArray("Data", schematic.data);
        nbt.setTag("TileEntities", schematic.tileDataList);
        int[] blocks = new int[schematic.blocks.length];
        NBTTagList blockData = new NBTTagList();
        Map<Block, Integer> map = Maps.newHashMap();
        int id = 0;
        for (int i = 0; i < schematic.blocks.length; i++) {
            Block block = schematic.blocks[i];
            if (block != Blocks.air) {
                if (!map.containsKey(block)) {
                    NBTTagCompound tag = new NBTTagCompound();
                    int ID = id++;
                    tag.setInteger("p", ID);
                    map.put(block, ID);
                    ResourceLocation rl = GameData.getBlockRegistry().getNameForObject(block);
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
     * @param area     The area in theWorld which will be saved as a .modschematic file.
     */
    public Schematic createModSchematic(World world, Area area, short horizon) {
        if (world == null || area == null)
            return null;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setShort("Width", area.getWidth());
        nbt.setShort("Height", area.getHeight());
        nbt.setShort("Length", area.getLength());
        nbt.setShort("Horizon", horizon);
        Block[] blocks = new Block[area.getBlockCount()];
        byte[] data = new byte[area.getBlockCount()];
        NBTTagList tileList = new NBTTagList();
        int i = 0;

        int schematicY = 0;
        for (int y = Math.min(area.y1, area.y2); y <= Math.max(area.y1, area.y2); y++) {
            int schematicZ = 0;
            for (int z = Math.min(area.z1, area.z2); z <= Math.max(area.z1, area.z2); z++) {
                int schematicX = 0;
                for (int x = Math.min(area.x1, area.x2); x <= Math.max(area.x1, area.x2); x++) {
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

        return new Schematic(tileList, area.getWidth(), area.getHeight(), area.getLength(), horizon, data, blocks);
    }

}
