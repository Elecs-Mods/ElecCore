package elec332.core.network.impl;

import elec332.core.api.network.ElecByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Elec332 on 4-11-2016.
 */
interface DefaultByteBufFactory extends ElecByteBuf.Factory {

    @Override
    default public ElecByteBuf createByteBuf() {
        return createByteBuf(Unpooled.buffer());
    }

    @Override
    default public ElecByteBuf createByteBuf(ByteBuf parent) {
        return new ElecByteBufImpl(parent);
    }

}
