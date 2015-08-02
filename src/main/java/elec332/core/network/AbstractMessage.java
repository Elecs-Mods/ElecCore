package elec332.core.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

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
