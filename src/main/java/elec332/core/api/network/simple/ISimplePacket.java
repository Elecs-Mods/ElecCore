package elec332.core.api.network.simple;

import elec332.core.api.network.ElecByteBuf;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-10-2016.
 *
 * A simple packet
 */
public interface ISimplePacket {

    public void toBytes(ElecByteBuf byteBuf);

    @Nullable
    default public ISimplePacketHandler getPacketHandler() {
        return null;
    }

}
