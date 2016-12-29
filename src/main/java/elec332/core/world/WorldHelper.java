package elec332.core.world;

import elec332.core.main.ElecCore;
import elec332.core.network.packets.PacketReRenderBlock;
import elec332.core.server.ServerHelper;
import elec332.core.util.NBTHelper;
import elec332.core.util.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 20-3-2015.
 */
@SuppressWarnings("unused")
public class WorldHelper {

    public static Biome getBiome(World world, BlockPos pos){
        return world.getBiomeGenForCoords(pos);
    }

    public static IBlockState getBlockStateForPlacement(Block block, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, @Nullable EnumHand hand) {
        return block.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    public static boolean spawnEntityInWorld(World world, Entity entity){
        return world.spawnEntityInWorld(entity);
    }

    public static boolean canBlockBePlaced(World world, Block block, BlockPos pos, boolean b, EnumFacing facing, @Nullable Entity entity){
        return world.canBlockBePlaced(block, pos, b, facing, entity, null);
    }

    public static void notifyNeighborsOfStateChange(World world, BlockPos pos, Block block){
        world.notifyNeighborsOfStateChange(pos, block);
    }

    public static ChunkPos chunkCoordFromBlockPos(BlockPos pos){
        return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static long longFromBlockPos(BlockPos pos){
        return ChunkPos.chunkXZ2Int(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static long longFromChunk(Chunk chunk){
        return longFromChunkXZ(chunk.getChunkCoordIntPair());
    }

    public static long longFromChunkXZ(ChunkPos chunkCoordIntPair){
        return ChunkPos.chunkXZ2Int(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
    }

    public static void reRenderBlock(TileEntity tile){
        if (!tile.getWorld().isRemote){
            ServerHelper.instance.sendMessageToAllPlayersWatchingBlock(tile.getWorld(), tile.getPos(), new PacketReRenderBlock(tile), ElecCore.networkHandler);
        } else {
            WorldHelper.markBlockForRenderUpdate(tile.getWorld(), tile.getPos());
        }
    }

    public static void setBlockState(World world, BlockPos pos, IBlockState state, int flags){
        world.setBlockState(pos, state, flags);
    }

    public static void markBlockForUpdate(World world, BlockPos pos){
        if (!world.isRemote){
            ((WorldServer)world).getPlayerChunkMap().markBlockForUpdate(pos);
        } else {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
        //world.markBlockForUpdate(pos);
    }

    public static boolean chunkLoaded(World world, BlockPos pos){
        Chunk chunk = world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4);
        return chunk == null || chunk.isLoaded();
    }

    public static void markBlockForRenderUpdate(World world, BlockPos pos){
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public static void spawnExplosion(World worldObj, int xCoord, int yCoord, int zCoord, float force){
        worldObj.createExplosion(null, xCoord, yCoord, zCoord, force*4, true);
    }

    public static ForgeChunkManager.Ticket requestTicket(World world, BlockPos loc, Object modInstance){
        ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(modInstance, world, ForgeChunkManager.Type.NORMAL);
        if (ticket != null){
            new NBTHelper(ticket.getModData()).addToTag(loc);
        }
        return ticket;
    }

    public static ChunkPos fromBlockLoc(BlockPos loc){
        return new ChunkPos(loc.getX() >> 4, loc.getZ() >> 4);
    }

    public static void forceChunk(ForgeChunkManager.Ticket ticket){
        ForgeChunkManager.forceChunk(ticket, fromBlockLoc(new NBTHelper(ticket.getModData()).getPos()));
    }

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory){
        for (int i = 0; i < inventory.getSlots(); i++) {
            dropStack(worldIn, pos, inventory.getStackInSlot(i));
        }
    }


    public static void dropStack(World world, BlockPos blockLoc, ItemStack stack){
        dropStack(world, blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(), stack);
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack itemStack){
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")){
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, itemStack);
            entityitem.setDefaultPickupDelay();
            WorldHelper.spawnEntityInWorld(world, entityitem);
        }
    }

    public static void scheduleBlockUpdate(World world, BlockPos blockLoc){
        world.scheduleUpdate(blockLoc, getBlockAt(world, blockLoc), 1);
    }

    @SuppressWarnings("all")
    public static int getDimID(World world){
        if (world == null) {
            throw new IllegalArgumentException("Cannot fetch the Dimension-ID from a null world!");
        }
        if (world.provider == null){
            for (Integer i : DimensionManager.getIDs()){
                if (DimensionManager.getWorld(i) == world)
                    return i;
            }
            throw new RuntimeException("Unable to determine the dimension of world: "+ world);
        }
        return world.provider.getDimension();
    }

    public static int getBlockMeta(IBlockAccess world, BlockPos blockLoc){
        return getBlockMeta(getBlockState(world, blockLoc));
    }

    public static int getBlockMeta(IBlockState state){
        return state.getBlock().getMetaFromState(state);
    }

    public static TileEntity getTileAt(IBlockAccess world, BlockPos loc){
        return world.getTileEntity(loc);
    }

    public static Block getBlockAt(IBlockAccess world, BlockPos loc){
        return getBlockState(world, loc).getBlock();
    }

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos){
        return world.getBlockState(pos);
    }

    public static void spawnLightningAtLookVec(EntityPlayer player, Double range){
        RayTraceResult position = PlayerHelper.getPosPlayerIsLookingAt(player, range);
        spawnLightningAt(player.getEntityWorld(), position.getBlockPos());
    }

    public static void spawnLightningAt(World world, BlockPos blockPos){
        spawnLightningAt(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static void spawnLightningAt(World world, double x, double y, double z){
        //world.pl(x, y, z,"ambient.weather.thunder", 10000.0F, 0.8F);
        //world.playSoundEffect(x, y, z,"random.explode", 10000.0F, 0.8F);
        WorldHelper.spawnEntityInWorld(world, new EntityLightningBolt(world, x, y, z, false));
    }
}
