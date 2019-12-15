package elec332.core.network.packets;

import elec332.core.ElecCore;
import elec332.core.api.network.IExtendedMessageContext;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-2-2015.
 */
public abstract class AbstractPacket extends AbstractMessage implements BiConsumer<AbstractPacket, Supplier<IExtendedMessageContext>> {

    public AbstractPacket() {
        super((CompoundNBT) null);
    }

    public AbstractPacket(Supplier<CompoundNBT> dataSupplier) {
        super(dataSupplier);
    }

    public AbstractPacket(CompoundNBT b) {
        super(b);
    }

    @Override
    public void accept(AbstractPacket abstractPacket, Supplier<IExtendedMessageContext> extendedMessageContext) {
        IExtendedMessageContext messageContext = extendedMessageContext.get();
        ElecCore.tickHandler.registerCall(() -> onMessageThreadSafe(abstractPacket.networkPackageObject, messageContext), messageContext.getReceptionSide());
    }

    public abstract void onMessageThreadSafe(CompoundNBT message, IExtendedMessageContext ctx);

}
