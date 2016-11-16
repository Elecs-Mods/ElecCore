package elec332.core.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.main.ElecCore;
import elec332.core.network.packets.AbstractMessage;
import elec332.core.server.ServerHelper;
import elec332.core.util.EnumHelper;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import elec332.core.world.location.BlockStateWrapper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 26-7-2015.
 */
public final class MultiBlockStructureRegistry implements IMessageHandler<MultiBlockStructureRegistry.SyncMultiBlockPacket, IMessage>{

    protected MultiBlockStructureRegistry(MultiBlockRegistry multiBlockRegistry){
        this.multiBlockStructures = Maps.newHashMap();
        this.multiBlockRegistry = multiBlockRegistry;
        multiBlockRegistry.networkHandler.registerPacket(this, SyncMultiBlockPacket.class, Side.CLIENT);
    }

    private final MultiBlockRegistry multiBlockRegistry;

    protected void registerMultiBlockStructure(IMultiBlockStructure multiBlock, String name){
        multiBlockStructures.put(name, multiBlock);
        //System.out.println("Registered structure with name: "+name);
    }

    private Map<String, IMultiBlockStructure> multiBlockStructures;

    protected String getIdentifier(IMultiBlockStructure structure){
        for (Map.Entry<String, IMultiBlockStructure> entry : multiBlockStructures.entrySet()){
            if (entry.getValue().equals(structure)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("ERROR: Structure: "+structure.getClass().getName()+" is not registered!");
    }

    public boolean attemptCreate(EntityPlayer player, World world, BlockPos pos, EnumFacing side){
        if (world.isRemote)
            return false;
        BlockStateWrapper blockData = atLocation(world, pos);
        if (blockData == null)
            return false;
        for (IMultiBlockStructure multiBlock : multiBlockStructures.values()){
            if (multiBlock.getTriggerBlock().equals(blockData)){
                if (multiBlock instanceof AbstractAdvancedMultiBlockStructure && !((AbstractAdvancedMultiBlockStructure) multiBlock).canCreate((EntityPlayerMP) player)){
                    continue;
                }
                if (tryCreateStructure(multiBlock, world, pos, side, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean attemptReCreate(String s, TileEntity tile, EnumFacing facing){
        return tryCreateStructure(multiBlockStructures.get(s), tile.getWorld(), tile.getPos(), facing, true);
    }

    private boolean tryCreateStructure(IMultiBlockStructure multiBlock, World world, BlockPos pos, EnumFacing side, boolean recreate){
        if (side == null || side == EnumFacing.UP || side == EnumFacing.DOWN)
            return false;
        BlockStateWrapper leftBottomCorner = null;
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        int newX = x;
        int newY = y;
        int newZ = z;
        BlockStateWrapper temp1;
        //for (BlockStateWrapper data : multiBlock.getStructure().getBlockTypes())
        //    System.out.println(data.toString());
        xLoop:
        for (int i = 0; i < multiBlock.getStructure().getHn()+1; i++) {
            if (side == EnumFacing.NORTH || side == EnumFacing.EAST) {
                temp1 = atLocation(world, newX + 1, y, z);
                //System.out.println("ScanningX_NE: "+newX+","+newY+","+newZ+" contains: "+temp1);
                if (temp1 != null && multiBlock.getStructure().getBlockTypes().contains(temp1)) {
                    newX++;
                    continue;
                }
            } else if (side == EnumFacing.SOUTH || side == EnumFacing.WEST){
                temp1 = atLocation(world, newX - 1, y, z);
                //System.out.println("ScanningX_SW: "+newX+","+newY+","+newZ+" contains: "+temp1);
                if (temp1 != null && multiBlock.getStructure().getBlockTypes().contains(temp1)) {
                    newX--;
                    continue;
                }
            }
            for (int j = 0; j < multiBlock.getStructure().getHn() + 1; j++) {
                newY--;
                temp1 = atLocation(world, newX, newY, z);
                //System.out.println("ScanningY: "+newX+","+newY+","+newZ+" contains: "+temp1);
                if (temp1 != null && multiBlock.getStructure().getBlockTypes().contains(temp1))
                    continue;
                newY++;
                for (int k = 0; k < multiBlock.getStructure().getHn() + 1; k++) {
                    if (side == EnumFacing.NORTH || side == EnumFacing.WEST) {
                        temp1 = atLocation(world, newX, newY, newZ - 1);
                        //System.out.println("ScanningZ_NW: "+newX+","+newY+","+newZ+" contains: "+temp1);
                        if (temp1 != null && multiBlock.getStructure().getBlockTypes().contains(temp1)) {
                            newZ--;
                            continue;
                        }
                    } else if (side == EnumFacing.EAST || side == EnumFacing.SOUTH){
                        temp1 = atLocation(world, newX, newY, newZ + 1);
                        //System.out.println("ScanningZ_ES: "+newX+","+newY+","+newZ+" contains: "+temp1);
                        if (temp1 != null && multiBlock.getStructure().getBlockTypes().contains(temp1)) {
                            newZ++;
                            continue;
                        }
                    }
                    //newZ++; //Works, but why??
                    //System.out.println("BottomLeft = "+newX+","+newY+","+newZ);
                    leftBottomCorner = atLocation(world, newX, newY, newZ);
                    //System.out.println("Scanning: "+newX+","+newY+","+newZ+" is corner: "+leftBottomCorner);
                    break xLoop;
                }
            }
        }
        if (leftBottomCorner != null && areBlocksAtRightPlace(multiBlock.getStructure(), world, newX, newY, newZ, side)){
            if (multiBlock instanceof AbstractAdvancedMultiBlockStructure && !((AbstractAdvancedMultiBlockStructure) multiBlock).areSecondaryConditionsMet(world, new BlockPos(newX, newY, newZ), side))
                return false;
            if (multiBlock.replaceUponCreated() != null) {
                BlockStructure main = multiBlock.getStructure();
                replaceAll(main, world, newX, newY, newZ, side, main.newBlockStructureWithSameDimensions(multiBlock.replaceUponCreated()));
            }
            //newZ--;
            multiBlockRegistry.get(world).createNewMultiBlock(multiBlock, new BlockPos(newX, newY, newZ), getAllMultiBlockLocations(multiBlock.getStructure(), newX, newY, newZ, side), world, side);
            if (!recreate && !world.isRemote){
                for (EntityPlayerMP player : ServerHelper.instance.getAllPlayersWatchingBlock(world, newX, newZ))
                    multiBlockRegistry.networkHandler.sendTo(new SyncMultiBlockPacket(multiBlock, x, y, z, side, this), player);
            }
            int hn = multiBlock.getStructure().getHn();
            world.markBlockRangeForRenderUpdate(newX, newY, newZ, newX + hn, newY + hn, newZ + hn);
            return true;
        }
        return false;
    }

    private List<BlockPos> getAllMultiBlockLocations(final BlockStructure multiBlock, final int x, final int y, final int z, final EnumFacing side){
        final List<BlockPos> ret = Lists.newArrayList();
        multiBlock.startLoop(new BlockStructure.IPositionCall() {
            @Override
            public void forPos(int length, int width, int height) {
                ret.add(getTranslated(x, y, z, side, length, width, height));
            }
        });
        return ret;
    }

    private void replaceAll(BlockStructure multiBlock, World world, int x, int y, int z, EnumFacing side, BlockStructure toReplace){
        replaceAll(multiBlock, world, new BlockPos(x, y, z), side, toReplace);
    }

    private void replaceAll(final BlockStructure multiBlock, final World world, final BlockPos pos, final EnumFacing side, final BlockStructure toReplace){
        multiBlock.startLoop(new BlockStructure.IPositionCall() {
            @Override
            public void forPos(int length, int width, int height) {
                BlockPos loc = getTranslated(pos, side, length, width, height);
                BlockStateWrapper data = toReplace.getStructure()[length][width][height];
                world.setBlockToAir(loc);
                world.setBlockState(loc, data.getBlockState(), 3);
            }
        });
    }

    private boolean areBlocksAtRightPlace(final BlockStructure multiBlock, final World world, final int x, final int y, final int z, final EnumFacing side){
        try {
            multiBlock.startLoop(new BlockStructure.IPositionCall() {
                @Override
                public void forPos(int length, int width, int height) {
                    BlockPos translated = getTranslated(x, y, z, side, length, width, height);
                    BlockStateWrapper bsw = multiBlock.getStructure()[length][width][height];
                    if (!WorldHelper.chunkLoaded(world, translated) || bsw != null && !bsw.equals(atLocation(world, translated)) || hasMultiBlock(translated, world)){
                        System.out.println("Checking MB failed at relative: "+length+", "+width+", "+height);
                        System.out.println("Found: "+atLocation(world, translated)+", expected: "+multiBlock.getStructure()[length][width][height]);
                        throw new RuntimeException("INVALID");
                    }
                }
            });
        } catch (RuntimeException e){
            System.out.println("INVALID");
            if (e.getMessage() != null && e.getMessage().equals("INVALID"))
                return false;
            throw new RuntimeException(e);
        }
        System.out.println("VALID STUFF HERE, YAYZZZ :D");
        return true;
    }

    private boolean hasMultiBlock(BlockPos loc, World world){
        TileEntity tile = WorldHelper.getTileAt(world, loc);
        if (tile instanceof IMultiBlockTile){
            if (((IMultiBlockTile) tile).getMultiBlock() != null)
                return true;
        }
        return false;
    }

    protected static BlockPos getTranslated(BlockPos pos, EnumFacing side, int length, int width, int height){
        return getTranslated(pos.getX(), pos.getY(), pos.getZ(), side, length, width, height);
    }

    protected static BlockPos getTranslated(int x, int y, int z, EnumFacing side, int length, int width, int height){
        int newX;
        int newY;
        int newZ;
        if (side == EnumFacing.NORTH){
            newX = x - length;
            newZ = z + width;
        } else if (side == EnumFacing.EAST){
            newX = x - width;
            newZ = z - length;
        } else if (side == EnumFacing.SOUTH){
            newX = x + length;
            newZ = z - width;
        } else if (side == EnumFacing.WEST){
            newX = x + width;
            newZ = z + length;
        } else throw new IllegalArgumentException("Cannot process side: "+side);
        newY = y + height;
        //newZ--; //Fix for that weird stuff above
        return new BlockPos(newX, newY, newZ);
    }

    private BlockStateWrapper atLocation(IBlockAccess world, int x, int y, int z){
        return atLocation(world, new BlockPos(x, y, z));
    }

    private BlockStateWrapper atLocation(IBlockAccess world, BlockPos pos){
        Block block = WorldHelper.getBlockAt(world, pos);
        int meta = WorldHelper.getBlockMeta(world, pos);
        if (block == null || block == Blocks.AIR)
            return new BlockStateWrapper((Block)null);
        return new BlockStateWrapper(block, meta);
    }

    public static final class SyncMultiBlockPacket extends AbstractMessage {

        @SuppressWarnings("unused")
        public SyncMultiBlockPacket(){
            super(null);
        }

        private SyncMultiBlockPacket(IMultiBlockStructure multiBlock, int x, int y, int z, EnumFacing side, MultiBlockStructureRegistry structureRegistry){
            super(new NBTHelper().addToTag(x, "x").addToTag(y, "y").addToTag(z, "z").addToTag(side, "side").addToTag(structureRegistry.getIdentifier(multiBlock), "mbs").serializeNBT());
        }

    }

    @Override
    public IMessage onMessage(final SyncMultiBlockPacket message, final MessageContext ctx) {
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                NBTTagCompound tag = message.networkPackageObject;
                tryCreateStructure(multiBlockStructures.get(tag.getString("mbs")), ElecCore.proxy.getClientWorld(), new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")), EnumHelper.fromString(tag.getString("side"), EnumFacing.class), false);
            }
        }, ctx.side);
        return null;
    }

}
