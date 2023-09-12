package elec332.core.network.packets;

import elec332.core.api.network.IMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketBuffer;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 1-8-2015.
 */
public abstract class AbstractMessage implements IMessage {

    public AbstractMessage(Supplier<CompoundTag> dataSupplier) {
        this(dataSupplier.get());
    }

    public AbstractMessage(CompoundTag b) {
        this.networkPackageObject = b;
    }

    protected CompoundTag networkPackageObject;

    @Override
    public void fromBytes(PacketBuffer buf) {
        this.networkPackageObject = buf.readCompoundTag();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(this.networkPackageObject);
    }

}
