package elec332.core.api.network;

import net.minecraft.network.PacketBuffer;

/**
 * Created by Elec332 on 2-1-2019
 */
public interface IMessage {

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    public void fromBytes(PacketBuffer buf);

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    public void toBytes(PacketBuffer buf);

}
