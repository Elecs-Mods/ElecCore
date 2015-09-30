package elec332.core.effects.api;

import elec332.core.effects.api.ability.Ability;

/**
 * Created by Elec332 on 27-9-2015.
 */
public interface IElecCoreAbilitiesAPI {

    public void registerEffect(Ability ability);

    public Ability getEffectFromName(String name);

    public void requestActivation();

    public boolean willBeActivated();

}
