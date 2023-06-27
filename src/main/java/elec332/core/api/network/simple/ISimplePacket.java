package elec332.core.api.network.simple;

import elec332.core.api.network.ElecByteBuf;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-10-2016.
 * <p>
 * A simple packet
 */
public interface ISimplePacket {

    void toBytes(ElecByteBuf byteBuf);

    @Nullable
    default ISimplePacketHandler getPacketHandler() {
        return null;
    }

}
