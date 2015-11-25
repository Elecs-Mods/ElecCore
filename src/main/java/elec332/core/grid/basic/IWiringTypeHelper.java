package elec332.core.grid.basic;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 3-8-2015.
 */
public interface IWiringTypeHelper {

    public boolean isReceiver(TileEntity tile);

    public boolean isTransmitter(TileEntity tile);

    public boolean isSource(TileEntity tile);

    public boolean canReceiverReceiveFrom(TileEntity tile, @Nullable EnumFacing direction);

    public boolean canTransmitterConnectTo(TileEntity transmitter, TileEntity otherTransmitter);

    public boolean canTransmitterConnectTo(TileEntity transmitter, @Nullable EnumFacing direction);

    public boolean canSourceProvideTo(TileEntity tile, @Nullable EnumFacing direction);

    public boolean isTileValid(TileEntity tile);

    public enum ConnectType{
        CONNECTOR, SEND, RECEIVE, SEND_RECEIVE
    }

}
