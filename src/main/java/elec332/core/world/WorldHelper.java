package elec332.core.world;

import elec332.core.util.BlockLoc;
import elec332.core.util.NBTHelper;
import elec332.core.util.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * Created by Elec332 on 20-3-2015.
 */
public class WorldHelper {

    public static boolean chunkExists(World world, BlockPos pos){
        return world.getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
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

    public static ChunkCoordIntPair fromBlockLoc(BlockPos loc){
        return new ChunkCoordIntPair(loc.getX() >> 4, loc.getZ() >> 4);
    }

    public static void forceChunk(ForgeChunkManager.Ticket ticket){
        ForgeChunkManager.forceChunk(ticket, fromBlockLoc(new BlockLoc(ticket.getModData())));
    }

    public static void dropStack(World world, BlockPos blockLoc, ItemStack stack){
        dropStack(world, blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(), stack);
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack itemStack){
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")){
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, itemStack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void scheduleBlockUpdate(World world, BlockPos blockLoc){
        world.scheduleUpdate(blockLoc, getBlockAt(world, blockLoc), 1);
    }

    public static int getDimID(World world){
        if (world == null)
            throw new IllegalArgumentException("Cannot fetch the Dimension-ID from a null world!");
        if (world.provider == null){
            for (Integer i : DimensionManager.getIDs()){
                if (DimensionManager.getWorld(i) == world)
                    return i;
            }
            throw new RuntimeException("Unable to determine the dimension of world: "+ world);
        }
        return world.provider.getDimensionId();
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
        MovingObjectPosition position = PlayerHelper.getPosPlayerIsLookingAt(player, range);
        spawnLightningAt(player.worldObj, position.getBlockPos());
    }

    public static void spawnLightningAt(World world, BlockPos blockPos){
        spawnLightningAt(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static void spawnLightningAt(World world, double x, double y, double z){
        world.playSoundEffect(x, y, z,"ambient.weather.thunder", 10000.0F, 0.8F);
        world.playSoundEffect(x, y, z,"random.explode", 10000.0F, 0.8F);
        world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
    }
}
