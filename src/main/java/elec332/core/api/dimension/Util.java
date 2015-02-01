package elec332.core.api.dimension;

import elec332.core.api.dimension.teleporter.Teleporter;
import elec332.core.api.dimension.teleporter.Teleporter_NoPortal;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by Elec332 on 24-1-2015.
 */
class Util {
    public static void TPPlayerToDim(EntityPlayerMP thePlayer, Block frame, PortalBlock portal, int DimID){
        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, DimID, new Teleporter(thePlayer.mcServer.worldServerForDimension(DimID), portal, frame));
    }

    public static void TPPlayerToDim(EntityPlayerMP thePlayer, int DimID){
        thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, DimID, new Teleporter_NoPortal(thePlayer.mcServer.worldServerForDimension(DimID)));
    }
}
