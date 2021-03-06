package elec332.core.network.packets;

import elec332.core.api.network.IMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 1-8-2015.
 */
public abstract class AbstractMessage implements IMessage {

    public AbstractMessage(Supplier<CompoundNBT> dataSupplier) {
        this(dataSupplier.get());
    }

    public AbstractMessage(CompoundNBT b) {
        this.networkPackageObject = b;
    }

    protected CompoundNBT networkPackageObject;

    @Override
    public void fromBytes(PacketBuffer buf) {
        this.networkPackageObject = buf.readCompoundTag();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(this.networkPackageObject);
    }

}
