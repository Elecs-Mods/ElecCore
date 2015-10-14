package elec332.core.effects;

import com.google.common.collect.Lists;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.ability.WrappedAbility;
import elec332.core.effects.api.util.IAbilityPacket;
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
    private World world;
    protected List<WrappedAbility> activeEffects;
    private List<WrappedAbility> toRemove;


    public void updateEffects(){
        for (WrappedAbility effect : activeEffects){
            effect.onUpdate(entity);
        }
        checkRemove();
    }

    public void addEffect(WrappedAbility effect){
        _addEffect(effect, true);
    }

    private void _addEffect(WrappedAbility ability, boolean add){
        if (ability != null) {
            if (!world.isRemote){
                sendPacketAbility(ability, IAbilityPacket.PacketType.ADD);
            }
            WrappedAbility q = getEffect(ability.getAbility());
            if (q != null && q.getDuration() > 0){
                q.mergeWith(ability);
                return;
            }
            activeEffects.add(ability);
            if (add)
                ability.onActivated(entity);
        }
    }

    public void removeEffect(WrappedAbility ability){
        removeEffect(ability.getAbility());
    }

    public void removeEffect(Ability ability){
        if (!world.isRemote){
            sendPacketAbility(new WrappedAbility(ability, 0), IAbilityPacket.PacketType.REMOVE);
        }
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
    public void readFromPacket(IAbilityPacket packet) {
        switch (packet.getPacketType()){
            case ADD:
                addEffect(packet.getAbility());
                break;
            case SYNC:
                _addEffect(packet.getAbility(), false);
                break;
            case REMOVE:
                removeEffect(packet.getAbility());
                break;
            default:
                throw new IllegalArgumentException();
        }
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
        activeEffects.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            activeEffects.add(WrappedAbility.readEffectFromNBT(list.getCompoundTagAt(i)));
        }
    }


    @Override
    public void init(Entity entity, World world) {
        this.entity = (EntityLivingBase) entity;
        this.world = world;
    }

    private void checkRemove(){
        if (!toRemove.isEmpty()){
            for (WrappedAbility ability : toRemove){
                activeEffects.remove(ability);
            }
            toRemove.clear();
        }
    }

    private void sendPacketAbility(WrappedAbility ability, IAbilityPacket.PacketType packetType){
        AbilityHandler.instance.syncAbilityDataToClient(entity, ability, packetType);
    }

}
