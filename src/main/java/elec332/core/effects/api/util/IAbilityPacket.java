package elec332.core.effects.api.util;

import elec332.core.effects.api.ability.WrappedAbility;

/**
 * Created by Elec332 on 14-10-2015.
 */
public interface IAbilityPacket {

    public WrappedAbility getAbility();

    public PacketType getPacketType();

    public enum PacketType{
        ADD, REMOVE, SYNC
    }
}
