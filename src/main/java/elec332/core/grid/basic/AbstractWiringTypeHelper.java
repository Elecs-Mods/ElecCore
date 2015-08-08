package elec332.core.grid.basic;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractWiringTypeHelper {

    public abstract boolean isReceiver(TileEntity tile);

    public abstract boolean isTransmitter(TileEntity tile);

    public abstract boolean isSource(TileEntity tile);

    public abstract boolean canReceiverReceiveFrom(TileEntity tile, ForgeDirection direction);

    public abstract boolean canTransmitterConnectTo(TileEntity transmitter, TileEntity otherTransmitter);

    public abstract boolean canTransmitterConnectTo(TileEntity transmitter, ForgeDirection direction);

    public abstract boolean canSourceProvideTo(TileEntity tile, ForgeDirection direction);

    public abstract boolean isTileValid(TileEntity tile);

    public enum ConnectType{
        CONNECTOR, SEND, RECEIVE, SEND_RECEIVE
    }

}
