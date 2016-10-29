package elec332.core.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by Elec332 on 1-8-2015.
 */
public abstract class AbstractMessage implements IMessage {

    public AbstractMessage(NBTTagCompound b){
        this.networkPackageObject = b;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkPackageObject = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.networkPackageObject);
    }

    public NBTTagCompound networkPackageObject;
}
