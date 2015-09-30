package elec332.core.effects;

import com.google.common.collect.Lists;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.ability.WrappedAbility;
import elec332.core.effects.api.util.IEntityAbilityProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Elec332 on 27-9-2015.
 */
public final class EntityAbilityProperties implements IEntityAbilityProperties {

    public EntityAbilityProperties(){
        activeEffects = Lists.newArrayList();
        toRemove = Lists.newArrayList();
    }

    private EntityLivingBase entity;
    private List<WrappedAbility> activeEffects;
    private List<WrappedAbility> toRemove;

    public void updateEffects(){
        checkRemove();
        for (WrappedAbility effect : activeEffects){
            effect.onUpdate(entity);
        }
    }

    public void addEffect(WrappedAbility effect){
        if (effect != null) {
            WrappedAbility q = getEffect(effect.getAbility());
            if (q != null && q.getDuration() > 0){
                q.mergeWith(effect);
                return;
            }
            activeEffects.add(effect);
            effect.onActivated(entity);
        }
    }

    public void removeEffect(WrappedAbility ability){
        removeEffect(ability.getAbility());
    }

    public void removeEffect(Ability ability){
        _removeEffect(getEffect(ability));
    }

    private void _removeEffect(WrappedAbility effect){
        if (effect != null){
            toRemove.add(effect);
            effect.onRemoved(entity);
        }
    }

    public WrappedAbility getEffect(Ability ability){
        return getEffect(ability.getName());
    }

    public WrappedAbility getEffect(String effect){
        for (WrappedAbility wrappedAbility : activeEffects){
            if (wrappedAbility.getAbility().getName().equals(effect))
                return wrappedAbility;
        }
        return null;
    }


    @Override
    public void saveNBTData(NBTTagCompound compound) {
        checkRemove();
        NBTTagList list = new NBTTagList();
        for (WrappedAbility effect : activeEffects){
            if (effect != null) {
                NBTTagCompound toSave = new NBTTagCompound();
                effect.writeToNBT(toSave);
                list.appendTag(toSave);
            }
        }
        compound.setTag("activeEffects", list);
    }


    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("activeEffects", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            activeEffects.add(WrappedAbility.readEffectFromNBT(list.getCompoundTagAt(i)));
        }
    }


    @Override
    public void init(Entity entity, World world) {
        this.entity = (EntityLivingBase) entity;
    }

    private void checkRemove(){
        if (!toRemove.isEmpty()){
            for (WrappedAbility ability : toRemove){
                activeEffects.remove(ability);
            }
            toRemove.clear();
        }
    }

}
