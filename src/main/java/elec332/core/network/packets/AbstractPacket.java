package elec332.core.network.packets;

import elec332.core.ElecCore;
import elec332.core.api.network.IExtendedMessageContext;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-2-2015.
 */
public abstract class AbstractPacket extends AbstractMessage implements BiConsumer<AbstractPacket, Supplier<IExtendedMessageContext>> {

    public AbstractPacket() {
        super((NBTTagCompound) null);
    }

    public AbstractPacket(Supplier<NBTTagCompound> dataSupplier) {
        super(dataSupplier);
    }

    public AbstractPacket(NBTTagCompound b) {
        super(b);
    }

    @Override
    public void accept(AbstractPacket abstractPacket, Supplier<IExtendedMessageContext> extendedMessageContext) {
        IExtendedMessageContext messageContext = extendedMessageContext.get();
        ElecCore.tickHandler.registerCall(() -> onMessageThreadSafe(abstractPacket.networkPackageObject, messageContext), messageContext.getSide());
    }

    public abstract void onMessageThreadSafe(NBTTagCompound message, IExtendedMessageContext ctx);

}
