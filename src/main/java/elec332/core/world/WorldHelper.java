package elec332.core.world;

import elec332.core.player.PlayerHelper;
import elec332.core.util.BlockLoc;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

    public static void spawnExplosion(World worldObj, int xCoord, int yCoord, int zCoord, float force){
        worldObj.createExplosion(null, xCoord, yCoord, zCoord, force*4, true);
    }

    public static ForgeChunkManager.Ticket requestTicket(World world, BlockLoc loc, Object modInstance){
        ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(modInstance, world, ForgeChunkManager.Type.NORMAL);
        if (ticket != null){
            loc.toNBT(ticket.getModData());
        }
        return ticket;
    }

    public static ChunkCoordIntPair fromBlockLoc(BlockLoc loc){
        return new ChunkCoordIntPair(loc.xCoord >> 4, loc.zCoord >> 4);
    }

    public static void forceChunk(ForgeChunkManager.Ticket ticket){
        ForgeChunkManager.forceChunk(ticket, fromBlockLoc(new BlockLoc(ticket.getModData())));
    }

    public static void dropStack(World world, BlockLoc blockLoc, ItemStack stack){
        dropStack(world, blockLoc.xCoord, blockLoc.yCoord, blockLoc.zCoord, stack);
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack itemStack){
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")){
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, itemStack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void scheduleBlockUpdate(World world, BlockLoc blockLoc){
        scheduleBlockUpdate(world, blockLoc.xCoord, blockLoc.yCoord, blockLoc.zCoord);
    }

    public static void scheduleBlockUpdate(World world, int x, int y, int z){
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 1);
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
        return world.provider.dimensionId;
    }

    public static int getBlockMeta(IBlockAccess world, BlockLoc blockLoc){
        return world.getBlockMetadata(blockLoc.xCoord, blockLoc.yCoord, blockLoc.zCoord);
    }

    public static TileEntity getTileAt(IBlockAccess world, BlockLoc loc){
        return world.getTileEntity(loc.xCoord, loc.yCoord, loc.zCoord);
    }

    public static Block getBlockAt(IBlockAccess world, BlockLoc loc){
        return world.getBlock(loc.xCoord, loc.yCoord, loc.zCoord);
    }

    public static void spawnLightningAtLookVec(EntityPlayer player, Double range){
        MovingObjectPosition position = PlayerHelper.getPosPlayerIsLookingAt(player, range);
        spawnLightningAt(player.worldObj, position.blockX, position.blockY, position.blockZ);
    }

    public static void spawnLightningAt(World world, double x, double y, double z){
        world.playSoundEffect(x, y, z,"ambient.weather.thunder", 10000.0F, 0.8F);
        world.playSoundEffect(x, y, z,"random.explode", 10000.0F, 0.8F);
        world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
    }
}
