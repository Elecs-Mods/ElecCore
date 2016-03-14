package elec332.core.effects.api;

import com.google.common.collect.ImmutableList;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.IEntityAbilityProperties;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Elec332 on 27-9-2015.
 */
public final class ElecCoreAbilitiesAPI {

    private static final IElecCoreAbilitiesAPI api;
    public static final String PROPERTIES_NAME = "ElecCoreEffects";

    public static final String owner = "ElecCore";
    public static final String version = "#API_VER#";
    public static final String provides = "ElecCore|Abilities";

    @CapabilityInject(IEntityAbilityProperties.class)
    public static Capability<IEntityAbilityProperties> ABILITIES_CAPABILITY;

    public static IElecCoreAbilitiesAPI getApi(){
        return api;
    }

    static {
        CapabilityManager.INSTANCE.register(IEntityAbilityProperties.class, new Capability.IStorage<IEntityAbilityProperties>() {

            @Override
            public NBTBase writeNBT(Capability<IEntityAbilityProperties> capability, IEntityAbilityProperties instance, EnumFacing side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<IEntityAbilityProperties> capability, IEntityAbilityProperties instance, EnumFacing side, NBTBase nbt) {
                throw new UnsupportedOperationException();
            }

        }, new Callable<IEntityAbilityProperties>() {

            @Override
            public IEntityAbilityProperties call() throws Exception {
                throw new UnsupportedOperationException();
            }

        });
        IElecCoreAbilitiesAPI a;
        try {
            a = (IElecCoreAbilitiesAPI) Class.forName("elec332.core.effects.AbilityHandler").getField("instance").get(null);
        } catch (Exception e){
            System.out.println("[ElecCoreAbilitiesAPI] Error loading API handler, abilities will not work!");
            a = new NullAPI();
        }
        api = a;
    }

    private static class NullAPI implements IElecCoreAbilitiesAPI {

        private NullAPI(){
        }

        @Override
        public void registerEffect(Ability ability) {
        }

        @Override
        public Ability getEffectFromName(String name) {
            return null;
        }

        @Override
        public void requestActivation() {
        }

        @Override
        public boolean willBeActivated() {
            return false;
        }

        @Override
        public boolean isRegistered(Ability ability) {
            return false;
        }

        @Override
        public List<Ability> getRegisteredAbilities() {
            return ImmutableList.of();
        }

    }

}
