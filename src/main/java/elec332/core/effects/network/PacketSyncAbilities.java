package elec332.core.effects.network;

import elec332.core.effects.api.ability.WrappedAbility;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.effects.api.util.IAbilityPacket;
import elec332.core.effects.api.util.IEntityAbilityProperties;
import elec332.core.network.packets.AbstractPacket;
import elec332.core.util.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 27-9-2015.
 */
@SuppressWarnings("unused")
public class PacketSyncAbilities extends AbstractPacket implements IAbilityPacket{

    public PacketSyncAbilities(){
    }

    public PacketSyncAbilities(EntityLivingBase entity, NBTTagCompound tagCompound, PacketType type){
        super(new NBTHelper(tagCompound).addToTag(entity.getEntityId(), "EntityDataIDToSender").addToTag(type.toString(), "type").serializeNBT());
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket message, MessageContext ctx) {
        IEntityAbilityProperties prop = AbilityHelper.getHandler((EntityLivingBase)Minecraft.getMinecraft().theWorld.getEntityByID(message.networkPackageObject.getInteger("EntityDataIDToSender")));
        ability = WrappedAbility.readEffectFromNBT(message.networkPackageObject);
        packetType = IAbilityPacket.PacketType.valueOf(message.networkPackageObject.getString("type"));
        if (ability == null || packetType == null)
            throw new RuntimeException();
        prop.readFromPacket(this);
        return null;
    }

    private WrappedAbility ability;
    private PacketType packetType;

    @Override
    public WrappedAbility getAbility() {
        return ability;
    }

    @Override
    public PacketType getPacketType() {
        return packetType;
    }

}
