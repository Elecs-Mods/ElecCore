package elec332.core.effects.api;

import com.google.common.collect.ImmutableList;
import elec332.core.effects.api.ability.Ability;

import java.util.List;

/**
 * Created by Elec332 on 27-9-2015.
 */
public final class ElecCoreAbilitiesAPI {

    private static final IElecCoreAbilitiesAPI api;
    public static final String PROPERTIES_NAME = "ElecCoreEffects";

    public static final String owner = "ElecCore";
    public static final String version = "#API_VER#";
    public static final String provides = "ElecCore|Abilities";

    public static IElecCoreAbilitiesAPI getApi(){
        return api;
    }

    static {
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
