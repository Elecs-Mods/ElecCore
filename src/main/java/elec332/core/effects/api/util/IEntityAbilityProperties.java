package elec332.core.effects.api.util;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.ability.WrappedAbility;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Created by Elec332 on 27-9-2015.
 */
public interface IEntityAbilityProperties extends IExtendedEntityProperties {

    public void updateEffects();

    public void addEffect(WrappedAbility effect);

    public void removeEffect(WrappedAbility ability);

    public void removeEffect(Ability ability);

    public WrappedAbility getEffect(Ability ability);

    public WrappedAbility getEffect(String effect);

    public void readFromPacket(IAbilityPacket packet);

    @Override
    public void saveNBTData(NBTTagCompound compound);

    @Override
    public void loadNBTData(NBTTagCompound compound);

    @Override
    public void init(Entity entity, World world);

}
