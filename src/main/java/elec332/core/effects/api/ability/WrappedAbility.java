package elec332.core.effects.api.ability;

import elec332.core.effects.api.ElecCoreAbilitiesAPI;
import elec332.core.effects.api.util.AbilityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 27-9-2015.
 *
 * This basically does the same as @link net.minecraft.potion.PotionEffect
 */
public final class WrappedAbility {

    public WrappedAbility(Ability ability, int duration){
        this(ability, duration, 1);
    }

    public WrappedAbility(Ability ability, int duration, int strength){
        this.ability = ability;
        this.strength = Math.min(strength, ability.getMaxLevel());
        this.duration = duration;
        if (ability.isInstant())
            this.duration = 1;
    }

    private final Ability ability;
    private int strength, duration;

    public final void onActivated(EntityLivingBase entity){
        ability.onEffectAddedToEntity(entity, this);
    }

    public final void onRemoved(EntityLivingBase entity){
        ability.onEffectRemovedFromEntity(entity, this);
    }

    public final void onUpdate(EntityLivingBase entity){
        ability.updateEffectOnEntity(entity, this);
        duration--;
        if (duration <= 0){
            AbilityHelper.removeEffectFromEntity(entity, this);
        }
    }

    public void applyTo(EntityLivingBase entity){
        AbilityHelper.addEffectToEntity(entity, copyOf());
    }

    public WrappedAbility copyOf(){
        return new WrappedAbility(ability, duration, strength);
    }

    public Ability getAbility() {
        return ability;
    }

    public int getStrength() {
        return strength;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isInstant() {
        return ability.isInstant();
    }

    public final void writeToNBT(NBTTagCompound tagCompound){
        tagCompound.setString("ability", ability.getName());
        tagCompound.setInteger("strength", strength);
        tagCompound.setInteger("duration", duration);
    }

    public void mergeWith(WrappedAbility otherAbility){
        if (otherAbility == null || otherAbility.ability != ability)
            return;
        duration = otherAbility.duration;
        strength = otherAbility.strength;
    }

    public static WrappedAbility readEffectFromNBT(NBTTagCompound tagCompound){
        String s = tagCompound.getString("ability");
        Ability ability = ElecCoreAbilitiesAPI.getApi().getEffectFromName(s);
        if (ability != null){
            return new WrappedAbility(ability, tagCompound.getInteger("duration"), tagCompound.getInteger("strength"));
        }
        return null;
    }
}
