package elec332.core.api.registration;

import net.minecraftforge.eventbus.api.GenericEvent;

/**
 * Created by Elec332 on 10-7-2020
 */
public abstract class APIInjectedEvent<T> extends GenericEvent<T> {

    public APIInjectedEvent(Class<T> type) {
        super(type);
    }

    public abstract T getInjectedAPI();

}
