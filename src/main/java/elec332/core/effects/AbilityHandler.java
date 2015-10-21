package elec332.core.effects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import elec332.core.effects.api.ElecCoreAbilitiesAPI;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.IElecCoreAbilitiesAPI;
import elec332.core.effects.api.ability.WrappedAbility;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.effects.api.util.IAbilityPacket;
import elec332.core.effects.defaultabilities.*;
import elec332.core.effects.network.PacketSyncAbilities;
import elec332.core.main.ElecCore;
import elec332.core.server.ServerHelper;
import elec332.core.util.EventHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 27-9-2015.
 */
public final class AbilityHandler implements IElecCoreAbilitiesAPI {

    public static final AbilityHandler instance = new AbilityHandler();
    private AbilityHandler(){
        effectMap = Maps.newHashMap();
        shouldActivate = false;
    }

    private Map<String, Ability> effectMap;
    private boolean shouldActivate;

    @Override
    public void registerEffect(Ability ability) {
        if (ability == null)
            throw new IllegalArgumentException("You cannot register a null ability!");
        if (effectMap.containsKey(ability.getName()))
            throw new IllegalArgumentException("There is already a registered ability with ID: "+ ability.getName());
        effectMap.put(ability.getName(), ability);
    }

    @Override
    public Ability getEffectFromName(String name) {
        return effectMap.get(name);
    }

    @Override
    public void requestActivation() {
        shouldActivate = true;
    }

    @Override
    public boolean willBeActivated() {
        if (!Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
            throw new IllegalAccessError();
        return shouldActivate;
    }

    @Override
    public boolean isRegistered(Ability ability) {
        if (ability == null){
            return false;
        }
        for (Map.Entry<String, Ability> entry : effectMap.entrySet()){
            if (entry.getKey().equals(ability.getName()) && entry.getValue() == ability){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Ability> getRegisteredAbilities(){
        return ImmutableList.copyOf(effectMap.values());
    }

    public void init(){
        if (shouldActivate) {
            EventHelper.registerHandlerForge(new EffectsTickHandler());
            EventHelper.registerHandlerForge(new EffectsSyncHandler());
            initDefaultAbilities();
            ElecCore.networkHandler.registerClientPacket(PacketSyncAbilities.class);
        }
    }

    public void syncAbilityDataToClient(EntityLivingBase entity, WrappedAbility ability, IAbilityPacket.PacketType packetType){
        NBTTagCompound toSend = new NBTTagCompound();
        ability.writeToNBT(toSend);
        for (EntityPlayerMP player : ServerHelper.instance.getAllPlayersWatchingBlock(entity.worldObj, (int) entity.posX, (int) entity.posZ)) {
            ElecCore.networkHandler.getNetworkWrapper().sendTo(new PacketSyncAbilities(entity, toSend, packetType), player);
        }
    }

    public static class EffectsTickHandler{

        @SubscribeEvent
        public void updateEntity(LivingEvent.LivingUpdateEvent event){
            IExtendedEntityProperties prop = event.entityLiving.getExtendedProperties(ElecCoreAbilitiesAPI.PROPERTIES_NAME);
            if (prop != null){
                ((EntityAbilityProperties) prop).updateEffects();
            }
        }

    }

    public static class EffectsSyncHandler{

        @SubscribeEvent
        public void onEntityConstructing(EntityEvent.EntityConstructing event){
            if (event.entity instanceof EntityLivingBase)
                event.entity.registerExtendedProperties(ElecCoreAbilitiesAPI.PROPERTIES_NAME, new EntityAbilityProperties());
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            syncEffects(event.player);
        }

        @SubscribeEvent
        public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            syncEffects(event.player);
        }

        @SubscribeEvent
        public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
            syncEffects(event.player);
        }

        private void syncEffects(EntityLivingBase entity){
            EntityAbilityProperties properties = (EntityAbilityProperties) AbilityHelper.getHandler(entity);
            if (properties == null)
                throw new RuntimeException();
            for (WrappedAbility ability : properties.activeEffects){
                AbilityHandler.instance.syncAbilityDataToClient(entity, ability, IAbilityPacket.PacketType.SYNC);
            }
        }

    }

    private void initDefaultAbilities(){
        registerEffect(new Climb());
        registerEffect(new FireResistance());
        registerEffect(new Flight());
        registerEffect(new Invisibility());
        registerEffect(new Jump());
        registerEffect(new PowerStrike());
        registerEffect(new QuickStrike());
        registerEffect(new QuickSwimming());
        registerEffect(new WaterBreathing());
    }
}
